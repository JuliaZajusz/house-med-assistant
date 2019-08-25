package com.shacky.housemedassistant.resolvers

//import com.mongodb.client.model.geojson.Point
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.repository.CoordinateRepository
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


@Component
class CoordinateQueryResolver(val coordinateRepository: CoordinateRepository, val mongoOperations: MongoOperations) : GraphQLQueryResolver {
    fun coordinates(): List<Coordinate> {
        val list = coordinateRepository.findAll()
        return list;
    }

    fun findOneByLocation(location: List<Float>): Coordinate? {
        return coordinateRepository.findOneByLocation(location);
    }

    fun findById(id: String): Coordinate {
        return coordinateRepository.findById(id).orElseThrow { NoSuchElementException(id) };
    }

    fun findCoordinatesByDistance(x: Number, y: Number, distanceInKm: Number): List<Coordinate> {
        val distanceInRad: Double = distanceInKm.toDouble() / 6371
        val point = Point(x.toDouble(), y.toDouble())
        return mongoOperations.find(Query(Criteria.where("location").nearSphere(point).maxDistance(distanceInRad)), Coordinate::class.java);
    }

    fun degreesToRadians(degrees: Double): Double {
        return degrees * Math.PI / 180;
    }

    /**
     * @param x1 the longitude of first point
     * @param y1 the latitude of first point
     * @param x2 the longitude of second point
     * @param y2 the latitude of second point
     */
    fun findDistanceBetweenCoordinates(x1: Number, y1: Number, x2: Number, y2: Number): Double {
        var earthRadiusKm = 6371;

        var dLat = degreesToRadians(y2.toDouble() - y1.toDouble());
        var dLon = degreesToRadians(x2.toDouble() - x1.toDouble());

        val y1Rad = degreesToRadians(y1.toDouble());
        val y2Rad = degreesToRadians(y2.toDouble());

        var a = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * Math.cos(y1Rad) * cos(y2Rad);
        var c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return earthRadiusKm * c;
    }
}
