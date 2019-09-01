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
            salesmanSetRepository.save(salesmanSet)
        }
        return salesmanSet
    }

    fun deleteSalesmanSet(id: String): Boolean {
        salesmanSetRepository.deleteById(id)
        return true
    }

    fun calcNeighborhoodMatrix(coordinates: List<Coordinate>): List<Distance> {
        var neighborhoodMatrix: MutableList<Distance> = mutableListOf();
        for (item1 in coordinates) {
            for (item2 in coordinates) {
                var distance = 0.0;
                if (!item1.id.equals(item2.id)) {
                    distance = coordinateQueryResolver.findDistanceBetweenCoordinates(item1.location[0], item1.location[1], item2.location[0], item2.location[1])
                }
                neighborhoodMatrix.add(Distance(item1.id, item2.id, distance.toFloat()))
            }
        }
        return neighborhoodMatrix
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
                println("The element is $startPlace")
                val pathElement = neighborhoodMatrix.filter { distance ->
                    //                    (distance.coordinate_1_id.equals(startPlace.id) && !visited.contains(distance.coordinate_2_id)) || (distance.coordinate_2_id.equals(startPlace.id) && !visited.contains(distance.coordinate_1_id))
                    distance.coordinate_1_id.equals(startPlace.id) && !visited.contains(distance.coordinate_2_id)
                }.minBy { distance -> distance.value }
                if (pathElement != null) {
                    val secondPlace: Coordinate = places.first { coordinate -> coordinate.id.equals(pathElement.coordinate_2_id) }
                    pathPlaces.add(secondPlace)
                    visited.add(pathElement.coordinate_2_id)
                    startElementIndex = places.indexOf(secondPlace)
                }
            }
        }
        var pathValue: Float = pathMutationResolver.calcPathValue(pathPlaces)
        val path: Path = Path(pathPlaces, pathValue);
        return path;
    }


    fun findBestPathUsingGeneticAlgorythm(id: String, timeInSec: Int, populationSize: Int, parentPopulationSize: Int): Path {
        var salesmanSet: SalesmanSet = salesmanSetQueryResolver.findById(id);
        var pathPlaces: List<Coordinate> = mutableListOf();
//            Timer().schedule((timeInSec* 1000).toLong()) {
//        for(i in 0..timeInSec) {
        salesmanSet = doGenetic(salesmanSet, populationSize, parentPopulationSize)
//            }
        pathPlaces = salesmanSet.paths.last().places  //to juz jest path, moze nie trzeba tworzyć nowej
        var pathValue: Float = pathMutationResolver.calcPathValue(pathPlaces)
        val path: Path = Path(pathPlaces, pathValue);
        return path;
    }

    fun doGenetic(salesmanSet: SalesmanSet, populationSize: Int, parentPopulationSize: Int, mutationsProbability: Int = 1, swapsInMutation: Int = 10): SalesmanSet {
        var newSalesmanSet = salesmanSet
        //create population
        var genomPath = newSalesmanSet.places
        for (i in newSalesmanSet.population.size..populationSize) {
            genomPath = getRandomGenom(genomPath)
            val value = pathMutationResolver.calcPathValue(genomPath)
            newSalesmanSet.population.add(Path(genomPath, value))
        }

        var i = 0;
        //rob
        while (true) {
            //jesli warunek konca nie zostal osiagniety
            if (i < 100) {
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
            val crossPoint2: Int = ThreadLocalRandom.current().nextInt(crossPoint1 + 1, pathSize - crossPoint1 - 1)

            var a = crossover(salesmanSet.parentPopulation[2 * i], salesmanSet.parentPopulation[2 * i + 1], crossPoint1, crossPoint2)  //dziecko1
            var b = crossover(salesmanSet.parentPopulation[2 * i + 1], salesmanSet.parentPopulation[2 * i], crossPoint1, crossPoint2)  //dziecko2
            a = mutate(a, mutationsProbability, swapsInMutation)
            b = mutate(b, mutationsProbability, swapsInMutation)
            updatedSalesmanSet.population.add(a)
            updatedSalesmanSet.population.add(b)
        }
        return updatedSalesmanSet;
    }

    private fun crossover(parentOneGenom: Path, parentTwoGenom: Path, crossPoint1: Int, crossPoint2: Int): Path {
        val pathSize = parentOneGenom.places.size
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
            val zm = parentOneGenom.places[i];

            if (!checkForRepeat.contains(zm)) {
                childListOfCoordinates[i] = zm;
                checkForRepeat.add(zm);
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
        val child: Path = Path(childListOfCoordinates.filterNotNull());
        return child;
    }

    private fun mutate(genomPath: Path, mutationsProbability: Int, swapsInMutation: Int): Path {
        val chance = ThreadLocalRandom.current().nextInt(0, 100)
        val updatedGenomPath = genomPath.places.toMutableList()

        if (chance <= mutationsProbability) {
            for (i in 0 until swapsInMutation) {
                val city1 = ThreadLocalRandom.current().nextInt(0, genomPath.places.size)
                val city2 = ThreadLocalRandom.current().nextInt(0, genomPath.places.size)
                updatedGenomPath[city1] = genomPath.places[city2];
                updatedGenomPath[city2] = genomPath.places[city1];
            }
        }
        return Path(updatedGenomPath)
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

        for (i in 0..parentPopulationSize) {
            val randomInteger = ThreadLocalRandom.current().nextInt(0, 100)
            println(randomInteger)

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
