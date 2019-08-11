package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Path
import com.shacky.housemedassistant.repository.PathRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class PathMutationResolver(private val pathRepository: PathRepository) : GraphQLMutationResolver {
    fun newPath(points: List<Coordinate>): Path {
        val path = Path(points)
        path.id = UUID.randomUUID().toString()
        pathRepository.save(path)
        return path
    }

    fun deletePath(id: String): Boolean {
        pathRepository.deleteById(id)
        return true
    }
}