package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "tag")
data class Tag(
        var name: String
) {
    @Id
    var id: String = UUID.randomUUID().toString()
}
