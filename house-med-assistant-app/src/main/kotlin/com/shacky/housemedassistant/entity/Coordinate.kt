package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "coordinate")
data class Coordinate(
//        @GeoSpatialIndexed
        @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
        var location: List<Float> = ArrayList()
) {
        @Id
        var id: String = ""
}
