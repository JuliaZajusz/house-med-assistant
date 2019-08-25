package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Place
import com.shacky.housemedassistant.entity.SalesmanSet
import com.shacky.housemedassistant.repository.SalesmanSetRepository
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.HashMap

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
        val salesmanSet = SalesmanSet(newCoordinates)
        salesmanSet.id = UUID.randomUUID().toString()
        salesmanSetRepository.save(salesmanSet)
        return salesmanSet
    }

    fun deleteSalesmanSet(id: String): Boolean {
        salesmanSetRepository.deleteById(id)
        return true
    }

    fun calcNeighborhoodMatrix(coordinates: List<Coordinate>): Map<String, Map<String, Number>> {
        var neighborhoodMatrix: MutableMap<String, Map<String, Number>> = mutableMapOf();
        val a = Place("Westminster, Londyn ", Coordinate(listOf(-0.1435083f, 51.4990956f)))
        val distanceInRad = 5.0 / 6371
//        val result = mongoCollection.find(Filters.geoWithinCenterSphere("coordinate.location", a.coordinate.location[0].toDouble(), a.coordinate.location[1].toDouble(), distanceInRad))
//        val result = coordinateQueryResolver.find(Filters.geoWithinCenterSphere("coordinate.location", a.coordinate.location[0].toDouble(), a.coordinate.location[1].toDouble(), distanceInRad))

        var neighborhoodRow: Map<String, Number> = HashMap();
        for (item in coordinates) {
//            neighborhoodRow =
            neighborhoodMatrix[item.id] = neighborhoodRow
        }
        return neighborhoodMatrix
    }
}
