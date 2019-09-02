package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Distance
import com.shacky.housemedassistant.repository.DistanceRepository
import org.springframework.stereotype.Component


@Component
class DistanceQueryResolver(val distanceRepository: DistanceRepository) : GraphQLQueryResolver {
    fun distances(): List<Distance> {
        val list = distanceRepository.findAll()
        return list;
    }

    fun getDistanceByCoordinates(startCoordinate: Coordinate, endCoordinate: Coordinate): Distance? {
        return distanceRepository.findDistanceByStartCoordinateIdAndEndCoordinateId(startCoordinate.id, endCoordinate.id)
    }
}
