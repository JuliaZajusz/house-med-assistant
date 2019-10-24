package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "path")
data class Path(
        var places: List<Patient> = ArrayList(),
        var value: Number = -1
) {
    @Id
    var id: String = ""

}
