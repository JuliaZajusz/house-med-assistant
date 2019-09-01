package com.shacky.housemedassistant

import com.shacky.housemedassistant.entity.Coordinate
import com.shacky.housemedassistant.repository.PlaceRepository
import com.shacky.housemedassistant.resolvers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


@Component
class DatabaseInit() {
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


    @PostConstruct
    fun init() {
//        LOG.info("TUUUUUUUUUUUUUUUUUUUUUUUU")
        println("TUUUUUUUUUUUUUUUUUUUUUUUU")

        placeMutationResolver.newPlace("Big Ben", listOf(-0.1268194f, 51.5007292f))
        salesmanSetMutationResolver.newSalesmanSet(
                listOf(Coordinate(listOf(2.3f, 2.4f)),
                        Coordinate(listOf(44f, 55.6f)),
                        Coordinate(listOf(0f, 0f)),
                        Coordinate(listOf(1f, 1f))
                )
        )
    }
}
