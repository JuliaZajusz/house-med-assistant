package com.shacky.housemedassistant.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.shacky.housemedassistant.entity.SalesmanSet
import com.shacky.housemedassistant.repository.SalesmanSetRepository
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Component

@Component
class SalesmanSetQueryResolver(val salesmanSetRepository: SalesmanSetRepository, val mongoOperations: MongoOperations) : GraphQLQueryResolver {
    fun salesmanSets(): List<SalesmanSet> {
        val list = salesmanSetRepository.findAll()
        return list;
    }
}
