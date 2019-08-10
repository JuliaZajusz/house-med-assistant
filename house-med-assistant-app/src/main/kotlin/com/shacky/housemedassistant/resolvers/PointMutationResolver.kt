package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Point
import com.shacky.housemedassistant.repository.PointRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class PointMutationResolver(private val pointRepository: PointRepository, val coordinateMutationResolver: CoordinateMutationResolver) : GraphQLMutationResolver {

    fun newPoint(name: String): Point {
        val point = Point(name, null)
        point.id = UUID.randomUUID().toString()
        pointRepository.save(point)
        return point
    }

    fun deletePoint(id: String): Boolean {
        pointRepository.deleteById(id)
        return true
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
