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
        val neighborhoodMatrix = calcNeighborhoodMatrix(newCoordinates)
        val salesmanSet = SalesmanSet(newCoordinates, neighborhoodMatrix)
        salesmanSet.id = UUID.randomUUID().toString()
        salesmanSetRepository.save(salesmanSet)
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

    fun doGenetic(salesmanSet: SalesmanSet, populationSize: Int, parentPopulationSize: Int): SalesmanSet {
        val newSalesmanSet = salesmanSet
        //create population
        var genomPath = newSalesmanSet.places
        for (i in newSalesmanSet.population.size..populationSize) {
            genomPath = mutateGenomPathBySwithingElements(genomPath)
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
//        mutuj

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

    private fun mutateGenomPathBySwithingElements(genomPath: List<Coordinate>): List<Coordinate> {
        return genomPath.shuffled()
    }

    fun chooseParentPopulationByRouletteMethod(population: List<Path>, parentPopulationSize: Int): List<Path> {
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
