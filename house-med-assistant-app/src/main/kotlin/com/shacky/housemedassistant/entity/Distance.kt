package com.shacky.housemedassistant.entity

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document

@CompoundIndex(name = "start_end_idx", def = "{'startCoordinateId' : 1, 'endCoordinateId' : 1}")
@Document(collection = "distance")
data class Distance(
        var startCoordinateId: String = "",
        var endCoordinateId: String = "",
        var value: Float = 0.0f
) {
//    @Id
//    var id: String = ""
}
