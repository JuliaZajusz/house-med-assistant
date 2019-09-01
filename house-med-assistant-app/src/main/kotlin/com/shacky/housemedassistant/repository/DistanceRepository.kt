package com.shacky.housemedassistant.repository

import com.shacky.housemedassistant.entity.Distance
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface DistanceRepository : MongoRepository<Distance, String> {


}

