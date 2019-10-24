package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Tag
import com.shacky.housemedassistant.repository.TagRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class TagMutationResolver(private val tagRepository: TagRepository) : GraphQLMutationResolver {
    fun newTag(name: String): Tag {
//        val tag = Tag(name)
        var tag = tagRepository.getTagByName(name)
        if (tag == null) {
            tag = Tag(name)
            tag.id = UUID.randomUUID().toString()
            tagRepository.save(tag)
        }
        return tag
    }

    fun addTag(tag: Tag): Tag {
        return tagRepository.getTagByName(tag.name) ?: tagRepository.save(tag)
    }

    fun deleteTag(name: String): Boolean {
        tagRepository.deleteByName(name)
        return true
    }

    fun updateTag(id: String, newName: String): Tag {
        val tag = tagRepository.findById(id)
        tag.ifPresent {
            it.name = newName
            tagRepository.save(it)
        }
        return tag.get()
    }
}
