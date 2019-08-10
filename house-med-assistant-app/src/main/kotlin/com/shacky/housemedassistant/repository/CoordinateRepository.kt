package com.shacky.housemedassistant.repository

import com.shacky.housemedassistant.entity.Coordinate
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CoordinateRepository : MongoRepository<Coordinate, String>