package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Distance
import com.shacky.housemedassistant.repository.DistanceRepository
import org.springframework.stereotype.Component
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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
            calculatedValue = findDistanceBetweenCoordinates(startCoordinate, endCoordinate).toFloat()
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
        var calculatedValue: Float = findDistanceBetweenCoordinates(startCoordinate, endCoordinate).toFloat()
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

    fun findDistanceBetweenCoordinates(startCoordinate: Coordinate, endCoordinate: Coordinate): Double {
        return findDistanceBetweenCoordinates(startCoordinate.location[0], startCoordinate.location[1], endCoordinate.location[0], endCoordinate.location[1])
    }

    private fun degreesToRadians(degrees: Double): Double {
        return degrees * Math.PI / 180;
    }

    /**
     * @param x1 the longitude of first point
     * @param y1 the latitude of first point
     * @param x2 the longitude of second point
     * @param y2 the latitude of second point
     */
    fun findDistanceBetweenCoordinates(x1: Number, y1: Number, x2: Number, y2: Number): Double {
        val earthRadiusKm = 6371;

        val dLat = degreesToRadians(y2.toDouble() - y1.toDouble());
        val dLon = degreesToRadians(x2.toDouble() - x1.toDouble());

        val y1Rad = degreesToRadians(y1.toDouble());
        val y2Rad = degreesToRadians(y2.toDouble());

        val a = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * Math.cos(y1Rad) * cos(y2Rad);
        val c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return earthRadiusKm * c;
    }


}
