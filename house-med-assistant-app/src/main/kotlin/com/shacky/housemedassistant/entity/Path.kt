package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "path")
data class Path(
//        @GeoSpatialIndexed
        var places: List<Coordinate> = ArrayList()
) {
    @Id
    var id: String = ""
}
