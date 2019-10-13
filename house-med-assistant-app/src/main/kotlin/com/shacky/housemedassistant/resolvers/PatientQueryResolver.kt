package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.Patient
import com.shacky.housemedassistant.entity.Tag
import com.shacky.housemedassistant.repository.PatientRepository
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.stereotype.Component

@Component
class PatientQueryResolver(val patientRepository: PatientRepository,
                           val tagQueryResolver: TagQueryResolver,
                           private val mongoOperations: MongoOperations) : GraphQLQueryResolver {
    fun patients(): List<Patient> {
        val list = patientRepository.findAll()
//        for (item in list) {
//            item.coordinate = getCoordinates(placeId = item.id)
//        }
        return list
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

    fun findPatientsByNameRespectingTags(name: String, tags: List<String>): List<Patient> {
        val query = Query()
        val newTags: MutableList<Tag> = mutableListOf();
        for (tagName in tags) {
            val tag = tagQueryResolver.getTagByName(tagName)
            if (tag != null) {
                newTags.add(tag)
            }
        }

        query.addCriteria(Criteria.where("tags").all(newTags))
        query.addCriteria(TextCriteria.forDefaultLanguage().
//                        forLanguage("en"). // effectively the same as forDefaultLanguage() here
                matching(name));


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
