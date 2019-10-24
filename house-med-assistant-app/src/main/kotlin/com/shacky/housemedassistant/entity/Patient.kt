package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "patient")
data class Patient(
        @TextIndexed
        var lastName: String,
        @TextIndexed
        var firstName: String,
//        @TextIndexed
        var address: String,
        var coordinate: Coordinate,
        var tags: List<Tag>
) {
    @Id
    var id: String = UUID.randomUUID().toString()
}
