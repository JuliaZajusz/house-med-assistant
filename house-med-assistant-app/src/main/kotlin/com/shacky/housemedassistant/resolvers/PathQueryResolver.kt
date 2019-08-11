package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.Path
import com.shacky.housemedassistant.repository.PathRepository
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Component

@Component
class PathQueryResolver(val pathRepository: PathRepository, val mongoOperations: MongoOperations) : GraphQLQueryResolver {
    fun paths(): List<Path> {
        val list = pathRepository.findAll()
        return list;
    }
}