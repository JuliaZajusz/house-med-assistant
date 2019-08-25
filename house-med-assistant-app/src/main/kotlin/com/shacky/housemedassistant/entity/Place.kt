package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "place")
data class Place(
        var name: String,
        var coordinate: Coordinate
) {
    @Id
    var id: String = ""
}
