package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.SalesmanSet
import com.shacky.housemedassistant.repository.SalesmanSetRepository
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Component

@Component
class SalesmanSetQueryResolver(val salesmanSetRepository: SalesmanSetRepository, val mongoOperations: MongoOperations) : GraphQLQueryResolver {
    fun salesmanSets(): List<SalesmanSet> {
        val list = salesmanSetRepository.findAll()
        return list;
    }

    fun findById(id: String): SalesmanSet {
        return salesmanSetRepository.findById(id).orElseThrow { NoSuchElementException(id) }
    }

    fun getSalesmanSetByCoordinates(coordinates: List<Coordinate>): SalesmanSet? {
        val places: MutableList<Coordinate> = mutableListOf()
        coordinates.forEach { coordinate ->
            if (coordinate.id == null) {  //to dziwne, najwidoczniej już przy tworzneiu obiektu ustawiane jest id...
                return null
            }
            //TODO jeśli ma id to pobierz obiekt z bazy i dodaj go do place
        }
        return salesmanSetRepository.findSalesmanSetByPlaces(places)
    }
}
