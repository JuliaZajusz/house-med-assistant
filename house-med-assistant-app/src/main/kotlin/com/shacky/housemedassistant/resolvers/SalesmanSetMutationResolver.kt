package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Distance
import com.shacky.housemedassistant.entity.Path
import com.shacky.housemedassistant.entity.SalesmanSet
import com.shacky.housemedassistant.repository.SalesmanSetRepository
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ThreadLocalRandom

@Component
class SalesmanSetMutationResolver(private val salesmanSetRepository: SalesmanSetRepository,
                                  val salesmanSetQueryResolver: SalesmanSetQueryResolver,
                                  val coordinateMutationResolver: CoordinateMutationResolver,
                                  val coordinateQueryResolver: CoordinateQueryResolver,
                                  val distanceMutationResolver: DistanceMutationResolver,
                                  val distanceQueryResolver: DistanceQueryResolver,
                                  val pathMutationResolver: PathMutationResolver
) : GraphQLMutationResolver {
    fun newSalesmanSet(coordinates: List<Coordinate>): SalesmanSet? {
        var newCoordinates: MutableList<Coordinate> = mutableListOf();
        for (item in coordinates) {
            if (!item.id.isEmpty()) {
                val itemById = coordinateQueryResolver.findById(item.id)
                newCoordinates.add(itemById)
            } else {
                newCoordinates.add(coordinateMutationResolver.newCoordinate(item.location))
            }
        }
        var salesmanSet = salesmanSetQueryResolver.getSalesmanSetByCoordinates(newCoordinates)
        if (salesmanSet == null) {
            val neighborhoodMatrix = calcNeighborhoodMatrix(newCoordinates)
            salesmanSet = SalesmanSet(newCoordinates, neighborhoodMatrix)
            salesmanSet.id = UUID.randomUUID().toString()
            salesmanSet.paths = mutableListOf(findFirstPath(salesmanSet))
            salesmanSetRepository.save(salesmanSet)
        }
        return salesmanSet
    }

    fun updateSalesmanSet(salesmanSet: SalesmanSet): SalesmanSet {
        val oldSalesmanSet = salesmanSetRepository.findById(salesmanSet.id).get();
        val updatedSet = salesmanSet
        if (oldSalesmanSet.places != updatedSet.places) {
            println("rożne")
            var newCoordinates: MutableList<Coordinate> = mutableListOf();
            for (item in updatedSet.places) {
                if (!item.id.isEmpty()) {
                    val itemById = coordinateQueryResolver.findById(item.id)
                    newCoordinates.add(itemById)
                } else {
                    newCoordinates.add(coordinateMutationResolver.newCoordinate(item.location))
                }
            }
            updatedSet.neighborhoodMatrix = calcNeighborhoodMatrix(newCoordinates)
            salesmanSet.paths = mutableListOf(findFirstPath(salesmanSet))
        }
        return salesmanSetRepository.save(updatedSet)
//        return salesmanSetRepository.insert(updatedSet)
    }

    fun upgradeSalesmanSet(id: String, timeInSec: Int, populationSize: Int = 200, parentPopulationSize: Int = 20): SalesmanSet {
        findBestPathUsingGeneticAlgorythm(id, timeInSec, populationSize, parentPopulationSize)
        return salesmanSetQueryResolver.findById(id);
    }

    fun doSomethingElse(): Float {
        for (i in 0..10) {
            println("doSomethingElse $i")
            Thread.sleep(1000)
        }
        return 11.10f
    }

    fun newSalesmanSetByDistance(numberOfCoordinates: Int, distances: List<Float>): SalesmanSet? {
        var newCoordinates: MutableList<Coordinate> = mutableListOf();
        for (i in 0 until numberOfCoordinates) {
            val long = 89.0f + i.toFloat() / numberOfCoordinates
            val lat = 89.0f + i.toFloat() / numberOfCoordinates
            newCoordinates.add(coordinateMutationResolver.newCoordinate(listOf(long, lat)))
        }
        val neighborhoodMatrix = createNeighborhoodMatrixByGivedDistances(newCoordinates, distances)
        var salesmanSet = salesmanSetQueryResolver.getSalesmanSetByDistances(neighborhoodMatrix)
        if (salesmanSet == null) {
            salesmanSet = SalesmanSet(newCoordinates, neighborhoodMatrix)
            salesmanSet.id = UUID.randomUUID().toString()
            salesmanSetRepository.save(salesmanSet)
        }
        return salesmanSet
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
                neighborhoodMatrix.add(distanceMutationResolver.newDistance(item1.location, item2.location, distance))
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
                    distanceMutationResolver.getOrCreateDistanceByCoordinates(item1, item2)
                }
                neighborhoodMatrix.add(distance)
            }
        }
        return neighborhoodMatrix
    }

    fun findFirstPath(salesmanSet: SalesmanSet): Path {
        val visited: MutableList<String> = mutableListOf();
        val pathPlaces: MutableList<Coordinate> = mutableListOf();

        val places = salesmanSet.places
        val neighborhoodMatrix = salesmanSet.neighborhoodMatrix
        var startElementIndex = 0;

        if (places.isNotEmpty()) {
            pathPlaces.add(salesmanSet.places[startElementIndex])
            visited.add(salesmanSet.places[startElementIndex].id)
        }

        for (i in places.indices) {
            val startPlace = places[startElementIndex]
            val pathElement = neighborhoodMatrix.filter { distance ->
                distance.startCoordinateId.equals(startPlace.id) && !visited.contains(distance.endCoordinateId)
            }.minBy { distance -> distance.value }
            if (pathElement != null) {
                val secondPlace: Coordinate = places.first { coordinate -> coordinate.id.equals(pathElement.endCoordinateId) }
                pathPlaces.add(secondPlace)
                visited.add(pathElement.endCoordinateId)
                startElementIndex = places.indexOf(secondPlace)
            }
        }
        val pathValue: Float = pathMutationResolver.calcPathValue(pathPlaces)
        val path: Path = Path(pathPlaces, pathValue);
        return path;
    }

    fun findGreedyPath(id: String, startCoordinateId: String = ""): Path {
        val salesmanSet = salesmanSetRepository.findById(id);
        val visited: MutableList<String> = mutableListOf();
        val pathPlaces: MutableList<Coordinate> = mutableListOf();

        if (salesmanSet.isPresent) {
            val places = salesmanSet.get().places
            val neighborhoodMatrix = salesmanSet.get().neighborhoodMatrix
            var startElement: Coordinate = places[0];
            var startElementIndex = 0;
            if (startCoordinateId.isNotEmpty()) {
                startElement = coordinateQueryResolver.findById(startCoordinateId)
                startElementIndex = places.indexOf(startElement)
            }

            if (places.isNotEmpty()) {
                pathPlaces.add(salesmanSet.get().places[startElementIndex])
                visited.add(salesmanSet.get().places[startElementIndex].id)
            }

            for (i in places.indices) {
                val startPlace = places[startElementIndex]
                val pathElement = neighborhoodMatrix.filter { distance ->
                    distance.startCoordinateId.equals(startPlace.id) && !visited.contains(distance.endCoordinateId)
                }.minBy { distance -> distance.value }
                if (pathElement != null) {
                    val secondPlace: Coordinate = places.first { coordinate -> coordinate.id.equals(pathElement.endCoordinateId) }
                    pathPlaces.add(secondPlace)
                    visited.add(pathElement.endCoordinateId)
                    startElementIndex = places.indexOf(secondPlace)
                }
            }
        }
        val pathValue: Float = pathMutationResolver.calcPathValue(pathPlaces)
        val path: Path = Path(pathPlaces, pathValue);
        return path;
    }


    fun findBestPathUsingGeneticAlgorythm(id: String, timeInSec: Int, populationSize: Int, parentPopulationSize: Int): Path {
        var salesmanSet: SalesmanSet = salesmanSetQueryResolver.findById(id);
        val greedyPath = findGreedyPath(salesmanSet.id)
        if (!salesmanSet.paths.contains(greedyPath)) {
            salesmanSet.paths.add(greedyPath)
        }
        val start = System.currentTimeMillis();
        val end = start + timeInSec * 1000; // 60 seconds * 1000 ms/sec
        while (System.currentTimeMillis() < end) {
            salesmanSet = doGenetic(salesmanSet, populationSize, parentPopulationSize)
        }
        salesmanSet.paths = salesmanSet.paths.distinct().sortedBy { it.value.toFloat() }.toMutableList()
        if (salesmanSet.paths.size > 10) {
            salesmanSet.paths = salesmanSet.paths.subList(0, 10)  //to chyba nie działa
        }
//        salesmanSet.population = salesmanSet.population.distinct().toMutableList()  //desperacja
        salesmanSet.population = mutableListOf()  //desperacja
        salesmanSet.parentPopulation = mutableListOf()  //desperacja
        updateSalesmanSet(salesmanSet)
        return salesmanSet.paths.first()
    }

    fun doGenetic(salesmanSet: SalesmanSet, populationSize: Int, parentPopulationSize: Int, mutationsProbability: Int = 1, swapsInMutation: Int = 10): SalesmanSet {
        println("doGenetic")
        var newSalesmanSet = salesmanSet
        newSalesmanSet.population = mutableListOf()
        if (salesmanSet.paths.isNotEmpty()) {
            newSalesmanSet.population.add(salesmanSet.paths.first())
        }
        //create population
        var genomPath = newSalesmanSet.places
        for (i in newSalesmanSet.population.size until populationSize) {
            genomPath = getRandomGenom(genomPath)
            val value = pathMutationResolver.calcPathValue(genomPath)
            newSalesmanSet.population.add(Path(genomPath, value))
        }
        //rob
        var i = 0;
        while (true) {
            println("while true $i")
            //jesli warunek konca nie zostal osiagniety
            if (i < 10) {
                newSalesmanSet.population = newSalesmanSet.population.distinct().toMutableList()
                //        sortuj populację
                newSalesmanSet.population = newSalesmanSet.population.sortedBy { it.value.toFloat() }.toMutableList()
                //        jesli jest za duża to usuń część osobników
                if (newSalesmanSet.population.size > populationSize) {
                    newSalesmanSet.population = newSalesmanSet.population.subList(0, populationSize - 1)
                }
//        wybierz populację rodzicielską (ruletka, turniej)
                newSalesmanSet.parentPopulation = chooseParentPopulationByRouletteMethod(newSalesmanSet.population, parentPopulationSize)
//        krzyżuj
                newSalesmanSet = reproducePopulation(newSalesmanSet, mutationsProbability, swapsInMutation)
//        mutuj
//                for (i in 0 until newSalesmanSet.population.size) {
//
//                }

                i++;
            } else { //jesli zostal osiagniety
//        sortuj populację
                newSalesmanSet.population = newSalesmanSet.population.sortedBy { it.value.toFloat() } as MutableList<Path>
//        zwróc najlepszą ścieżkę (dodaj do paths)
                newSalesmanSet.paths.add(newSalesmanSet.population.first())
                break;
            }
        }
        return newSalesmanSet
    }

    private fun reproducePopulation(salesmanSet: SalesmanSet, mutationsProbability: Int, swapsInMutation: Int): SalesmanSet {
        var updatedSalesmanSet = salesmanSet
        val populationSize = salesmanSet.population.size
        val parentPopulationSize = salesmanSet.parentPopulation.size
        val parentCouples = salesmanSet.parentPopulation.size / 2;
        val pathSize = salesmanSet.places.size;
        for (i in 0 until parentCouples)
        //dla kazdej pary rodzicow utworz pare dzieci
        {
            val crossPoint1: Int = ThreadLocalRandom.current().nextInt(0, pathSize - 1)
            val crossPoint2: Int = ThreadLocalRandom.current().nextInt(crossPoint1, pathSize - 1)

            var a = crossover(salesmanSet.parentPopulation[2 * i], salesmanSet.parentPopulation[2 * i + 1], crossPoint1, crossPoint2)  //dziecko1
            var b = crossover(salesmanSet.parentPopulation[2 * i + 1], salesmanSet.parentPopulation[2 * i], crossPoint1, crossPoint2)  //dziecko2
            a = mutate(a, mutationsProbability, swapsInMutation)
            b = mutate(b, mutationsProbability, swapsInMutation)
            if (a.places.size != salesmanSet.places.size || b.places.size != salesmanSet.places.size || a.places.distinct().size < salesmanSet.places.size || b.places.distinct().size < salesmanSet.places.size) {
                println("" + crossPoint1 + " " + crossPoint2)
                throw Exception()
            }

            updatedSalesmanSet.population.add(a)
            updatedSalesmanSet.population.add(b)
        }
        return updatedSalesmanSet;
    }

    private fun crossover(parentOneGenom: Path, parentTwoGenom: Path, crossPoint1: Int, crossPoint2: Int): Path {
        val pathSize = parentTwoGenom.places.size
        val childListOfCoordinates: MutableList<Coordinate?> = parentTwoGenom.places.toMutableList()  //środek
        val checkForRepeat = mutableListOf<Coordinate>()

        for (i in crossPoint1 until crossPoint2)                //srodek
        {
            checkForRepeat.add(parentTwoGenom.places[i])
        }

        for (i in 0 until crossPoint1)                //poczatek
        {
            val zm = parentOneGenom.places[i];

            if (!checkForRepeat.contains(zm)) {
                childListOfCoordinates[i] = zm;
                checkForRepeat.add(zm);
            } else {
                childListOfCoordinates[i] = null;
            }
        }

        for (i in crossPoint2 until pathSize)            //koniec
        {
            var zm: Coordinate? = null;
            try {
                zm = parentOneGenom.places[i];
            } catch (e: IndexOutOfBoundsException) {
                println("" + parentOneGenom + parentTwoGenom + crossPoint1 + crossPoint2)
            }

            if (!checkForRepeat.contains(zm)) {
                childListOfCoordinates[i] = zm;
                if (zm != null) {
                    checkForRepeat.add(zm)
                };
            } else {
                childListOfCoordinates[i] = null;
            }
        }

        for (i in 0 until pathSize) {
            if (childListOfCoordinates[i] == null) {
                for (j in 0 until pathSize) {
                    if (!checkForRepeat.contains(parentOneGenom.places[j])) {
                        childListOfCoordinates[i] = parentOneGenom.places[j];
                        checkForRepeat.add(parentOneGenom.places[j]);
                        break;
                    }
                }

            }
        }
        val pathValue: Float = pathMutationResolver.calcPathValue(childListOfCoordinates.filterNotNull())
        val child: Path = Path(childListOfCoordinates.filterNotNull(), pathValue);
        return child;
    }

    fun mutate(genomPath: Path, mutationsProbability: Int, swapsInMutation: Int): Path {
        val chance = ThreadLocalRandom.current().nextInt(0, 100)
        val updatedGenomPath = genomPath.places.toMutableList()

        if (chance <= mutationsProbability) {
            for (i in 0 until swapsInMutation) {
                val city1 = ThreadLocalRandom.current().nextInt(0, updatedGenomPath.size)
                val city2 = ThreadLocalRandom.current().nextInt(0, updatedGenomPath.size)
                val temp = updatedGenomPath[city1];
                updatedGenomPath[city1] = updatedGenomPath[city2];
                updatedGenomPath[city2] = temp;
            }
        }
        val pathValue: Float = pathMutationResolver.calcPathValue(updatedGenomPath)
        return Path(updatedGenomPath, pathValue)
    }


    private fun getRandomGenom(genomPath: List<Coordinate>): List<Coordinate> {
        return genomPath.shuffled()
    }

    private fun chooseParentPopulationByRouletteMethod(population: List<Path>, parentPopulationSize: Int): List<Path> {
        var parentPopulation: MutableList<Path> = mutableListOf<Path>()
        var populationValueSum: Float = 0.0f;
        population.forEach { genomPath -> populationValueSum += 1 / genomPath.value.toFloat() }

        var rouletteList = mutableListOf<Float>(0.0f)
        population.forEach { genomPath ->
            val pathPercentage = (1 / genomPath.value.toFloat()) * (1 / populationValueSum) * 100
            rouletteList.add(rouletteList[rouletteList.size - 1] + pathPercentage)
        }

        for (i in 0 until parentPopulationSize) {
            val randomInteger = ThreadLocalRandom.current().nextInt(0, 100)

            loop@ for (i in 0 until rouletteList.size) {
                if (rouletteList[i] < randomInteger && (i + 1 == rouletteList.size || rouletteList[i + 1] > randomInteger)) {
                    parentPopulation.add(population[i])
                    break@loop
                }
            }

        }
        return parentPopulation
    }

}
