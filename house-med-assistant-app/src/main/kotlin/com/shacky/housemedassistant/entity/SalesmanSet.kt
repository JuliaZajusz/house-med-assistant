package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "salesmanSet")
data class SalesmanSet(
        var places: List<Coordinate> = ArrayList(),
        var neighborhoodMatrix: Map<String, Map<String, Number>> = HashMap(),  //distance between places - key: coordinate object id
        var paths: List<Path> = ArrayList()
) {
    @Id
    var id: String = ""
}
