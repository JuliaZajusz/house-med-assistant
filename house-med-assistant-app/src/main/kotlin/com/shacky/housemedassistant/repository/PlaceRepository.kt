package com.shacky.housemedassistant.repository

import com.shacky.housemedassistant.entity.Place
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PlaceRepository : MongoRepository<Place, String> {
//    fun findByCoordinateAndName(place: Place): Any {
//
//        val q = Query(Criteria().alike(place))
//        return Optional
//                .ofNullable<S>(mongoOperations.findOne(q, example.getProbeType(), entityInformation.getCollectionName()))
//
////        return this.findAll(Query(Criteria(entityInformation.getIdAttribute())
////                .`in`(Streamable.of<ID>(ids).stream().collect(StreamUtils.toUnmodifiableList<ID>()))))
//    }

    //    fun findOneBy_nameAndCoordinate(name: String, coordinate: Coordinate): Place?
    fun findOneByName(name: String): Place?
}
