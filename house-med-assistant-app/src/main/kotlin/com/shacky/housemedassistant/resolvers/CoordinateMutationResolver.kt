package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.repository.CoordinateRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class CoordinateMutationResolver(private val coordinateRepository: CoordinateRepository,
                                 val coordinateQueryResolver: CoordinateQueryResolver) : GraphQLMutationResolver {
    fun newCoordinate(location: List<Float>): Coordinate {
        var coordinate = coordinateQueryResolver.findOneByLocation(location);
        if (coordinate == null) {
            coordinate = Coordinate(location)
            coordinate.id = UUID.randomUUID().toString()
            coordinateRepository.save(coordinate)
        }
        return coordinate
    }

    fun deleteCoordinate(id: String): Boolean {
        coordinateRepository.deleteById(id)
        return true
    }
}
