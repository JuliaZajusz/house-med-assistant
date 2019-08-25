package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Path
import com.shacky.housemedassistant.repository.PathRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class PathMutationResolver(private val pathRepository: PathRepository,
                           val coordinateMutationResolver: CoordinateMutationResolver,
                           val coordinateQueryResolver: CoordinateQueryResolver
) : GraphQLMutationResolver {
    fun newPath(coordinates: List<Coordinate>): Path? {
        var newCoordinates: MutableList<Coordinate> = mutableListOf();
        for (item in coordinates) {
            if (!item.id.isEmpty()) {
//                try {
                val itemById = coordinateQueryResolver.findById(item.id)
                newCoordinates.add(itemById)
//                } catch (e: NoSuchElementException) {
//                    return null
//                }
            } else {
                newCoordinates.add(coordinateMutationResolver.newCoordinate(item.location))
            }
        }
        val path = Path(newCoordinates)
        path.id = UUID.randomUUID().toString()
        pathRepository.save(path)
        return path
    }

    fun deletePath(id: String): Boolean {
        pathRepository.deleteById(id)
        return true
    }
}
