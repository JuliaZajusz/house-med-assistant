package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Path
import com.shacky.housemedassistant.repository.PathRepository
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Component

@Component
class PathQueryResolver(val pathRepository: PathRepository,
                        val distanceQueryResolver: DistanceQueryResolver,
                        val mongoOperations: MongoOperations) : GraphQLQueryResolver {
    fun paths(): List<Path> {
        val list = pathRepository.findAll()
        return list;
    }

    fun calcPathValue(places: List<Coordinate>): Float {
        var value = 0.0f;
        var startCoordinate: Coordinate
        var endCoordinate: Coordinate
        for (i in places.indices) {
            if (places.size - 1 > i) {
//                value += distanceMutationResolver.getOrCreateDistanceByCoordinates(places[i], places[i + 1]).value
                startCoordinate = places[i]
                endCoordinate = places[i + 1]

            } else {
                startCoordinate = places[i]
                endCoordinate = places[0]
            }
            value += if (distanceQueryResolver.getDistanceByCoordinates(startCoordinate, endCoordinate) != null) {
                distanceQueryResolver.getDistanceByCoordinates(startCoordinate, endCoordinate)!!.value
            } else {
                distanceQueryResolver.findDistanceBetweenCoordinates(startCoordinate, endCoordinate).toFloat()
            }
        }
        return value
    }
}
