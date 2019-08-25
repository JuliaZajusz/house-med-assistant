package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Path
import com.shacky.housemedassistant.repository.CoordinateRepository
import com.shacky.housemedassistant.repository.PathRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class PathMutationResolver(private val pathRepository: PathRepository, val coordinateRepository: CoordinateRepository) : GraphQLMutationResolver {
    fun newPath(places: List<Coordinate>): Path {
        for (item in places) {
            if (item.id.isEmpty()) {
                item.id = UUID.randomUUID().toString()
            }
        }
        coordinateRepository.saveAll(places)
        val path = Path(places)
        path.id = UUID.randomUUID().toString()
        pathRepository.save(path)
        return path
    }

    fun deletePath(id: String): Boolean {
        pathRepository.deleteById(id)
        return true
    }
}
