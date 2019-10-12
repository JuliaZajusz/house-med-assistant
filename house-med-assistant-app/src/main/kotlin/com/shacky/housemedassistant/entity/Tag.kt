package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "tag")
data class Tag(
        var name: String
) {
    @Id
    var id: String = ""
}
