package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "patient")
data class Patient(
        var lastName: String,
        var firstName: String,
        var coordinate: Coordinate,
        var tags: List<String>
) {
    @Id
    var id: String = ""
}
