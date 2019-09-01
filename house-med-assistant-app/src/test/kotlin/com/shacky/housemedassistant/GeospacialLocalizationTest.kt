package com.shacky.housemedassistant

//import com.mongodb.client.model.geojson.Point
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Position
import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.entity.Path
import com.shacky.housemedassistant.entity.Place
import com.shacky.housemedassistant.repository.PlaceRepository
import com.shacky.housemedassistant.resolvers.*
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

    private var mongoClient: MongoClient? = null
    private var db: MongoDatabase? = null
    private var collection: MongoCollection<Document>? = null

    @Autowired
    lateinit var placeMutationResolver: PlaceMutationResolver

    @Autowired
    lateinit var placeQueryResolver: PlaceQueryResolver

    @Autowired
    lateinit var placeRepository: PlaceRepository

    @Autowired
    lateinit var coordinateMutationResolver: CoordinateMutationResolver

    @Autowired
    lateinit var coordinateQueryResolver: CoordinateQueryResolver

    @Autowired
    lateinit var salesmanSetMutationResolver: SalesmanSetMutationResolver

    @Autowired
    lateinit var salesmanSetQueryResolver: SalesmanSetQueryResolver

    @Autowired
    lateinit var pathMutationResolver: PathMutationResolver

    @Autowired
    lateinit var pathQueryResolver: PathQueryResolver

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
    fun givenNearbyLocation_whenSearchNearby_thenFound() {
        val currentLoc = Point(Position(-0.126821, 51.495885))
        val result = collection!!.find(Filters.near("coordinate.location", currentLoc, 1000.0, 10.0))

        assertNotNull(result.first())
        assertEquals("Big Ben", result.first()!!["name"])
    }

    @Test
    fun givenNearbyLocation_whenSearchWithinCircleSphere_thenFound() {
        val a = Place("Westminster, Londyn ", Coordinate(listOf(-0.1435083f, 51.4990956f)))
        val distanceInRad = 5.0 / 6371
        val result = collection!!.find(Filters.geoWithinCenterSphere("coordinate.location", a.coordinate.location[0].toDouble(), a.coordinate.location[1].toDouble(), distanceInRad))
        assertNotNull(result.first())
        assertEquals("Big Ben", result.first()!!["name"])
    }

    @Test
    fun checkFindCoordinatesByDistance() {
        val result = coordinateQueryResolver.findCoordinatesByDistance(-0.1435083f, 51.4990956f, 5)
        assertNotNull(result.first())
        assertEquals(listOf(-0.1268194f, 51.50073f), result.first().location)  //BigBen
    }

    @Test
    fun checkFindDistanceBetweenCoordinates() {
        val result = coordinateQueryResolver.findDistanceBetweenCoordinates(0, 0, 1, 1)
        println(result)
        assertEquals(157.24938127194397, result, 0.0)
    }

    @Test
    fun checkFindOneByLocation() {
        var coordinate = coordinateMutationResolver.newCoordinate(listOf(1f, 1.2f))
        var coordinate1 = coordinateQueryResolver.findOneByLocation(coordinate!!.location)
        assertEquals(coordinate, coordinate1)
    }

    @Test
    fun checkGetSalesmanSetByCoordinates() {
        val salesmanSet = salesmanSetQueryResolver.getSalesmanSetByCoordinates(
                listOf(Coordinate(listOf(2.3f, 2.4f)),
                        Coordinate(listOf(44f, 55.6f)),
                        Coordinate(listOf(0f, 0f)),
                        Coordinate(listOf(1f, 1f)))
        )

        assertNotNull(salesmanSet)
    }

    @Test
    fun checkFindGreedyPath() {
        val salesmanSet = salesmanSetMutationResolver.newSalesmanSet(
                listOf(Coordinate(listOf(2.3f, 2.4f)),
                        Coordinate(listOf(44f, 55.6f)),
                        Coordinate(listOf(0f, 0f)),
                        Coordinate(listOf(1f, 1f))
                )
        )
        val result1 = salesmanSetMutationResolver.findGreedyPath(salesmanSet!!.id)
        val path1: Path = listOf(
                coordinateQueryResolver.findOneByLocation(listOf(2.3f, 2.4f)),
                coordinateQueryResolver.findOneByLocation(listOf(1f, 1f)),
                coordinateQueryResolver.findOneByLocation(listOf(0f, 0f)),
                coordinateQueryResolver.findOneByLocation(listOf(44f, 55.6f))
        )?.let { Path(it as List<Coordinate>) }
        path1.value = pathMutationResolver.calcPathValue(path1.places)

        assertEquals(result1.places, path1.places)
        assertEquals(result1.value, path1.value)

        val result2 = salesmanSetMutationResolver.findGreedyPath(salesmanSet!!.id, coordinateQueryResolver.findOneByLocation(listOf(0f, 0f))!!.id)
        val path2: Path = listOf(
                coordinateQueryResolver.findOneByLocation(listOf(0f, 0f)),
                coordinateQueryResolver.findOneByLocation(listOf(1f, 1f)),
                coordinateQueryResolver.findOneByLocation(listOf(2.3f, 2.4f)),
                coordinateQueryResolver.findOneByLocation(listOf(44f, 55.6f))
        )?.let { Path(it as List<Coordinate>) }
        path2.value = pathMutationResolver.calcPathValue(path2.places)

        assertEquals(result2.places, path2.places)
        assertEquals(result2.value, path2.value)
    }

    @Test
    fun findBestPathUsingGeneticAlgorythmTest() {
        val salesmanSet = salesmanSetMutationResolver.newSalesmanSet(
                listOf(Coordinate(listOf(2.3f, 2.4f)),
                        Coordinate(listOf(44f, 55.6f)),
                        Coordinate(listOf(0f, 0f)),
                        Coordinate(listOf(1f, 1f))
                )
        )
        val result = salesmanSetMutationResolver.findBestPathUsingGeneticAlgorythm(salesmanSet!!.id, 20, 200, 10)
        println(result)
    }

}
