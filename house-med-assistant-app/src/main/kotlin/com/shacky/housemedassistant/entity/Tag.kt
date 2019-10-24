package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "tag")
class Tag() {
    @Id
    var id: String = ""
    var name: String = ""

    constructor (name: String) : this() {
        this.name = name
    }

    constructor (id: String, name: String) : this() {
        this.id = id
        this.name = name
    }
}
