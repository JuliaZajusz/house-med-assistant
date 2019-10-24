package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.Tag
import com.shacky.housemedassistant.repository.TagRepository
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

@Component
class TagQueryResolver(val tagRepository: TagRepository,
                       val tagMutationResolver: TagMutationResolver,
                       private val mongoOperations: MongoOperations) : GraphQLQueryResolver {
    fun tags(): List<Tag> {
        return tagRepository.findAll()
    }

    fun getTagByName(name: String): Tag {
        var result = tagRepository.getTagByName(name);
        if (result == null) {
            result = tagMutationResolver.newTag(name)
        }
        return result
//        val query = Query()
//        query.addCriteria(Criteria.where("name").`is`(name))
//        return mongoOperations.find(query, Review::class.java)
    }

    fun searchTagsByText(searchedText: String): List<Tag> {
        val query = Query()
        if (searchedText.isNotEmpty()) {
            query.addCriteria(Criteria.where("name").regex(".*" + searchedText + ".*"))
            return mongoOperations.find(query, Tag::class.java);
        } else {
            return tags()
        }
    }
}
