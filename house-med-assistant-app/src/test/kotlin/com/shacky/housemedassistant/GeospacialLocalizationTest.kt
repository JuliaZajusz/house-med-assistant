package com.shacky.housemedassistant

//import com.mongodb.client.model.geojson.Point
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Position
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Place
import com.shacky.housemedassistant.repository.PlaceRepository
import com.shacky.housemedassistant.resolvers.PlaceMutationResolver
import com.shacky.housemedassistant.resolvers.PlaceQueryResolver
import org.bson.Document
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GeospatialLiveTest {
//class GeospatialLiveTest(
//        @Autowired val coordinateMutationResolver: CoordinateMutationResolver,
//        @Autowired val coordinateQueryResolver: CoordinateQueryResolver,
//        @Autowired val coordinateRepository: CoordinateRepository
//) {

    private var mongoClient: MongoClient? = null
    private var db: MongoDatabase? = null
    private var collection: MongoCollection<Document>? = null

    @Autowired
    lateinit var placeMutationResolver: PlaceMutationResolver

    @Autowired
    lateinit var placeQueryResolver: PlaceQueryResolver

    @Autowired
    lateinit var placeRepository: PlaceRepository

    @Before
    fun setup() {
        if (mongoClient == null) {
            mongoClient = MongoClient()
            db = mongoClient!!.getDatabase("kotlin-graphql")
            collection = db!!.getCollection("place")
        }
        placeMutationResolver.newPlace("Big Ben", listOf(-0.1268194f, 51.5007292f))
    }


    @Test
    fun getInsertedObjects() {
//        val result = coordinateRepository.findAll();
//        val result = coordinateQueryResolver.coordinates();
//        println(result[0])
    }

    @Test
    fun givenNearbyLocation_whenSearchNearby_thenFound() {
        val currentLoc = Point(Position(-0.126821, 51.495885))
        val result = collection!!.find(Filters.near("coordinate.location", currentLoc, 1000.0, 10.0))

        assertNotNull(result.first())
        assertEquals("Big Ben", result.first()!!["name"])
    }

    @Test
    fun givenNearbyLocation_whenSearchWithinCircleSphere_thenFound() {
//            val a = placeMutationResolver.newPlace("Westminster, Londyn ", listOf(-0.1435083f, 51.4990956f))
        val a = Place("Westminster, Londyn ", Coordinate(listOf(-0.1435083f, 51.4990956f)))
        val distanceInRad = 5.0 / 6371
        val result = collection!!.find(Filters.geoWithinCenterSphere("coordinate.location", a.coordinate.location[0].toDouble(), a.coordinate.location[1].toDouble(), distanceInRad))
        assertNotNull(result.first())
        assertEquals("Big Ben", result.first()!!["name"])
    }


}
