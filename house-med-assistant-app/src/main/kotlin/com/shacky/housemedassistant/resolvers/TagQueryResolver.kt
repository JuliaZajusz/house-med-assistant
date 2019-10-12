package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.Tag
import com.shacky.housemedassistant.repository.TagRepository
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Component

@Component
class TagQueryResolver(val tagRepository: TagRepository,
                       private val mongoOperations: MongoOperations) : GraphQLQueryResolver {
    fun tags(): List<Tag> {
        return tagRepository.findAll()
    }

    private fun getTagByName(name: String): Tag? {
        return tagRepository.getTagByName(name);
//        val query = Query()
//        query.addCriteria(Criteria.where("name").`is`(name))
//        return mongoOperations.find(query, Review::class.java)
    }
}
