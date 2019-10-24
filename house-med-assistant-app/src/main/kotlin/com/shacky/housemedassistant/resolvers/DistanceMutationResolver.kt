package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Distance
import com.shacky.housemedassistant.repository.DistanceRepository
import org.springframework.stereotype.Component

@Component
class DistanceMutationResolver(private val distanceRepository: DistanceRepository,
                               val distanceQueryResolver: DistanceQueryResolver,
                               val coordinateMutationResolver: CoordinateMutationResolver) : GraphQLMutationResolver {
    fun newDistance(start: List<Float>, end: List<Float>, value: Float? = null): Distance {
        val startCoordinate: Coordinate = coordinateMutationResolver.newCoordinate(start)
        val endCoordinate: Coordinate = coordinateMutationResolver.newCoordinate(end)
        var calculatedValue: Float;
        if (value != null) {
            calculatedValue = value
        } else {
            calculatedValue = distanceQueryResolver.findDistanceBetweenCoordinates(startCoordinate, endCoordinate).toFloat()
        }
        var distance = distanceQueryResolver.getDistanceByCoordinates(startCoordinate, endCoordinate);
        if (distance == null) {
            distance = Distance(startCoordinate.id, endCoordinate.id, calculatedValue)
            distanceRepository.save(distance)
        } else if (distance.value != calculatedValue) {
            distance = updateDistance(distance)
        }
        return distance
    }

    fun createDistance(start: Coordinate, end: Coordinate): Distance {
        val startCoordinate: Coordinate = coordinateMutationResolver.newCoordinate(start.location)
        val endCoordinate: Coordinate = coordinateMutationResolver.newCoordinate(end.location)
        var calculatedValue: Float = distanceQueryResolver.findDistanceBetweenCoordinates(startCoordinate, endCoordinate).toFloat()
        var distance = distanceQueryResolver.getDistanceByCoordinates(startCoordinate, endCoordinate);
        if (distance == null) {
            distance = Distance(startCoordinate.id, endCoordinate.id, calculatedValue)
            distanceRepository.save(distance)
        }
        return distance
    }

    fun getOrCreateDistanceByCoordinates(startCoordinate: Coordinate, endCoordinate: Coordinate): Distance {
        return distanceQueryResolver.getDistanceByCoordinates(startCoordinate, endCoordinate)
                ?: createDistance(startCoordinate, endCoordinate)
    }

    fun updateDistance(distance: Distance): Distance { //TODO, do sprawdzenia
        return distanceRepository.save(distance)
    }

    fun deleteDistance(id: String): Boolean {
        distanceRepository.deleteById(id)
        return true
    }
}
