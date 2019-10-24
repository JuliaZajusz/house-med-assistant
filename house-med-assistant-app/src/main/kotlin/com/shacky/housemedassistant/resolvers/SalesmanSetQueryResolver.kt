package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.Distance
import com.shacky.housemedassistant.entity.Patient
import com.shacky.housemedassistant.entity.SalesmanSet
import com.shacky.housemedassistant.repository.CoordinateRepository
import com.shacky.housemedassistant.repository.SalesmanSetRepository
import org.springframework.stereotype.Component

@Component
class SalesmanSetQueryResolver(val salesmanSetRepository: SalesmanSetRepository, val coordinateRepository: CoordinateRepository) : GraphQLQueryResolver {
    fun salesmanSets(): List<SalesmanSet> {
        val list = salesmanSetRepository.findAll()
        return list;
    }

    fun findById(id: String): SalesmanSet {
        return salesmanSetRepository.findById(id).orElseThrow { NoSuchElementException(id) }
    }

//    fun getSalesmanSetByCoordinates(coordinates: List<Coordinate>): SalesmanSet? {
////        val places: MutableList<Coordinate> = mutableListOf()
////        coordinates.forEach { coordinate ->
////            val coordinateDBO = coordinateRepository.findOneByLocation(coordinate.location)
////            if (coordinateDBO == null) {
////                return null
////            }
////            places.add(coordinateDBO)
////        }
////        return salesmanSetRepository.findSalesmanSetByPlaces(places)
//        return salesmanSetRepository.findSalesmanSetByPlaces(coordinates)
//    }

    fun getSalesmanSetByPatients(patients: List<Patient>): SalesmanSet? {
        return salesmanSetRepository.findSalesmanSetByPlaces(patients)
    }

    fun getSalesmanSetByDistances(neighborhoodMatrix: List<Distance>): SalesmanSet? {
        return salesmanSetRepository.findSalesmanSetByNeighborhoodMatrix(neighborhoodMatrix)
    }
}
