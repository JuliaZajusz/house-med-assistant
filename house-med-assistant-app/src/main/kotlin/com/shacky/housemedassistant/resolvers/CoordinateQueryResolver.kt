package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.Coordinate
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

@Component
class CoordinateQueryResolver(val mongoOperations: MongoOperations) : GraphQLQueryResolver {
    fun coordinates(pointId: String): List<Coordinate> {
        val query = Query()
        query.addCriteria(Criteria.where("pointId").`is`(pointId))
        return mongoOperations.find(query, Coordinate::class.java)
    }
}