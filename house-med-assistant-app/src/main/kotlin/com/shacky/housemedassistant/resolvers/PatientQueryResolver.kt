package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.Patient
import com.shacky.housemedassistant.entity.Tag
import com.shacky.housemedassistant.repository.PatientRepository
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

@Component
class PatientQueryResolver(val patientRepository: PatientRepository,
                           val tagQueryResolver: TagQueryResolver,
                           private val mongoOperations: MongoOperations) : GraphQLQueryResolver {
    fun patients(): List<Patient> {
        return patientRepository.findAll()
    }

    fun getPatientById(id: String): Patient {
        return patientRepository.findById(id).orElseThrow { NoSuchElementException(id) }
    }

    fun findPatientsByLastNameAndFirstName(lastName: String, firstName: String): List<Patient> {
        val query = Query()
        query.addCriteria(Criteria.where("lastName").`is`(lastName).and("firstName").`is`(firstName))
        val patients = mongoOperations.find(query, Patient::class.java)
        return patients;
    }

    fun findPatientsByTags(tags: List<String>): List<Patient> {
        val query = Query()
        val newTags: MutableList<Tag> = mutableListOf();
        for (tagName in tags) {
            val tag = tagQueryResolver.getTagByName(tagName)
            if (tag != null) {
                newTags.add(tag)
            }
        }
        query.addCriteria(Criteria.where("tags").all(newTags))
        val patients = mongoOperations.find(query, Patient::class.java)
        return patients;
    }

    fun findPatientsByFullTextSearch(searchedText: String): List<Patient> {
        val query = Query()
        if (searchedText.isNotEmpty()) {
            query.addCriteria(Criteria.where("lastName").regex(".*" + searchedText + ".*"))
            return mongoOperations.find(query, Patient::class.java);
        } else {
            return patients()
        }
    }

    fun findPatientsByTextRespectingTags(searchedText: String, tags: List<String>): List<Patient> {
        val query = Query()
        val newTags: MutableList<Tag> = mutableListOf();
        for (tagName in tags) {
            val tag = tagQueryResolver.getTagByName(tagName)
            if (tag != null) {
                newTags.add(tag)
            }
        }
        query.addCriteria(Criteria.where("tags").all(newTags))
        if (searchedText.isNotEmpty()) {
            query.addCriteria(Criteria.where("lastName").regex(".*" + searchedText + ".*"))
        }
        val patients = mongoOperations.find(query, Patient::class.java)
        return patients;
    }

    fun patientByCoordinate(coordinateId: String): List<Patient> {
        val query = Query()
        query.addCriteria(Criteria.where("coordinate.id").`is`(coordinateId))
        return mongoOperations.find(query, Patient::class.java)
    }

//    private fun getCoordinates(placeId: String): Coordinate {
//        val query = Query()
//        query.addCriteria(Criteria.where("id").`is`(placeId))
//        return mongoOperations.findOne(query, Coordinate::class.java)!!
//    }
}
