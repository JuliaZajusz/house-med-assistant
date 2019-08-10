package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.Point
import com.shacky.housemedassistant.repository.PointRepository
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

@Component
class PointQueryResolver(val pointRepository: PointRepository,
                         private val mongoOperations: MongoOperations) : GraphQLQueryResolver {
    fun points(): List<Point> {
        val list = pointRepository.findAll()
//        for (item in list) {
//            item.coordinate = getCoordinates(pointId = item.id)
//        }
        return list
    }

    fun coordinatePoints(coordinateId: String): List<Point> {
        val query = Query()
        query.addCriteria(Criteria.where("coordinate.id").`is`(coordinateId))
        return mongoOperations.find(query, Point::class.java)
    }

//    private fun getCoordinates(pointId: String): Coordinate {
//        val query = Query()
//        query.addCriteria(Criteria.where("id").`is`(pointId))
//        return mongoOperations.findOne(query, Coordinate::class.java)!!
//    }
}
