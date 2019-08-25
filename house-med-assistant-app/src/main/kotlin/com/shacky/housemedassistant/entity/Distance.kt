package com.shacky.housemedassistant.entity

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "distance")
data class Distance(
        var coordinate_1_id: String = "",
        var coordinate_2_id: String = "",
        var value: Float = 0.0f
)
