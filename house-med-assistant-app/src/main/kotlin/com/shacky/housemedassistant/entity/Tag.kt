package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "tag")
class Tag() {
    @Id
    var id: String = UUID.randomUUID().toString()
    var name: String = ""

    constructor (name: String) : this() {
        this.name = name
    }
}
