package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Path
import com.shacky.housemedassistant.entity.Patient
import com.shacky.housemedassistant.repository.PathRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class PathMutationResolver(private val pathRepository: PathRepository,
                           val coordinateMutationResolver: CoordinateMutationResolver,
                           val coordinateQueryResolver: CoordinateQueryResolver,
                           val pathQueryResolver: PathQueryResolver
//                           val distanceMutationResolver: DistanceMutationResolver,
//                           val distanceQueryResolver: DistanceQueryResolver
) : GraphQLMutationResolver {
    fun newPath(patients: List<Patient>): Path? { //TODO dbanie o niedublowanie się koordynatów powinno być w coordinateMutationResolver.newCoordinate()
//        //TODO poza tym, ze należy usunąć stąd dodawanie koordynatów, to nie ma sensu.
//        val newCoordinates: MutableList<Coordinate> = mutableListOf();
//        for (item in patients) {
//            if (!item.id.isEmpty()) {
//                val itemById = coordinateQueryResolver.findById(item.id)
//                newCoordinates.add(itemById)
//            } else {
//                newCoordinates.add(coordinateMutationResolver.newCoordinate(item.location))
//            }
//        }
        val path = Path(patients, pathQueryResolver.calcPathValue(patients.map { patient -> patient.coordinate }))
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

    fun updatePath(id: String, places: List<Patient>): Path {
        val path = pathRepository.findById(id)
        path.ifPresent {
            it.places = places
            it.value = pathQueryResolver.calcPathValue(places.map { patient -> patient.coordinate })
            pathRepository.save(it)
        }
        return path.get()
    }
}
