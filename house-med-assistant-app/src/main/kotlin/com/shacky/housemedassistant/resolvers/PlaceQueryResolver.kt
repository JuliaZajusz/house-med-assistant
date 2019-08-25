package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.Place
import com.shacky.housemedassistant.repository.PlaceRepository
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

@Component
class PlaceQueryResolver(val placeRepository: PlaceRepository,
                         private val mongoOperations: MongoOperations) : GraphQLQueryResolver {
    fun places(): List<Place> {
        val list = placeRepository.findAll()
//        for (item in list) {
//            item.coordinate = getCoordinates(placeId = item.id)
//        }
        return list
    }

    fun findPlaceByName(name: String): List<Place> {
        val query = Query()
        query.addCriteria(Criteria.where("name").`is`(name))
        val users = mongoOperations.find(query, Place::class.java)
        return users;
    }

    fun coordinatePlaces(coordinateId: String): List<Place> {
        val query = Query()
        query.addCriteria(Criteria.where("coordinate.id").`is`(coordinateId))
        return mongoOperations.find(query, Place::class.java)
    }

//    private fun getCoordinates(placeId: String): Coordinate {
//        val query = Query()
//        query.addCriteria(Criteria.where("id").`is`(placeId))
//        return mongoOperations.findOne(query, Coordinate::class.java)!!
//    }
}
