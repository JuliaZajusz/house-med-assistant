package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.*
import com.shacky.housemedassistant.repository.SalesmanSetRepository
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

@Component
class SalesmanSetMutationResolver(private val salesmanSetRepository: SalesmanSetRepository,
                                  val salesmanSetQueryResolver: SalesmanSetQueryResolver,
                                  val distanceMutationResolver: DistanceMutationResolver,
                                  val distanceQueryResolver: DistanceQueryResolver,
                                  val pathQueryResolver: PathQueryResolver,
                                  val tagMutationResolver: TagMutationResolver
) : GraphQLMutationResolver {
    fun newSalesmanSet(patients: List<Patient>): SalesmanSet? {
        var salesmanSet = salesmanSetQueryResolver.getSalesmanSetByPatients(patients)
        if (salesmanSet == null) {
            patients.forEach { patient ->
                patient.tags.forEach { tag ->
                    tagMutationResolver.addTag(tag)
                }
                patient
            }
            val coordinates = patients.map { patient -> patient.coordinate };
            val neighborhoodMatrix = calcNeighborhoodMatrix(coordinates)
            salesmanSet = SalesmanSet(patients, neighborhoodMatrix)
            salesmanSet.id = UUID.randomUUID().toString()
            salesmanSet.paths = mutableListOf(findFirstPath(salesmanSet))
            salesmanSetRepository.save(salesmanSet)
        }
        return salesmanSet
    }

    fun updateSalesmanSet(salesmanSet: SalesmanSet): SalesmanSet { //chyba tylko ostatnia linijka jest potrzebna
        println("updateSalesmanSet")
        val oldSalesmanSet = salesmanSetRepository.findById(salesmanSet.id).get();
        val updatedSet = salesmanSet
        if (oldSalesmanSet.places != updatedSet.places) {
            val newCoordinates = updatedSet.places.map { patient -> patient.coordinate };

            updatedSet.neighborhoodMatrix = calcNeighborhoodMatrix(newCoordinates)
            updatedSet.places.forEach { patient ->
                patient.tags.forEach { tag ->
                    tagMutationResolver.addTag(tag)
                }
                patient
            }
            salesmanSet.paths = mutableListOf(findFirstPath(salesmanSet))
        }
        return salesmanSetRepository.save(updatedSet)
    }

    fun upgradeSalesmanSet(id: String, timeInSec: Int, populationSize: Int = 200, parentPopulationSize: Int = 20): SalesmanSet {
        val salesmanSet: SalesmanSet = salesmanSetQueryResolver.findSalesmanSetById(id);
        val updatedSalesmanSet = SalesmanSetUtils(pathQueryResolver).findBestPathUsingGeneticAlgorythm(salesmanSet, timeInSec, populationSize, parentPopulationSize)
        updateSalesmanSet(updatedSalesmanSet)
        return updatedSalesmanSet;
//        return salesmanSetQueryResolver.findSalesmanSetById(id);
    }

    fun newSalesmanSetByDistance(numberOfCoordinates: Int, distances: List<Float>): SalesmanSet? {
        val patients: MutableList<Patient> = mutableListOf();
        for (i in 0 until numberOfCoordinates) {
            val long = 89.0f + i.toFloat() / numberOfCoordinates
            val lat = 89.0f + i.toFloat() / numberOfCoordinates
//            patients.add(coordinateMutationResolver.newCoordinate(listOf(long, lat)))
            patients.add(Patient(generateRandomString(5), generateRandomString(6), generateRandomString(7), Coordinate(listOf(long, lat), "coordinate-" + i), listOf()))
        }
        val newCoordinates = patients.map { patient -> patient.coordinate };
        val neighborhoodMatrix = createNeighborhoodMatrixByGivedDistances(newCoordinates, distances)
        var salesmanSet = salesmanSetQueryResolver.getSalesmanSetByDistances(neighborhoodMatrix)
        if (salesmanSet == null) {
            salesmanSet = SalesmanSet(patients, neighborhoodMatrix)
            salesmanSet.paths = mutableListOf(findFirstPath(salesmanSet))
            salesmanSet.id = UUID.randomUUID().toString()
            salesmanSetRepository.save(salesmanSet)
        }
        return salesmanSet
    }

    fun generateRandomString(length: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString = ThreadLocalRandom.current()
                .ints(length.toLong(), 0, charPool.size)
                .asSequence()
                .map(charPool::get)
                .joinToString("")
        return randomString
    }

    fun deleteSalesmanSet(id: String): Boolean {
        salesmanSetRepository.deleteById(id)
        return true
    }

    fun createNeighborhoodMatrixByGivedDistances(coordinates: List<Coordinate>, distances: List<Float>): List<Distance> {
        var neighborhoodMatrix: MutableList<Distance> = mutableListOf();
        for (item1 in coordinates) {
            for (item2 in coordinates) {
                val index = coordinates.indexOf(item1) * (coordinates.size) + coordinates.indexOf(item2)
                var distance = -1.0f
                try {
                    distance = distances[index]
                } catch (e: ArrayIndexOutOfBoundsException) {
                    println("" + index + " " + coordinates.indexOf(item1) + " " + coordinates.indexOf(item2))
                    throw ArrayIndexOutOfBoundsException()
                }

//                neighborhoodMatrix.add(distanceMutationResolver.newDistance(item1.location, item2.location, distance))
                neighborhoodMatrix.add(distanceMutationResolver.newDistance(item1, item2, distance))
//                val startCoordinateId = coordinateQueryResolver.findOneByLocation(item1.location)?.id
//                val endCoordinateId = coordinateQueryResolver.findOneByLocation(item2.location)?.id
//                if (startCoordinateId != null && endCoordinateId != null) {
//                    val element = Distance(startCoordinateId, endCoordinateId, distance)
//                    neighborhoodMatrix.add(element)
//                }
            }
        }
        return neighborhoodMatrix
    }

    fun calcNeighborhoodMatrix(coordinates: List<Coordinate>): List<Distance> {
        var neighborhoodMatrix: MutableList<Distance> = mutableListOf();
        for (item1 in coordinates) {
            for (item2 in coordinates) {
                var distance: Distance = if (item1.id.equals(item2.id)) {
                    Distance(item1.id, item2.id, 0.0f)
                } else {
//                    distanceMutationResolver.getOrCreateDistanceByCoordinates(item1, item2)
                    distanceQueryResolver.getDistanceByCoordinates(item1, item2)
                            ?: Distance(item1.id, item2.id, distanceQueryResolver.findDistanceBetweenCoordinates(item1, item2).toFloat())
                }
                neighborhoodMatrix.add(distance)
            }
        }
        return neighborhoodMatrix
    }

    //findGreedyPath jest bardzo podobna, tzrab przerefaktorować
    fun findFirstPath(salesmanSet: SalesmanSet): Path {
        val visited: MutableList<String> = mutableListOf();
        val pathPlaces: MutableList<Patient> = mutableListOf();

        val places = salesmanSet.places
        val neighborhoodMatrix = salesmanSet.neighborhoodMatrix
        var startElementIndex = 0;

        if (places.isNotEmpty()) {
            pathPlaces.add(salesmanSet.places[startElementIndex])
            visited.add(salesmanSet.places[startElementIndex].coordinate.id)
        }

        for (i in places.indices) {
            val startPlace = places[startElementIndex]
            val pathElement = neighborhoodMatrix.filter { distance ->
                distance.startCoordinateId.equals(startPlace.coordinate.id) && !visited.contains(distance.endCoordinateId)
            }.minBy { distance -> distance.value }
            if (pathElement != null) {
                val secondPlace: Patient = places.first { patient -> patient.coordinate.id.equals(pathElement.endCoordinateId) }
                pathPlaces.add(secondPlace)
                visited.add(pathElement.endCoordinateId)
                startElementIndex = places.indexOf(secondPlace)
            }
        }
        val pathValue: Float = pathQueryResolver.calcPathValue(pathPlaces.map { patient -> patient.coordinate })
        val path: Path = Path(pathPlaces, pathValue);
        return path;
    }

    //findFirstPath jest bardzo podobna, tzrab przerefaktorować
    fun findGreedyPath(id: String, startCoordinateId: String = ""): Path {
        val salesmanSet = salesmanSetRepository.findById(id);
        val visited: MutableList<String> = mutableListOf();
        val pathPlaces: MutableList<Patient> = mutableListOf();

        if (salesmanSet.isPresent) {
            val places = salesmanSet.get().places
            val neighborhoodMatrix = salesmanSet.get().neighborhoodMatrix
            var startElement: Patient = places[0];
            var startElementIndex = 0;
            if (startCoordinateId.isNotEmpty()) {
//                startElement = patientQueryResolver.patientByCoordinate(startCoordinateId).first()  //TODO, metoda zwraca listę, nie wiem czy powinna
                startElement = places.first { place -> place.coordinate.id == startCoordinateId }
                startElementIndex = places.indexOf(startElement)
            }

            if (places.isNotEmpty()) {
                pathPlaces.add(salesmanSet.get().places[startElementIndex])
                visited.add(salesmanSet.get().places[startElementIndex].coordinate.id)
            }

            for (i in places.indices) {
                val startPlace = places[startElementIndex]
                val pathElement = neighborhoodMatrix.filter { distance ->
                    distance.startCoordinateId.equals(startPlace.coordinate.id) && !visited.contains(distance.endCoordinateId)
                }.minBy { distance -> distance.value }
                if (pathElement != null) {
                    val secondPlace: Patient = places.first { patient -> patient.coordinate.id.equals(pathElement.endCoordinateId) }
                    pathPlaces.add(secondPlace)
                    visited.add(pathElement.endCoordinateId)
                    startElementIndex = places.indexOf(secondPlace)
                }
            }
        }
        val pathValue: Float = pathQueryResolver.calcPathValue(pathPlaces.map { patient -> patient.coordinate })
        val path: Path = Path(pathPlaces, pathValue);
        return path;
    }
//
//
//    fun findBestPathUsingGeneticAlgorythm(id: String, timeInSec: Int, populationSize: Int, parentPopulationSize: Int): Path {
//        var salesmanSet: SalesmanSet = salesmanSetQueryResolver.findSalesmanSetById(id);
//        val greedyPath = findGreedyPath(salesmanSet.id)
//        if (!salesmanSet.paths.contains(greedyPath)) {
//            salesmanSet.paths.add(greedyPath)
//        }
//        val start = System.currentTimeMillis();
//        val end = start + timeInSec * 1000; // 60 seconds * 1000 ms/sec
//        while (System.currentTimeMillis() < end) {
//            salesmanSet = doGenetic(salesmanSet, populationSize, parentPopulationSize)
//        }
//        salesmanSet.paths = salesmanSet.paths.distinct().sortedBy { it.value.toFloat() }.toMutableList()
//        if (salesmanSet.paths.size > 10) {
//            salesmanSet.paths = salesmanSet.paths.subList(0, 10)  //to chyba nie działa
//        }
////        salesmanSet.population = salesmanSet.population.distinct().toMutableList()  //desperacja
//        salesmanSet.population = mutableListOf()  //desperacja
//        salesmanSet.parentPopulation = mutableListOf()  //desperacja
//        updateSalesmanSet(salesmanSet)
//        return salesmanSet.paths.first()
//    }
//
//    fun doGenetic(salesmanSet: SalesmanSet, populationSize: Int, parentPopulationSize: Int, mutationsProbability: Int = 1, swapsInMutation: Int = 10): SalesmanSet {
//        println("doGenetic")
//        var newSalesmanSet = salesmanSet
//        newSalesmanSet.population = mutableListOf()
//        if (salesmanSet.paths.isNotEmpty()) {
//            newSalesmanSet.population.add(salesmanSet.paths.first())
//        }
//        //create population
//        var genomPath = newSalesmanSet.places
//        for (i in newSalesmanSet.population.size until populationSize) {
//            genomPath = getRandomGenom(genomPath)
//            val value = pathQueryResolver.calcPathValue(genomPath.map { patient -> patient.coordinate })
//            newSalesmanSet.population.add(Path(genomPath, value))
//        }
//        //rob
//        var i = 0;
//        while (true) {
//            println("while true $i")
//            //jesli warunek konca nie zostal osiagniety
//            if (i < 10) {
//                newSalesmanSet.population = newSalesmanSet.population.distinct().toMutableList()
//                //        sortuj populację
//                newSalesmanSet.population = newSalesmanSet.population.sortedBy { it.value.toFloat() }.toMutableList()
//                //        jesli jest za duża to usuń część osobników
//                if (newSalesmanSet.population.size > populationSize) {
//                    newSalesmanSet.population = newSalesmanSet.population.subList(0, populationSize - 1)
//                }
////        wybierz populację rodzicielską (ruletka, turniej)
//                newSalesmanSet.parentPopulation = chooseParentPopulationByRouletteMethod(newSalesmanSet.population, parentPopulationSize)
////        krzyżuj
//                newSalesmanSet = reproducePopulation(newSalesmanSet, mutationsProbability, swapsInMutation)
////        mutuj
////                for (i in 0 until newSalesmanSet.population.size) {
////
////                }
//
//                i++;
//            } else { //jesli zostal osiagniety
////        sortuj populację
//                newSalesmanSet.population = newSalesmanSet.population.sortedBy { it.value.toFloat() } as MutableList<Path>
////        zwróc najlepszą ścieżkę (dodaj do paths)
//                newSalesmanSet.paths.add(newSalesmanSet.population.first())
//                break;
//            }
//        }
//        return newSalesmanSet
//    }
//
//    private fun reproducePopulation(salesmanSet: SalesmanSet, mutationsProbability: Int, swapsInMutation: Int): SalesmanSet {
//        var updatedSalesmanSet = salesmanSet
//        val populationSize = salesmanSet.population.size
//        val parentPopulationSize = salesmanSet.parentPopulation.size
//        val parentCouples = salesmanSet.parentPopulation.size / 2;
//        val pathSize = salesmanSet.places.size;
//        for (i in 0 until parentCouples)
//        //dla kazdej pary rodzicow utworz pare dzieci
//        {
//            val crossPoint1: Int = ThreadLocalRandom.current().nextInt(0, pathSize - 1)
//            val crossPoint2: Int = ThreadLocalRandom.current().nextInt(crossPoint1, pathSize - 1)
//
//            var a = crossover(salesmanSet.parentPopulation[2 * i], salesmanSet.parentPopulation[2 * i + 1], crossPoint1, crossPoint2)  //dziecko1
//            var b = crossover(salesmanSet.parentPopulation[2 * i + 1], salesmanSet.parentPopulation[2 * i], crossPoint1, crossPoint2)  //dziecko2
//            a = mutate(a, mutationsProbability, swapsInMutation)
//            b = mutate(b, mutationsProbability, swapsInMutation)
//            if (a.places.size != salesmanSet.places.size || b.places.size != salesmanSet.places.size || a.places.distinct().size < salesmanSet.places.size || b.places.distinct().size < salesmanSet.places.size) {
//                println("" + crossPoint1 + " " + crossPoint2)
//                throw Exception()
//            }
//
//            updatedSalesmanSet.population.add(a)
//            updatedSalesmanSet.population.add(b)
//        }
//        return updatedSalesmanSet;
//    }
//
//    private fun crossover(parentOneGenom: Path, parentTwoGenom: Path, crossPoint1: Int, crossPoint2: Int): Path {
//        val pathSize = parentTwoGenom.places.size
//        val childListOfCoordinates: MutableList<Patient?> = parentTwoGenom.places.toMutableList()  //środek
//        val checkForRepeat = mutableListOf<Patient>()
//
//        for (i in crossPoint1 until crossPoint2)                //srodek
//        {
//            checkForRepeat.add(parentTwoGenom.places[i])
//        }
//
//        for (i in 0 until crossPoint1)                //poczatek
//        {
//            val zm = parentOneGenom.places[i];
//
//            if (!checkForRepeat.contains(zm)) {
//                childListOfCoordinates[i] = zm;
//                checkForRepeat.add(zm);
//            } else {
//                childListOfCoordinates[i] = null;
//            }
//        }
//
//        for (i in crossPoint2 until pathSize)            //koniec
//        {
//            var zm: Patient? = null;
//            try {
//                zm = parentOneGenom.places[i];
//            } catch (e: IndexOutOfBoundsException) {
//                println("" + parentOneGenom + parentTwoGenom + crossPoint1 + crossPoint2)
//            }
//
//            if (!checkForRepeat.contains(zm)) {
//                childListOfCoordinates[i] = zm;
//                if (zm != null) {
//                    checkForRepeat.add(zm)
//                };
//            } else {
//                childListOfCoordinates[i] = null;
//            }
//        }
//
//        for (i in 0 until pathSize) {
//            if (childListOfCoordinates[i] == null) {
//                for (j in 0 until pathSize) {
//                    if (!checkForRepeat.contains(parentOneGenom.places[j])) {
//                        childListOfCoordinates[i] = parentOneGenom.places[j];
//                        checkForRepeat.add(parentOneGenom.places[j]);
//                        break;
//                    }
//                }
//
//            }
//        }
//        val pathValue: Float = pathQueryResolver.calcPathValue(childListOfCoordinates.filterNotNull().map { patient -> patient.coordinate })
//        val child: Path = Path(childListOfCoordinates.filterNotNull(), pathValue);
//        return child;
//    }
//
//    fun mutate(genomPath: Path, mutationsProbability: Int, swapsInMutation: Int): Path {
//        val chance = ThreadLocalRandom.current().nextInt(0, 100)
//        val updatedGenomPath = genomPath.places.toMutableList()
//
//        if (chance <= mutationsProbability) {
//            for (i in 0 until swapsInMutation) {
//                val city1 = ThreadLocalRandom.current().nextInt(0, updatedGenomPath.size)
//                val city2 = ThreadLocalRandom.current().nextInt(0, updatedGenomPath.size)
//                val temp = updatedGenomPath[city1];
//                updatedGenomPath[city1] = updatedGenomPath[city2];
//                updatedGenomPath[city2] = temp;
//            }
//        }
//        val pathValue: Float = pathQueryResolver.calcPathValue(updatedGenomPath.map { patient -> patient.coordinate })
//        return Path(updatedGenomPath, pathValue)
//    }
//
//
//    private fun getRandomGenom(genomPath: List<Patient>): List<Patient> {
//        return genomPath.shuffled()
//    }
//
//    private fun chooseParentPopulationByRouletteMethod(population: List<Path>, parentPopulationSize: Int): List<Path> {
//        var parentPopulation: MutableList<Path> = mutableListOf<Path>()
//        var populationValueSum: Float = 0.0f;
//        population.forEach { genomPath -> populationValueSum += 1 / genomPath.value.toFloat() }
//
//        var rouletteList = mutableListOf<Float>(0.0f)
//        population.forEach { genomPath ->
//            val pathPercentage = (1 / genomPath.value.toFloat()) * (1 / populationValueSum) * 100
//            rouletteList.add(rouletteList[rouletteList.size - 1] + pathPercentage)
//        }
//
//        for (i in 0 until parentPopulationSize) {
//            val randomInteger = ThreadLocalRandom.current().nextInt(0, 100)
//
//            loop@ for (i in 0 until rouletteList.size) {
//                if (rouletteList[i] < randomInteger && (i + 1 == rouletteList.size || rouletteList[i + 1] > randomInteger)) {
//                    parentPopulation.add(population[i])
//                    break@loop
//                }
//            }
//
//        }
//        return parentPopulation
//    }

}
