package com.shacky.housemedassistant.repository

import com.shacky.housemedassistant.entity.Path
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PathRepository : MongoRepository<Path, String>