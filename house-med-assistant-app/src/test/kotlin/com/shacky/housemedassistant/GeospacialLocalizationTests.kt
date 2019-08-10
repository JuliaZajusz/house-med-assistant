package com.shacky.housemedassistant

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Polygon
import com.mongodb.client.model.geojson.Position
import org.bson.Document
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

class MongoGeospatialLiveTest {

    private var mongoClient: MongoClient? = null
    private var db: MongoDatabase? = null
    private var collection: MongoCollection<Document>? = null

    @Before
    fun setup() {
        if (mongoClient == null) {
            mongoClient = MongoClient()
            db = mongoClient!!.getDatabase("kotlin-graphql")
            collection = db!!.getCollection("place")
            collection!!.deleteMany(Document())
            collection!!.createIndex(Indexes.geo2dsphere("location"))
            collection!!.insertOne(Document.parse("{'name':'Big Ben','location': {'coordinates':[-0.1268194,51.5007292],'type':'Point'}}"))
//            collection!!.insertOne(Document.parse("{'name':'Hyde Park','location': {'coordinates': [[[-0.159381,51.513126],[-0.189615,51.509928],[-0.187373,51.502442], [-0.153019,51.503464],[-0.159381,51.513126]]],'type':'Polygon'}}"))
        }
    }

    @Test
    fun givenNearbyLocation_whenSearchNearby_thenFound() {
        val currentLoc = Point(Position(-0.126821, 51.495885))
        val result = collection!!.find(Filters.near("location", currentLoc, 1000.0, 10.0))

        assertNotNull(result.first())
        assertEquals("Big Ben", result.first()!!["name"])
    }

    @Test
    fun givenFarLocation_whenSearchNearby_thenNotFound() {
        val currentLoc = Point(Position(-0.5243333, 51.4700223))
        val result = collection!!.find(Filters.near("location", currentLoc, 5000.0, 10.0))

        assertNull(result.first())
    }

    @Test
    fun givenNearbyLocation_whenSearchWithinCircleSphere_thenFound() {
        val distanceInRad = 5.0 / 6371
        val result = collection!!.find(Filters.geoWithinCenterSphere("location", -0.1435083, 51.4990956, distanceInRad))

        assertNotNull(result.first())
        assertEquals("Big Ben", result.first()!!["name"])
    }

    @Test
    fun givenNearbyLocation_whenSearchWithinBox_thenFound() {
        val lowerLeftX = -0.1427638
        val lowerLeftY = 51.4991288
        val upperRightX = -0.1256209
        val upperRightY = 51.5030272

        val result = collection!!.find(Filters.geoWithinBox("location", lowerLeftX, lowerLeftY, upperRightX, upperRightY))

        assertNotNull(result.first())
        assertEquals("Big Ben", result.first()!!["name"])
    }

    @Test
    fun givenNearbyLocation_whenSearchWithinPolygon_thenFound() {
        val points = ArrayList<List<Float>>()
        points.add(Arrays.asList(-0.1439, 51.4952)) // victoria station
        points.add(Arrays.asList(-0.1121, 51.4989))// Lambeth North
        points.add(Arrays.asList(-0.13, 51.5163))// Tottenham Court Road
        points.add(Arrays.asList(-0.1439, 51.4952)) // victoria station
        val result = collection!!.find(Filters.geoWithinPolygon("location", points))

        assertNotNull(result.first())
        assertEquals("Big Ben", result.first()!!["name"])
    }

    @Test
    fun givenNearbyLocation_whenSearchUsingIntersect_thenFound() {
        val positions = ArrayList<Position>()
        positions.add(Position(-0.1439, 51.4952))
        positions.add(Position(-0.1346, 51.4978))
        positions.add(Position(-0.2177, 51.5135))
        positions.add(Position(-0.1439, 51.4952))
        val geometry = Polygon(positions)
        val result = collection!!.find(Filters.geoIntersects("location", geometry))

        assertNotNull(result.first())
        assertEquals("Hyde Park", result.first()!!["name"])
    }

}