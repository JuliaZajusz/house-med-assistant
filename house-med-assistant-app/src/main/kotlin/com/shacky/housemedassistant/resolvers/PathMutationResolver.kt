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
                           val coordinateQueryResolver: CoordinateQueryResolver,
                           val distanceMutationResolver: DistanceMutationResolver,
                           val distanceQueryResolver: DistanceQueryResolver
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

    fun updatePath(path: Path): Path {
        return pathRepository.save(path)
    }

    fun updatePath(id: String, value: Float): Path {
        val path = pathRepository.findById(id)
        path.ifPresent {
            it.value = value
            pathRepository.save(it)
        }
        return path.get()
    }

    fun updatePath(id: String, places: List<Coordinate>): Path {
        val path = pathRepository.findById(id)
        path.ifPresent {
            it.places = places
            it.value = calcPathValue(places)
            pathRepository.save(it)
        }
        return path.get()
    }

    fun calcPathValue(places: List<Coordinate>): Float {
        var value = 0.0;
        for (i in places.indices) {
            if (places.size - 1 > i) {
                value += distanceMutationResolver.getOrCreateDistanceByCoordinates(places[i], places[i + 1]).value
            } else {
                value += distanceMutationResolver.getOrCreateDistanceByCoordinates(places[i], places[0]).value
            }
        }
        return value.toFloat()
    }
}
