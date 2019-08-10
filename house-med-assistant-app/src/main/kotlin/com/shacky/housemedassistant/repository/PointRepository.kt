package com.shacky.housemedassistant.repository

import com.shacky.housemedassistant.entity.Point
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PointRepository : MongoRepository<Point, String>