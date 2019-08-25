package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Distance
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

//    fun calcNeighborhoodMatrix(coordinates: List<Coordinate>): Map<String, Map<String, Number>> {
//        var neighborhoodMatrix: MutableMap<String, Map<String, Number>> = mutableMapOf();
//        var neighborhoodRow: MutableMap<String, Number> =mutableMapOf();
//        for (item1 in coordinates) {
//            for (item2 in coordinates) {
//                var distance = 0.0;
//                if(!item1.id.equals(item2.id)) {
//                    distance = coordinateQueryResolver.findDistanceBetweenCoordinates(item1.location[0], item1.location[1], item2.location[0], item2.location[1])
//                }
//                neighborhoodRow[item2.id] = distance
//            }
//            neighborhoodMatrix[item1.id] = neighborhoodRow
//        }
//        return neighborhoodMatrix
//    }

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
}
