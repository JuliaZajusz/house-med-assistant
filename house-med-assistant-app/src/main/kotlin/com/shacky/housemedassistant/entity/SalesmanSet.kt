package com.shacky.housemedassistant.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "salesmanSet")
data class SalesmanSet(
        var places: List<Coordinate>,
        var neighborhoodMatrix: List<Distance> = ArrayList() //distance between places - key: coordinate object id
//        var neighborhoodMatrix: Map<String, Map<String, Number>> = HashMap()  //distance between places - key: coordinate object id
//        var paths: List<Path> = ArrayList()
) {
    @Id
    var id: String = ""
    var paths: List<Path> = ArrayList()
    var population: MutableList<Path> = mutableListOf<Path>()

    fun doGenetic(populationSize: Int) {
        //create population
        var genomPath = places
        for (i in population.size..populationSize) {
            genomPath = mutateGenomPathBySwithingElements(genomPath)
            val value = pathMutationResolver.calcPathValue(genomPath)
            population.add(Path(genomPath, value))
        }

        //rob
        //jesli warunek konca nie zostal osiagniety
//        sortuj populację
//        jesli jest za duża to usuń część osobników
//        wybierz populację rodzicielską (ruletka, turniej)
//        krzyżuj
//        mutuj

        //jesli zostal osiagniety
//        sortuj populację
//        zwróc najlepszą ścieżkę (dodaj do paths)

        var i = 0;
        while (true) {
            if (i < 100) {
                this.population = this.population.sortedBy { it.value.toFloat() }.toMutableList()
                if (population.size > populationSize) {
                    population = population.subList(0, populationSize - 1)
                }


                i++;
            } else {
                break;
            }
        }

    }

    fun mutateGenomPathBySwithingElements(genomPath: List<Coordinate>): List<Coordinate> {
        return genomPath.shuffled()
    }
}
