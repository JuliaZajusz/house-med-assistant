package com.shacky.housemedassistant.repository

import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Patient
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PatientRepository : MongoRepository<Patient, String> {

    //TODO to jest do poprawy, powinno zwracac listę, może być więcej obiektów o takiej samej nazwie
    fun findOneByLastNameAndFirstName(lastName: String, firstName: String): Patient?

    fun findOneByLastNameAndFirstNameAndCoordinate(LastName: String, firstName: String, coordinate: Coordinate): Patient?
}
