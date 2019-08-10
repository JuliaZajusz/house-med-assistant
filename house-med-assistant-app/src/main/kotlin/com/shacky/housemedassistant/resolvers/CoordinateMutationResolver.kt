package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.repository.CoordinateRepository
import org.springframework.stereotype.Component

@Component
class CoordinateMutationResolver(private val coordinateRepository: CoordinateRepository) : GraphQLMutationResolver {
    fun newCoordinate(coordinateId: String, location: List<Float>): Coordinate {
        val coordinate = Coordinate(coordinateId, location)
        coordinateRepository.save(coordinate)
        return coordinate
    }
}