package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.repository.CoordinateRepository
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Component

@Component
class CoordinateQueryResolver(val coordinateRepository: CoordinateRepository, val mongoOperations: MongoOperations) : GraphQLQueryResolver {
    fun coordinates(): List<Coordinate> {
        val list = coordinateRepository.findAll()
        return list;
    }

    fun findOneByLocation(location: List<Float>): Coordinate? {
        return coordinateRepository.findOneByLocation(location);
    }

    fun findById(id: String): Coordinate {
//        return coordinateRepository.findById(id).orElseThrow{NoSuchElementException(id)};
        return coordinateRepository.findById(id).orElseThrow { NoSuchElementException(id) };
    }
}
