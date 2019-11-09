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
import com.shacky.housemedassistant.entity.Patient
import com.shacky.housemedassistant.repository.PatientRepository
import com.shacky.housemedassistant.resolvers.*
import org.bson.Document
import org.junit.Assert.*
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
    lateinit var patientMutationResolver: PatientMutationResolver

    @Autowired
    lateinit var patientQueryResolver: PatientQueryResolver

    @Autowired
    lateinit var patientRepository: PatientRepository

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

    @Autowired
    lateinit var distanceMutationResolver: DistanceMutationResolver

    @Autowired
    lateinit var distanceQueryResolver: DistanceQueryResolver

    @Before
    fun setup() {
        if (mongoClient == null) {
            mongoClient = MongoClient()
            db = mongoClient!!.getDatabase("kotlin-graphql")
            collection = db!!.getCollection("patient")
        }
        patientMutationResolver.newPatient("Kowalski", "Jan", "", listOf(-0.1268194f, 51.5007292f), listOf())

//        salesmanSetMutationResolver.newSalesmanSetByDistance(6, listOf(
//                0.0f, 20.0f, 30.0f, 31.0f, 28.0f, 40.0f,
//                30.0f, 0.0f, 10.0f, 14.0f, 20.0f, 44.0f,
//                40.0f, 20.0f, 0.0f, 10.0f, 22.0f, 50.0f,
//                41.0f, 24.0f, 20.0f, 0.0f, 14.0f, 42.0f,
//                38.0f, 30.0f, 32.0f, 24.0f, 0.0f, 28.0f,
//                50.0f, 54.0f, 60.0f, 52.0f, 38.0f, 0.0f
//        ))
    }

    @Test
    fun givenNearbyLocation_whenSearchNearby_thenFound() {
        val currentLoc = Point(Position(-0.126821, 51.495885))
        val result = collection!!.find(Filters.near("coordinate.location", currentLoc, 1000.0, 10.0))

        assertNotNull(result.first())
        assertEquals("Kowalski", result.first()!!["lastName"])
    }

    @Test
    fun givenNearbyLocation_whenSearchWithinCircleSphere_thenFound() {
        val a = Patient("Westminster", "Londyn", "", Coordinate(listOf(-0.1435083f, 51.4990956f)), listOf())
        val distanceInRad = 5.0 / 6371
        val result = collection!!.find(Filters.geoWithinCenterSphere("coordinate.location", a.coordinate.location[0].toDouble(), a.coordinate.location[1].toDouble(), distanceInRad))
        assertNotNull(result.first())
        assertEquals("Kowalski", result.first()!!["lastName"])
    }

    @Test
    fun checkFindCoordinatesByDistance() {
        val result = coordinateQueryResolver.findCoordinatesByDistance(-0.1435083f, 51.4990956f, 5)
        assertNotNull(result.first())
        assertEquals(listOf(-0.1268194f, 51.50073f), result.first().location)  //BigBen
    }

    @Test
    fun checkFindDistanceBetweenCoordinates() {
        val result = distanceQueryResolver.findDistanceBetweenCoordinates(0, 0, 1, 1)
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
        val salesmanSet = salesmanSetQueryResolver.getSalesmanSetByPatients(
                listOf(Patient("", "", "", Coordinate(listOf(2.3f, 2.4f), "co-0"), listOf()),
                        Patient("", "", "", Coordinate(listOf(44f, 55.6f), "co-1"), listOf()),
                        Patient("", "", "", Coordinate(listOf(0f, 0f), "co-2"), listOf()),
                        Patient("", "", "", Coordinate(listOf(1f, 1f), "co-3"), listOf()))
        )

        assertNotNull(salesmanSet)
    }

//    @Test
//    fun checkGetSalesmanSetByDistances() {
//        val salesmanSet = salesmanSetMutationResolver.newSalesmanSetByDistance(6, listOf(
//                0.0f, 20.0f, 30.0f, 31.0f, 28.0f, 40.0f,
//                30.0f, 0.0f, 10.0f, 14.0f, 20.0f, 44.0f,
//                40.0f, 20.0f, 0.0f, 10.0f, 22.0f, 50.0f,
//                41.0f, 24.0f, 20.0f, 0.0f, 14.0f, 42.0f,
//                38.0f, 30.0f, 32.0f, 24.0f, 0.0f, 28.0f,
//                50.0f, 54.0f, 60.0f, 52.0f, 38.0f, 0.0f
//        ))
//        assertNotNull(salesmanSet)
//    }

    @Test
    fun checkFindGreedyPath() {
        val salesmanSet = salesmanSetMutationResolver.newSalesmanSet(
                listOf(Patient("", "", "", Coordinate(listOf(2.3f, 2.4f), "co-0"), listOf()),
                        Patient("", "", "", Coordinate(listOf(44f, 55.6f), "co-1"), listOf()),
                        Patient("", "", "", Coordinate(listOf(0f, 0f), "co-2"), listOf()),
                        Patient("", "", "", Coordinate(listOf(1f, 1f), "co-3"), listOf())
                )
        )


        val result1 = salesmanSetMutationResolver.findGreedyPath(salesmanSet!!.id)
        val path1: Path = Path(listOf(
//                coordinateQueryResolver.findOneByLocation(listOf(2.3f, 2.4f)),
//                coordinateQueryResolver.findOneByLocation(listOf(1f, 1f)),
//                coordinateQueryResolver.findOneByLocation(listOf(0f, 0f)),
//                coordinateQueryResolver.findOneByLocation(listOf(44f, 55.6f))

                Patient("", "", "", Coordinate(listOf(2.3f, 2.4f), "co-0"), listOf()),
                Patient("", "", "", Coordinate(listOf(1f, 1f), "co-3"), listOf()),
                Patient("", "", "", Coordinate(listOf(0f, 0f), "co-2"), listOf()),
                Patient("", "", "", Coordinate(listOf(44f, 55.6f), "co-1"), listOf())
        ))
        path1.value = pathQueryResolver.calcPathValue(path1.places.map { patient -> patient.coordinate })

        assertEquals(path1.places, result1.places)
        assertEquals(path1.value, result1.value)

        val patient0 = Patient("", "", "", Coordinate(listOf(0f, 0f), "co-2"), listOf())
        val result2 = salesmanSetMutationResolver.findGreedyPath(salesmanSet!!.id, patient0.coordinate.id)
        val path2: Path = Path(listOf(
//                coordinateQueryResolver.findOneByLocation(listOf(0f, 0f)),
//                coordinateQueryResolver.findOneByLocation(listOf(1f, 1f)),
//                coordinateQueryResolver.findOneByLocation(listOf(2.3f, 2.4f)),
//                coordinateQueryResolver.findOneByLocation(listOf(44f, 55.6f))

                Patient("", "", "", Coordinate(listOf(0f, 0f), "co-2"), listOf()),
                Patient("", "", "", Coordinate(listOf(1f, 1f), "co-3"), listOf()),
                Patient("", "", "", Coordinate(listOf(2.3f, 2.4f), "co-0"), listOf()),
                Patient("", "", "", Coordinate(listOf(44f, 55.6f), "co-1"), listOf())

        ))
        path2.value = pathQueryResolver.calcPathValue(path2.places.map { patient -> patient.coordinate })

        assertEquals(path2.places, result2.places)
        assertEquals(path2.value, result2.value)
    }

    @Test
    fun findBestPathUsingGeneticAlgorythmTest() {
        val salesmanSet = salesmanSetMutationResolver.newSalesmanSet(
                listOf(Patient("", "", "", Coordinate(listOf(2.3f, 2.4f), "co-0"), listOf()),
                        Patient("", "", "", Coordinate(listOf(44f, 55.6f), "co-1"), listOf()),
                        Patient("", "", "", Coordinate(listOf(0f, 0f), "co-2"), listOf()),
                        Patient("", "", "", Coordinate(listOf(1f, 1f), "co-3"), listOf())
                )
        )
        val result1 = SalesmanSetUtils(pathQueryResolver).findBestPathUsingGeneticAlgorythm(salesmanSet!!, 1, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result2 = SalesmanSetUtils(pathQueryResolver).findBestPathUsingGeneticAlgorythm(salesmanSet!!, 1, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result3 = SalesmanSetUtils(pathQueryResolver).findBestPathUsingGeneticAlgorythm(salesmanSet!!, 1, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result4 = SalesmanSetUtils(pathQueryResolver).findBestPathUsingGeneticAlgorythm(salesmanSet!!, 1, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result5 = SalesmanSetUtils(pathQueryResolver).findBestPathUsingGeneticAlgorythm(salesmanSet!!, 1, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result6 = SalesmanSetUtils(pathQueryResolver).findBestPathUsingGeneticAlgorythm(salesmanSet!!, 1, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result7 = SalesmanSetUtils(pathQueryResolver).findBestPathUsingGeneticAlgorythm(salesmanSet!!, 1, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result8 = SalesmanSetUtils(pathQueryResolver).findBestPathUsingGeneticAlgorythm(salesmanSet!!, 1, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result9 = SalesmanSetUtils(pathQueryResolver).findBestPathUsingGeneticAlgorythm(salesmanSet!!, 1, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/
        val result10 = SalesmanSetUtils(pathQueryResolver).findBestPathUsingGeneticAlgorythm(salesmanSet!!, 1, 200, 10)  //to nie dziala, czasem zwraca krótsze sciezki :/

        val path1: Path = Path(listOf(
//                coordinateQueryResolver.findOneByLocation(listOf(2.3f, 2.4f)),
//                coordinateQueryResolver.findOneByLocation(listOf(1f, 1f)),
//                coordinateQueryResolver.findOneByLocation(listOf(0f, 0f)),
//                coordinateQueryResolver.findOneByLocation(listOf(44f, 55.6f))

                Patient("", "", "", Coordinate(listOf(2.3f, 2.4f), "co-0"), listOf()),
                Patient("", "", "", Coordinate(listOf(1f, 1f), "co-3"), listOf()),
                Patient("", "", "", Coordinate(listOf(0f, 0f), "co-2"), listOf()),
                Patient("", "", "", Coordinate(listOf(44f, 55.6f), "co-1"), listOf())
        ))
        path1.value = pathQueryResolver.calcPathValue(path1.places.map { patient -> patient.coordinate })

        assertTrue(path1.value.toDouble() >= result1.paths[0].value.toDouble())
        assertTrue(path1.value.toDouble() >= result2.paths[0].value.toDouble())
        assertTrue(path1.value.toDouble() >= result3.paths[0].value.toDouble())
        assertTrue(path1.value.toDouble() >= result4.paths[0].value.toDouble())
        assertTrue(path1.value.toDouble() >= result5.paths[0].value.toDouble())
        assertTrue(path1.value.toDouble() >= result6.paths[0].value.toDouble())
        assertTrue(path1.value.toDouble() >= result7.paths[0].value.toDouble())
        assertTrue(path1.value.toDouble() >= result8.paths[0].value.toDouble())
        assertTrue(path1.value.toDouble() >= result9.paths[0].value.toDouble())
        assertTrue(path1.value.toDouble() >= result10.paths[0].value.toDouble())
    }

    @Test
    fun testMutate() {
        val path = pathMutationResolver.newPath(listOf(
//                Coordinate(listOf(2.3f, 2.4f)),
//                Coordinate(listOf(1f, 1f)),
//                Coordinate(listOf(0f, 0f)),
//                Coordinate(listOf(44f, 55.6f))
                Patient("", "", "", Coordinate(listOf(2.3f, 2.4f), "co-0"), listOf()),
                Patient("", "", "", Coordinate(listOf(1f, 1f), "co-3"), listOf()),
                Patient("", "", "", Coordinate(listOf(0f, 0f), "co-2"), listOf()),
                Patient("", "", "", Coordinate(listOf(44f, 55.6f), "co-1"), listOf())
        ))
        val result = path?.let { SalesmanSetUtils(pathQueryResolver).mutate(it, 100, 100) }
        assertEquals(path?.places!!.distinct().size, result?.places!!.distinct().size)
    }

//    @Test
//    fun check_6_1_datasetGreedyValue() {
//        val salesmanSet = salesmanSetMutationResolver.newSalesmanSetByDistance(6, listOf(
//                0.0f, 20.0f, 30.0f, 31.0f, 28.0f, 40.0f,
//                30.0f, 0.0f, 10.0f, 14.0f, 20.0f, 44.0f,
//                40.0f, 20.0f, 0.0f, 10.0f, 22.0f, 50.0f,
//                41.0f, 24.0f, 20.0f, 0.0f, 14.0f, 42.0f,
//                38.0f, 30.0f, 32.0f, 24.0f, 0.0f, 28.0f,
//                50.0f, 54.0f, 60.0f, 52.0f, 38.0f, 0.0f
//        ))
//
//        val result = salesmanSetMutationResolver.findGreedyPath(salesmanSet!!.id)
//        assertEquals(132.0f, result.value)
//    }

    @Test
    fun check_burma14_datasetGreedyValue() {
        val salesmanSet = salesmanSetMutationResolver.newSalesmanSet(
                listOf(
                        Patient("", "", "", Coordinate(listOf(16.47f - 90.0f, 96.10f - 90.0f), "co-birma-1"), listOf()),
                        Patient("", "", "", Coordinate(listOf(16.47f - 90.0f, 94.44f - 90.0f), "co-birma-2"), listOf()),
                        Patient("", "", "", Coordinate(listOf(20.09f - 90.0f, 92.54f - 90.0f), "co-birma-3"), listOf()),
                        Patient("", "", "", Coordinate(listOf(22.39f - 90.0f, 93.37f - 90.0f), "co-birma-4"), listOf()),
                        Patient("", "", "", Coordinate(listOf(25.23f - 90.0f, 97.24f - 90.0f), "co-birma-5"), listOf()),
                        Patient("", "", "", Coordinate(listOf(22.00f - 90.0f, 96.05f - 90.0f), "co-birma-6"), listOf()),
                        Patient("", "", "", Coordinate(listOf(20.47f - 90.0f, 97.02f - 90.0f), "co-birma-7"), listOf()),
                        Patient("", "", "", Coordinate(listOf(17.20f - 90.0f, 96.29f - 90.0f), "co-birma-8"), listOf()),
                        Patient("", "", "", Coordinate(listOf(16.30f - 90.0f, 97.38f - 90.0f), "co-birma-9"), listOf()),
                        Patient("", "", "", Coordinate(listOf(14.05f - 90.0f, 98.12f - 90.0f), "co-birma-10"), listOf()),
                        Patient("", "", "", Coordinate(listOf(16.53f - 90.0f, 97.38f - 90.0f), "co-birma-11"), listOf()),
                        Patient("", "", "", Coordinate(listOf(21.52f - 90.0f, 95.59f - 90.0f), "co-birma-12"), listOf()),
                        Patient("", "", "", Coordinate(listOf(19.41f - 90.0f, 97.13f - 90.0f), "co-birma-13"), listOf()),
                        Patient("", "", "", Coordinate(listOf(20.09f - 90.0f, 94.55f - 90.0f), "co-birma-14"), listOf())
                )
        )

        val result = salesmanSetMutationResolver.findGreedyPath(salesmanSet!!.id)
        assertEquals(4286.352.toDouble(), result.value.toDouble(), 0.001)
    }


    @Test
    fun check_6_1_datasetGeneticValue() {
        val salesmanSet = salesmanSetMutationResolver.newSalesmanSetByDistance(6, listOf(
                0.0f, 20.0f, 30.0f, 31.0f, 28.0f, 40.0f,
                30.0f, 0.0f, 10.0f, 14.0f, 20.0f, 44.0f,
                40.0f, 20.0f, 0.0f, 10.0f, 22.0f, 50.0f,
                41.0f, 24.0f, 20.0f, 0.0f, 14.0f, 42.0f,
                38.0f, 30.0f, 32.0f, 24.0f, 0.0f, 28.0f,
                50.0f, 54.0f, 60.0f, 52.0f, 38.0f, 0.0f
        ))

        val result = SalesmanSetUtils(pathQueryResolver).findBestPathUsingGeneticAlgorythm(salesmanSet!!, 1, 200, 10)
        val greedy = salesmanSetMutationResolver.findGreedyPath(salesmanSet.id)
        println("check_6_1_datasetGeneticValue result: " + result.paths[0].value)
        assertTrue(greedy.value.toDouble() >= result.paths[0].value.toDouble())
        assertTrue(132.0 <= result.paths[0].value.toDouble())
    }

    @Test
    fun check_burma14_dataseGenetictValue() {
        val salesmanSet = salesmanSetMutationResolver.newSalesmanSet(
                listOf(
                        Patient("", "", "", Coordinate(listOf(16.47f - 90.0f, 96.10f - 90.0f), "co-birma-1"), listOf()),
                        Patient("", "", "", Coordinate(listOf(16.47f - 90.0f, 94.44f - 90.0f), "co-birma-2"), listOf()),
                        Patient("", "", "", Coordinate(listOf(20.09f - 90.0f, 92.54f - 90.0f), "co-birma-3"), listOf()),
                        Patient("", "", "", Coordinate(listOf(22.39f - 90.0f, 93.37f - 90.0f), "co-birma-4"), listOf()),
                        Patient("", "", "", Coordinate(listOf(25.23f - 90.0f, 97.24f - 90.0f), "co-birma-5"), listOf()),
                        Patient("", "", "", Coordinate(listOf(22.00f - 90.0f, 96.05f - 90.0f), "co-birma-6"), listOf()),
                        Patient("", "", "", Coordinate(listOf(20.47f - 90.0f, 97.02f - 90.0f), "co-birma-7"), listOf()),
                        Patient("", "", "", Coordinate(listOf(17.20f - 90.0f, 96.29f - 90.0f), "co-birma-8"), listOf()),
                        Patient("", "", "", Coordinate(listOf(16.30f - 90.0f, 97.38f - 90.0f), "co-birma-9"), listOf()),
                        Patient("", "", "", Coordinate(listOf(14.05f - 90.0f, 98.12f - 90.0f), "co-birma-10"), listOf()),
                        Patient("", "", "", Coordinate(listOf(16.53f - 90.0f, 97.38f - 90.0f), "co-birma-11"), listOf()),
                        Patient("", "", "", Coordinate(listOf(21.52f - 90.0f, 95.59f - 90.0f), "co-birma-12"), listOf()),
                        Patient("", "", "", Coordinate(listOf(19.41f - 90.0f, 97.13f - 90.0f), "co-birma-13"), listOf()),
                        Patient("", "", "", Coordinate(listOf(20.09f - 90.0f, 94.55f - 90.0f), "co-birma-14"), listOf())
                )
        )

        val result = SalesmanSetUtils(pathQueryResolver).findBestPathUsingGeneticAlgorythm(salesmanSet!!, 10, 200, 10)
        val greedy = salesmanSetMutationResolver.findGreedyPath(salesmanSet.id).value
        println("check_burma14_dataseGenetictValue result: " + result.paths[0].value + "greedy:" + greedy)
        assertTrue(greedy.toDouble() >= result.paths[0].value.toDouble())
        assertTrue(3323.0 <= result.paths[0].value.toDouble())
    }

}
