package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.*
import com.shacky.housemedassistant.repository.SalesmanSetRepository
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

@Component
class SalesmanSetMutationResolver(private val salesmanSetRepository: SalesmanSetRepository,
                                  val salesmanSetQueryResolver: SalesmanSetQueryResolver,
                                  val distanceMutationResolver: DistanceMutationResolver,
                                  val distanceQueryResolver: DistanceQueryResolver,
                                  val pathQueryResolver: PathQueryResolver,
                                  val tagMutationResolver: TagMutationResolver
) : GraphQLMutationResolver {

    fun setSalesmanSetName(id: String, name: String): Boolean {
        val salesmanSet = salesmanSetQueryResolver.findSalesmanSetById(id)
        salesmanSet.name = name
        salesmanSetRepository.save(salesmanSet)
        return true
    }

    fun newSalesmanSet(patients: List<Patient>): SalesmanSet? {
        var salesmanSet = salesmanSetQueryResolver.getSalesmanSetByPatients(patients)
        if (salesmanSet == null) {
            patients.forEach { patient ->
                patient.tags.forEach { tag ->
                    tagMutationResolver.addTag(tag)
                }
                patient
            }
            val coordinates = patients.map { patient -> patient.coordinate };
            val neighborhoodMatrix = calcNeighborhoodMatrix(coordinates)
            salesmanSet = SalesmanSet(patients, neighborhoodMatrix)
            salesmanSet.id = UUID.randomUUID().toString()
            salesmanSet.paths = mutableListOf(findFirstPath(salesmanSet))
            salesmanSetRepository.save(salesmanSet)
        }
        return salesmanSet
    }

    fun updateSalesmanSet(salesmanSet: SalesmanSet): SalesmanSet { //chyba tylko ostatnia linijka jest potrzebna
        println("updateSalesmanSet")
        val oldSalesmanSet = salesmanSetRepository.findById(salesmanSet.id).get();
        val updatedSet = salesmanSet
        if (oldSalesmanSet.places != updatedSet.places) {
            val newCoordinates = updatedSet.places.map { patient -> patient.coordinate };

            updatedSet.neighborhoodMatrix = calcNeighborhoodMatrix(newCoordinates)
            updatedSet.places.forEach { patient ->
                patient.tags.forEach { tag ->
                    tagMutationResolver.addTag(tag)
                }
                patient
            }
            salesmanSet.paths = mutableListOf(findFirstPath(salesmanSet))
            salesmanSet.name = oldSalesmanSet.name
        }
        return salesmanSetRepository.save(updatedSet)
    }

    fun upgradeSalesmanSet(id: String, timeInSec: Int, populationSize: Int = 200, parentPopulationSize: Int = 20): SalesmanSet {
        val salesmanSet: SalesmanSet = salesmanSetQueryResolver.findSalesmanSetById(id);
        val updatedSalesmanSet = SalesmanSetUtils(pathQueryResolver).findBestPathUsingGeneticAlgorythm(salesmanSet, timeInSec, populationSize, parentPopulationSize)
        updateSalesmanSet(updatedSalesmanSet)
        return updatedSalesmanSet;
//        return salesmanSetQueryResolver.findSalesmanSetById(id);
    }

    fun newSalesmanSetByDistance(numberOfCoordinates: Int, distances: List<Float>): SalesmanSet? {
        val patients: MutableList<Patient> = mutableListOf();
        for (i in 0 until numberOfCoordinates) {
            val long = 89.0f + i.toFloat() / numberOfCoordinates
            val lat = 89.0f + i.toFloat() / numberOfCoordinates
//            patients.add(coordinateMutationResolver.newCoordinate(listOf(long, lat)))
            patients.add(Patient(generateRandomString(5), generateRandomString(6), generateRandomString(7), Coordinate(listOf(long, lat), "coordinate-" + i), listOf()))
        }
        val newCoordinates = patients.map { patient -> patient.coordinate };
        val neighborhoodMatrix = createNeighborhoodMatrixByGivedDistances(newCoordinates, distances)
        var salesmanSet = salesmanSetQueryResolver.getSalesmanSetByDistances(neighborhoodMatrix)
        if (salesmanSet == null) {
            salesmanSet = SalesmanSet(patients, neighborhoodMatrix)
            salesmanSet.paths = mutableListOf(findFirstPath(salesmanSet))
            salesmanSet.id = UUID.randomUUID().toString()
            salesmanSetRepository.save(salesmanSet)
        }
        return salesmanSet
    }

    fun generateRandomString(length: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString = ThreadLocalRandom.current()
                .ints(length.toLong(), 0, charPool.size)
                .asSequence()
                .map(charPool::get)
                .joinToString("")
        return randomString
    }

    fun deleteSalesmanSet(id: String): Boolean {
        salesmanSetRepository.deleteById(id)
        return true
    }

    fun createNeighborhoodMatrixByGivedDistances(coordinates: List<Coordinate>, distances: List<Float>): List<Distance> {
        var neighborhoodMatrix: MutableList<Distance> = mutableListOf();
        for (item1 in coordinates) {
            for (item2 in coordinates) {
                val index = coordinates.indexOf(item1) * (coordinates.size) + coordinates.indexOf(item2)
                var distance = -1.0f
                try {
                    distance = distances[index]
                } catch (e: ArrayIndexOutOfBoundsException) {
                    println("" + index + " " + coordinates.indexOf(item1) + " " + coordinates.indexOf(item2))
                    throw ArrayIndexOutOfBoundsException()
                }

//                neighborhoodMatrix.add(distanceMutationResolver.newDistance(item1.location, item2.location, distance))
                neighborhoodMatrix.add(distanceMutationResolver.newDistance(item1, item2, distance))
//                neighborhoodMatrix.add(Distance(item1.id, item2.id, distance))
//                val startCoordinateId = coordinateQueryResolver.findOneByLocation(item1.location)?.id
//                val endCoordinateId = coordinateQueryResolver.findOneByLocation(item2.location)?.id
//                if (startCoordinateId != null && endCoordinateId != null) {
//                    val element = Distance(startCoordinateId, endCoordinateId, distance)
//                    neighborhoodMatrix.add(element)
//                }
            }
        }
        return neighborhoodMatrix
    }

    fun calcNeighborhoodMatrix(coordinates: List<Coordinate>): List<Distance> {
        var neighborhoodMatrix: MutableList<Distance> = mutableListOf();
        for (i in 0 until coordinates.size) {
            val item1 = coordinates[i]
            for (j in i + 1 until coordinates.size) {
                val item2 = coordinates[j]
                var distance: Distance = if (item1.id.equals(item2.id)) {
                    Distance(item1.id, item2.id, 0.0f)
                } else {
//                    distanceMutationResolver.getOrCreateDistanceByCoordinates(item1, item2)
                    distanceQueryResolver.getDistanceByCoordinates(item1, item2)
                            ?: Distance(item1.id, item2.id, distanceQueryResolver.findDistanceBetweenCoordinates(item1, item2).toFloat())
                }
                neighborhoodMatrix.add(distance)
            }
        }
        return neighborhoodMatrix
    }

    //findGreedyPath jest bardzo podobna, tzrab przerefaktorować
    fun findFirstPath(salesmanSet: SalesmanSet): Path {
        val visited: MutableList<String> = mutableListOf();
        val pathPlaces: MutableList<Patient> = mutableListOf();

        val places = salesmanSet.places
        val neighborhoodMatrix = salesmanSet.neighborhoodMatrix
        var startElementIndex = 0;

        if (places.isNotEmpty()) {
            pathPlaces.add(salesmanSet.places[startElementIndex])
            visited.add(salesmanSet.places[startElementIndex].coordinate.id)
        }

        for (i in places.indices) {
            val startPlace = places[startElementIndex]
            val pathElement = neighborhoodMatrix.filter { distance ->
                distance.startCoordinateId.equals(startPlace.coordinate.id) && !visited.contains(distance.endCoordinateId) ||
                        distance.endCoordinateId.equals(startPlace.coordinate.id) && !visited.contains(distance.startCoordinateId)
            }.map { distance ->
                if (distance.endCoordinateId.equals(startPlace.coordinate.id)) {
                    val tmp = distance.startCoordinateId
                    distance.startCoordinateId = distance.endCoordinateId
                    distance.endCoordinateId = tmp
                }
                distance
            }
                    .minBy { distance -> distance.value }
            if (pathElement != null) {
                val secondPlace: Patient = places.first { patient -> patient.coordinate.id.equals(pathElement.endCoordinateId) }
                pathPlaces.add(secondPlace)
                visited.add(pathElement.endCoordinateId)
                startElementIndex = places.indexOf(secondPlace)
            }
        }
        val pathValue: Float = pathQueryResolver.calcPathValue(pathPlaces.map { patient -> patient.coordinate })
        val path: Path = Path(pathPlaces, pathValue);
        return path;
    }

    //findFirstPath jest bardzo podobna, tzrab przerefaktorować
    fun findGreedyPath(id: String, startCoordinateId: String = ""): Path {
        val salesmanSet = salesmanSetRepository.findById(id);
        val visited: MutableList<String> = mutableListOf();
        val pathPlaces: MutableList<Patient> = mutableListOf();

        if (salesmanSet.isPresent) {
            val places = salesmanSet.get().places
            val neighborhoodMatrix = salesmanSet.get().neighborhoodMatrix
            var startElement: Patient = places[0];
            var startElementIndex = 0;
            if (startCoordinateId.isNotEmpty()) {
//                startElement = patientQueryResolver.patientByCoordinate(startCoordinateId).first()  //TODO, metoda zwraca listę, nie wiem czy powinna
                startElement = places.first { place -> place.coordinate.id == startCoordinateId }
                startElementIndex = places.indexOf(startElement)
            }

            if (places.isNotEmpty()) {
                pathPlaces.add(salesmanSet.get().places[startElementIndex])
                visited.add(salesmanSet.get().places[startElementIndex].coordinate.id)
            }

            for (i in places.indices) {
                val startPlace = places[startElementIndex]
                val pathElement = neighborhoodMatrix.filter { distance ->
                    distance.startCoordinateId.equals(startPlace.coordinate.id) && !visited.contains(distance.endCoordinateId) ||
                            distance.endCoordinateId.equals(startPlace.coordinate.id) && !visited.contains(distance.startCoordinateId)
                }.map { distance ->
                    if (distance.endCoordinateId.equals(startPlace.coordinate.id)) {
                        val tmp = distance.startCoordinateId
                        distance.startCoordinateId = distance.endCoordinateId
                        distance.endCoordinateId = tmp
                    }
                    distance
                }
                        .minBy { distance -> distance.value }
                if (pathElement != null) {
                    val secondPlace: Patient = places.first { patient -> patient.coordinate.id.equals(pathElement.endCoordinateId) }
                    pathPlaces.add(secondPlace)
                    visited.add(pathElement.endCoordinateId)
                    startElementIndex = places.indexOf(secondPlace)
                }
            }
        }
        val pathValue: Float = pathQueryResolver.calcPathValue(pathPlaces.map { patient -> patient.coordinate })
        val path: Path = Path(pathPlaces, pathValue);
        return path;
    }
}
