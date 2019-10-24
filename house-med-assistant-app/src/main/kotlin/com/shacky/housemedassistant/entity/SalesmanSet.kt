package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import kotlin.collections.ArrayList

@Document(collection = "salesmanSet")
data class SalesmanSet(
        var places: List<Patient>,
        var neighborhoodMatrix: List<Distance> = ArrayList() //distance between places - key: coordinate object id
) {
    @Id
    var id: String = UUID.randomUUID().toString()
    var paths: MutableList<Path> = mutableListOf<Path>()
    var population: MutableList<Path> = mutableListOf<Path>()
    var parentPopulation: List<Path> = mutableListOf<Path>()

}
