package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Distance
import com.shacky.housemedassistant.entity.Path
import com.shacky.housemedassistant.entity.SalesmanSet
import com.shacky.housemedassistant.repository.SalesmanSetRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class SalesmanSetMutationResolver(private val salesmanSetRepository: SalesmanSetRepository,
                                  val coordinateMutationResolver: CoordinateMutationResolver,
                                  val coordinateQueryResolver: CoordinateQueryResolver
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
        val pathValue: Number = 0;
        var startElementIndex = 0;
        if (salesmanSet.isPresent) {
            val places = salesmanSet.get().places
            val neighborhoodMatrix = salesmanSet.get().neighborhoodMatrix

            if (startCoordinateId.isNotEmpty()) {
                val startElement = coordinateQueryResolver.findById(startCoordinateId)
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
        val path: Path = Path(pathPlaces, pathValue);
        return path;
    }
}
