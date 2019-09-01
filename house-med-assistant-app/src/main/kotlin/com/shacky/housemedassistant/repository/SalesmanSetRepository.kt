package com.shacky.housemedassistant.repository

import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Distance
import com.shacky.housemedassistant.entity.SalesmanSet
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SalesmanSetRepository : MongoRepository<SalesmanSet, String> {
    fun findSalesmanSetByPlaces(places: List<Coordinate>): SalesmanSet?

    fun findSalesmanSetByNeighborhoodMatrix(neighborhoodMatrix: List<Distance>): SalesmanSet?
}
