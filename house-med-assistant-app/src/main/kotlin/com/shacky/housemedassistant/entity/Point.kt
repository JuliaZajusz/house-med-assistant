package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "point")
data class Point(
        var name: String,
        var coordinate: Coordinate
) {
    @Id
    var id: String = ""
}