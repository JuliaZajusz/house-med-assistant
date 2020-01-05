package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Patient
import com.shacky.housemedassistant.entity.Tag
import com.shacky.housemedassistant.repository.PatientRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class PatientMutationResolver(private val patientRepository: PatientRepository,
                              val coordinateMutationResolver: CoordinateMutationResolver,
                              val patientQueryResolver: PatientQueryResolver,
                              val salesmanSetQueryResolver: SalesmanSetQueryResolver,
                              val salesmanSetMutationResolver: SalesmanSetMutationResolver,
                              val tagQueryResolver: TagQueryResolver,
                              val tagMutationResolver: TagMutationResolver) : GraphQLMutationResolver {

    fun newPatient(lastName: String, firstName: String, address: String, location: List<Float>, tags: List<String>): Patient {
        val coordinate = coordinateMutationResolver.newCoordinate(location)

        val newTags: MutableList<Tag> = mutableListOf();
        for (tagName in tags) {
            val tag = tagMutationResolver.newTag(tagName);
            newTags.add(tag)
        }

        val patient = Patient(lastName, firstName, address, coordinate, newTags)
        val existed = patientRepository.findOneByLastNameAndFirstNameAndCoordinate(lastName, firstName, coordinate);
        if (existed != null) {
            return existed
        }
        patient.id = UUID.randomUUID().toString()
        patientRepository.save(patient)
        return patient
    }

    fun deletePatient(id: String): Boolean {
        val patient = patientRepository.findById(id)
        if (patient.isPresent) {
            val coordinateId = patient.get().coordinate.id;
            patientRepository.deleteById(id)
            if (patientQueryResolver.patientByCoordinate(coordinateId).isEmpty()) {
                coordinateMutationResolver.deleteCoordinate(patient.get().coordinate.id);
            }
            return true
        }
        return false;
    }

    fun updatePatient(patient: Patient): Patient {
        val updatedPatientId = patient.id
        val oldPatient = patientRepository.findById(updatedPatientId)
        oldPatient.ifPresent {
            //update salesmansets with this patient
            val salesmanSetsToReset = salesmanSetQueryResolver.getSalesmansetsContainingGivenPatient(updatedPatientId);
            salesmanSetsToReset.forEach { salesmanSet ->
                var salesmanSetPlaces = salesmanSet.places
                salesmanSetPlaces = salesmanSetPlaces.filter { patient -> patient.id != updatedPatientId }
                salesmanSetPlaces = salesmanSetPlaces.toMutableList()
                salesmanSetPlaces.add(patient)
                salesmanSet.places = salesmanSetPlaces
                salesmanSetMutationResolver.updateSalesmanSet(salesmanSet);
            }
            //update patient
            patientRepository.save(patient)
        }
        return patientRepository.findById(updatedPatientId).get();
    }

//    fun updatePatient(id: String, location: List<Float> ): Patient {
//        val patient = patientRepository.findById(id)
//
//        patient.ifPresent {
//            val coordinates = coordinateMutationResolver.newCoordinate(patient.get().id, location)
//            it.coordinates = coordinates
//            patientRepository.save(it)
//        }
//        return patient.get()
//    }
}
