package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.shacky.housemedassistant.entity.Place
import com.shacky.housemedassistant.repository.PlaceRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class PlaceMutationResolver(private val placeRepository: PlaceRepository, val coordinateMutationResolver: CoordinateMutationResolver, val placeQueryResolver: PlaceQueryResolver) : GraphQLMutationResolver {

    fun newPlace(name: String, location: List<Float>): Place {
        val coordinate = coordinateMutationResolver.newCoordinate(location)
        val place = Place(name, coordinate)
//        val existed = placeRepository.findOne(Example.of(place));
        val existed = placeRepository.findOneByName(name);
        place.id = UUID.randomUUID().toString()
        placeRepository.save(place)
        return place
    }

    fun deletePlace(id: String): Boolean {
        val place = placeRepository.findById(id)
        if (place.isPresent) {
            val coordinateId = place.get().coordinate.id;
            placeRepository.deleteById(id)
            if (placeQueryResolver.coordinatePlaces(coordinateId).isEmpty()) {
                coordinateMutationResolver.deleteCoordinate(place.get().coordinate.id);
            }
            return true
        }
        return false;
    }

//    fun updatePlace(id: String, coordinates: List<Coordinate>): Place {
//        val place = placeRepository.findById(id)
//        place.ifPresent {
//            it.coordinates = coordinates
//            placeRepository.save(it)
//        }
//        return place.get()
//    }

//    fun updatePlace(id: String, location: List<Float> ): Place {
//        val place = placeRepository.findById(id)
//
//        place.ifPresent {
//            val coordinates = coordinateMutationResolver.newCoordinate(place.get().id, location)
//            it.coordinates = coordinates
//            placeRepository.save(it)
//        }
//        return place.get()
//    }
}
