package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "salesmanSet")
data class SalesmanSet(
        var places: List<Patient>,
        var neighborhoodMatrix: List<Distance> = ArrayList() //distance between places - key: coordinate object id
) {
    @Id
    var id: String = ""
    var paths: MutableList<Path> = mutableListOf<Path>()
    var population: MutableList<Path> = mutableListOf<Path>()
    var parentPopulation: List<Path> = mutableListOf<Path>()

}
