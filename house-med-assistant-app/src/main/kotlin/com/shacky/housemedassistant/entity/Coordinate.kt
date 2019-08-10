package com.shacky.housemedassistant.entity

import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "coordinate")
data class Coordinate(
        var pointId: String,
        @GeoSpatialIndexed
        var location: List<Float> = ArrayList()
)