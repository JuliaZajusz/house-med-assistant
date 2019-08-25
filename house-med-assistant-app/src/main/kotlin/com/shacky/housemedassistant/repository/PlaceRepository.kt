package com.shacky.housemedassistant.repository

import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Place
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PlaceRepository : MongoRepository<Place, String> {

    //TODO to jest do poprawy, powinno zwracac listę, może być więcej obiektów o takiej samej nazwie
    fun findOneByName(name: String): Place?

    fun findOneByNameAndCoordinate(name: String, coordinate: Coordinate): Place?
}
