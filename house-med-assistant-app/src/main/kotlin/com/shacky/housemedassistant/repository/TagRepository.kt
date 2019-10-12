package com.shacky.housemedassistant.repository

import com.shacky.housemedassistant.entity.Tag
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TagRepository : MongoRepository<Tag, String> {
    fun getTagByName(name: String): Tag?
    fun deleteByName(name: String): Boolean
}
