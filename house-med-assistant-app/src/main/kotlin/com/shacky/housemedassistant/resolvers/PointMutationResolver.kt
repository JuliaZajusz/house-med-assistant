package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Point
import com.shacky.housemedassistant.repository.PointRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class PointMutationResolver(private val pointRepository: PointRepository, val coordinateMutationResolver: CoordinateMutationResolver, val pointQueryResolver: PointQueryResolver) : GraphQLMutationResolver {

    fun newPoint(name: String, location: List<Float>): Point {
        val coordinate = coordinateMutationResolver.newCoordinate(location)
        val point = Point(name, coordinate)
        point.id = UUID.randomUUID().toString()
        pointRepository.save(point)
        return point
    }

    fun deletePoint(id: String): Boolean {
        val point = pointRepository.findById(id)
        if (point.isPresent) {
            val coordinateId = point.get().coordinate.id;
            pointRepository.deleteById(id)
            if (pointQueryResolver.coordinatePoints(coordinateId).isEmpty()) {
                coordinateMutationResolver.deleteCoordinate(point.get().coordinate.id);
            }
            return true
        }
        return false;
    }

//    fun updatePoint(id: String, coordinates: List<Coordinate>): Point {
//        val point = pointRepository.findById(id)
//        point.ifPresent {
//            it.coordinates = coordinates
//            pointRepository.save(it)
//        }
//        return point.get()
//    }

//    fun updatePoint(id: String, location: List<Float> ): Point {
//        val point = pointRepository.findById(id)
//
//        point.ifPresent {
//            val coordinates = coordinateMutationResolver.newCoordinate(point.get().id, location)
//            it.coordinates = coordinates
//            pointRepository.save(it)
//        }
//        return point.get()
//    }
}
