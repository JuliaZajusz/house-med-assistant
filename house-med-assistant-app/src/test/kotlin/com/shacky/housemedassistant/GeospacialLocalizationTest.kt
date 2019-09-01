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

        salesmanSetMutationResolver.newSalesmanSetByDistance(6, listOf(
                0.0f, 20.0f, 30.0f, 31.0f, 28.0f, 40.0f,
                30.0f, 0.0f, 10.0f, 14.0f, 20.0f, 44.0f,
                40.0f, 20.0f, 0.0f, 10.0f, 22.0f, 50.0f,
                41.0f, 24.0f, 20.0f, 0.0f, 14.0f, 42.0f,
                38.0f, 30.0f, 32.0f, 24.0f, 0.0f, 28.0f,
                50.0f, 54.0f, 60.0f, 52.0f, 38.0f, 0.0f
        ))
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

//    @Test
//    fun checkGetSalesmanSetByDistances() {
//        val salesmanSet = salesmanSetQueryResolver.getSalesmanSetByDistances(
//                listOf(
//                        Distance("0", "0", 0.0f),
//                        Distance("0", "1", 20.0f),
//                        Distance("0", "2", 30.0f),
//                        Distance("0", "3", 31.0f),
//                        Distance("0", "4", 28.0f),
//                        Distance("0", "5", 40.0f),
//                        Distance("1", "0", 30.0f),
//                        Distance("1", "1", 0.0f),
//                        Distance("1", "2", 10.0f),
//                        Distance("1", "3", 14.0f),
//                        Distance("1", "4", 20.0f),
//                        Distance("1", "5", 44.0f),
//                        Distance("2", "0", 40.0f),
//                        Distance("2", "1", 20.0f),
//                        Distance("2", "2", 0.0f),
//                        Distance("2", "3", 10.0f),
//                        Distance("2", "4", 22.0f),
//                        Distance("2", "5", 50.0f),
//                        Distance("3", "0", 41.0f),
//                        Distance("3", "1", 24.0f),
//                        Distance("3", "2", 20.0f),
//                        Distance("3", "3", 0.0f),
//                        Distance("3", "4", 14.0f),
//                        Distance("3", "5", 42.0f),
//                        Distance("4", "0", 38.0f),
//                        Distance("4", "1", 30.0f),
//                        Distance("4", "2", 32.0f),
//                        Distance("4", "3", 24.0f),
//                        Distance("4", "4", 0.0f),
//                        Distance("4", "5", 28.0f),
//                        Distance("5", "0", 50.0f),
//                        Distance("5", "1", 54.0f),
//                        Distance("5", "2", 60.0f),
//                        Distance("5", "3", 52.0f),
//                        Distance("5", "4", 38.0f),
//                        Distance("5", "5", 0.0f)
//        ))
//
//        assertNotNull(salesmanSet)
//    }

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

        assertEquals(path1.places, result1.places)
        assertEquals(path1.value, result1.value)

        val result2 = salesmanSetMutationResolver.findGreedyPath(salesmanSet!!.id, coordinateQueryResolver.findOneByLocation(listOf(0f, 0f))!!.id)
        val path2: Path = listOf(
                coordinateQueryResolver.findOneByLocation(listOf(0f, 0f)),
                coordinateQueryResolver.findOneByLocation(listOf(1f, 1f)),
                coordinateQueryResolver.findOneByLocation(listOf(2.3f, 2.4f)),
                coordinateQueryResolver.findOneByLocation(listOf(44f, 55.6f))
        )?.let { Path(it as List<Coordinate>) }
        path2.value = pathMutationResolver.calcPathValue(path2.places)

        assertEquals(path2.places, result2.places)
        assertEquals(path2.value, result2.value)
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
        val result1 = salesmanSetMutationResolver.findBestPathUsingGeneticAlgorythm(salesmanSet!!.id, 20, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result2 = salesmanSetMutationResolver.findBestPathUsingGeneticAlgorythm(salesmanSet!!.id, 20, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result3 = salesmanSetMutationResolver.findBestPathUsingGeneticAlgorythm(salesmanSet!!.id, 20, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result4 = salesmanSetMutationResolver.findBestPathUsingGeneticAlgorythm(salesmanSet!!.id, 20, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result5 = salesmanSetMutationResolver.findBestPathUsingGeneticAlgorythm(salesmanSet!!.id, 20, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result6 = salesmanSetMutationResolver.findBestPathUsingGeneticAlgorythm(salesmanSet!!.id, 20, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result7 = salesmanSetMutationResolver.findBestPathUsingGeneticAlgorythm(salesmanSet!!.id, 20, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result8 = salesmanSetMutationResolver.findBestPathUsingGeneticAlgorythm(salesmanSet!!.id, 20, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result9 = salesmanSetMutationResolver.findBestPathUsingGeneticAlgorythm(salesmanSet!!.id, 20, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result10 = salesmanSetMutationResolver.findBestPathUsingGeneticAlgorythm(salesmanSet!!.id, 20, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/

        val path1: Path = listOf(
                coordinateQueryResolver.findOneByLocation(listOf(2.3f, 2.4f)),
                coordinateQueryResolver.findOneByLocation(listOf(1f, 1f)),
                coordinateQueryResolver.findOneByLocation(listOf(0f, 0f)),
                coordinateQueryResolver.findOneByLocation(listOf(44f, 55.6f))
        )?.let { Path(it as List<Coordinate>) }
        path1.value = pathMutationResolver.calcPathValue(path1.places)

        assertEquals(path1.value, result1.value)
        assertEquals(path1.value, result2.value)
        assertEquals(path1.value, result3.value)
        assertEquals(path1.value, result4.value)
        assertEquals(path1.value, result5.value)
        assertEquals(path1.value, result6.value)
        assertEquals(path1.value, result7.value)
        assertEquals(path1.value, result8.value)
        assertEquals(path1.value, result9.value)
        assertEquals(path1.value, result10.value)
    }

    @Test
    fun testMutate() {
        val path = pathMutationResolver.newPath(listOf(
                Coordinate(listOf(2.3f, 2.4f)),
                Coordinate(listOf(1f, 1f)),
                Coordinate(listOf(0f, 0f)),
                Coordinate(listOf(44f, 55.6f))
        ))
        val result = path?.let { salesmanSetMutationResolver.mutate(it, 100, 100) }
        assertEquals(path?.places!!.distinct().size, result?.places!!.distinct().size)
    }

}
