package com.shacky.housemedassistant.resolvers

import com.shacky.housemedassistant.entity.Path
import com.shacky.housemedassistant.entity.Patient
import com.shacky.housemedassistant.entity.SalesmanSet
import java.util.concurrent.ThreadLocalRandom

//@Component
class SalesmanSetUtils(val pathQueryResolver: PathQueryResolver) {


    fun findBestPathUsingGeneticAlgorythm(oldSalesmanSet: SalesmanSet, timeInSec: Int, populationSize: Int, parentPopulationSize: Int): SalesmanSet {
        var salesmanSet: SalesmanSet = oldSalesmanSet;
//        val greedyPath = findGreedyPath(salesmanSet.id)
        val greedyPath = salesmanSet.paths[0]
        if (!salesmanSet.paths.contains(greedyPath)) {
            salesmanSet.paths.add(greedyPath)
        }
        val start = System.currentTimeMillis();
        val end = start + timeInSec * 1000; // 60 seconds * 1000 ms/sec
        while (System.currentTimeMillis() < end) {
            salesmanSet = doGenetic(salesmanSet, populationSize, parentPopulationSize)
        }
        salesmanSet.paths = salesmanSet.paths.distinct().sortedBy { it.value.toFloat() }.toMutableList()
        if (salesmanSet.paths.size > 10) {
            salesmanSet.paths = salesmanSet.paths.subList(0, 10)  //to chyba nie działa
        }
//        salesmanSet.population = salesmanSet.population.distinct().toMutableList()  //desperacja
        salesmanSet.population = mutableListOf()  //desperacja
        salesmanSet.parentPopulation = mutableListOf()  //desperacja
        return salesmanSet
//        updateSalesmanSet(salesmanSet)
//        return salesmanSet.paths.first()
    }

    fun doGenetic(salesmanSet: SalesmanSet, populationSize: Int, parentPopulationSize: Int, mutationsProbability: Int = 1, swapsInMutation: Int = 10): SalesmanSet {
        println("doGenetic")
        var newSalesmanSet = salesmanSet
        newSalesmanSet.population = mutableListOf()
        if (salesmanSet.paths.isNotEmpty()) {
            newSalesmanSet.population.add(salesmanSet.paths.first())
        }
        //create population
        var genomPath = newSalesmanSet.places
        for (i in newSalesmanSet.population.size until populationSize) {
            genomPath = getRandomGenom(genomPath)
            val value = pathQueryResolver.calcPathValue(genomPath.map { patient -> patient.coordinate })
            newSalesmanSet.population.add(Path(genomPath, value))
        }
        //rob
            //jesli warunek konca nie zostal osiagniety
                newSalesmanSet.population = newSalesmanSet.population.distinct().toMutableList()
                //        sortuj populację
                newSalesmanSet.population = newSalesmanSet.population.sortedBy { it.value.toFloat() }.toMutableList()
        println("while true")
//        for (entity in newSalesmanSet.population) {
//            var s:String = "";
//            entity.places.map{ patient -> patient.lastName}
//                    .forEach { s+= ", " + it }
//            println("${entity.value}: $s")
//        }
        //        jesli jest za duża to usuń część osobników
                if (newSalesmanSet.population.size > populationSize) {
                    newSalesmanSet.population = newSalesmanSet.population.subList(0, populationSize - 1)
                }
//        wybierz populację rodzicielską (ruletka, turniej)
                newSalesmanSet.parentPopulation = chooseParentPopulationByRouletteMethod(newSalesmanSet.population, parentPopulationSize)
//        krzyżuj
                newSalesmanSet = reproducePopulation(newSalesmanSet, mutationsProbability, swapsInMutation)
//        mutuj


//        sortuj populację
                newSalesmanSet.population = newSalesmanSet.population.sortedBy { it.value.toFloat() } as MutableList<Path>
        var s: String = "";
        newSalesmanSet.population[0].places.map { patient -> patient.lastName }
                .forEach { s += ", " + it }
//        println("pierwsza w posortowanej populacji: ${newSalesmanSet.population[0].value}: $s")
//        zwróc najlepszą ścieżkę (dodaj do paths)
                newSalesmanSet.paths.add(newSalesmanSet.population.first())
        newSalesmanSet.paths = newSalesmanSet.paths.sortedBy { it.value.toFloat() }.toMutableList()
//        for (index in 0 until newSalesmanSet.paths.size) {
        for (index in 0 until 1) {
            val path = newSalesmanSet.paths[index]
            var p: String = "";
            path.places.map { patient -> patient.lastName }
                    .forEach { p += ", " + it }
//            println("$index ::aktualne paths: ${path.value}: $p")
        }

        return newSalesmanSet
    }

    private fun reproducePopulation(salesmanSet: SalesmanSet, mutationsProbability: Int, swapsInMutation: Int): SalesmanSet {
        var updatedSalesmanSet = salesmanSet
        val populationSize = salesmanSet.population.size
        val parentPopulationSize = salesmanSet.parentPopulation.size
        val parentCouples = salesmanSet.parentPopulation.size / 2;
        val pathSize = salesmanSet.places.size;
        for (i in 0 until parentCouples)
        //dla kazdej pary rodzicow utworz pare dzieci
        {
            val crossPoint1: Int = ThreadLocalRandom.current().nextInt(0, pathSize - 1)
            val crossPoint2: Int = ThreadLocalRandom.current().nextInt(crossPoint1, pathSize - 1)

            val parentOneGenom = salesmanSet.parentPopulation[2 * i]
            val parentTwoGenom = salesmanSet.parentPopulation[2 * i + 1]
            var a = crossover(parentOneGenom, parentTwoGenom, crossPoint1, crossPoint2)  //dziecko1
            var b = crossover(parentTwoGenom, parentOneGenom, crossPoint1, crossPoint2)  //dziecko2
            a = mutate(a, mutationsProbability, swapsInMutation)
            b = mutate(b, mutationsProbability, swapsInMutation)
            if (a.places.size != salesmanSet.places.size || b.places.size != salesmanSet.places.size || a.places.distinct().size < salesmanSet.places.size || b.places.distinct().size < salesmanSet.places.size) {
                println("" + crossPoint1 + " " + crossPoint2)
                throw Exception()
            }

            var duplicates = a.places.map { patient -> patient.lastName }
                    .groupingBy { it }.eachCount().filter { it.value > 1 }
//            println("Iteracja $i:")
            if (duplicates.size > 0) {
//                var p1: String = "";
//                parentOneGenom.places.map { patient -> patient.lastName }
//                        .forEach { p1 += ", " + it }
//                println("parent1: ${parentOneGenom.value}: $p1")
//                var p2: String = "";
//                parentTwoGenom.places.map { patient -> patient.lastName }
//                        .forEach { p2 += ", " + it }
//                println("parent2: ${parentTwoGenom.value}: $p2")
//                println("crossPoint1: ${crossPoint1}, crossPoint2 $crossPoint2")
//                var p: String = "";
//                a.places.map { patient -> patient.lastName }
//                        .forEach { p += ", " + it }
//                println("a: ${a.value}: $p")
            } else {
                updatedSalesmanSet.population.add(a)
            }

            duplicates = b.places.map { patient -> patient.lastName }
                    .groupingBy { it }.eachCount().filter { it.value > 1 }
//            println("Counting first letters:")
            if (duplicates.size > 0) {
//                var p1: String = "";
//                parentOneGenom.places.map { patient -> patient.lastName }
//                        .forEach { p1 += ", " + it }
//                println("parent1: ${parentOneGenom.value}: $p1")
//                var p2: String = "";
//                parentTwoGenom.places.map { patient -> patient.lastName }
//                        .forEach { p2 += ", " + it }
//                println("parent2: ${parentTwoGenom.value}: $p2")
//                println("crossPoint1: ${crossPoint1}, crossPoint2 $crossPoint2")
//                var p: String = "";
//                b.places.map { patient -> patient.lastName }
//                        .forEach { p += ", " + it }
//                println("b: ${b.value}: $p")
            } else {
                updatedSalesmanSet.population.add(b)
            }


        }
        return updatedSalesmanSet;
    }

    private fun crossover(parentOneGenom: Path, parentTwoGenom: Path, crossPoint1: Int, crossPoint2: Int): Path {
//        var p1: String = "";
//        parentOneGenom.places.map { patient -> patient.lastName }
//                .forEach { p1 += ", " + it }
//        println("parent1: ${parentOneGenom.value}: $p1")
//        var p2: String = "";
//        parentTwoGenom.places.map { patient -> patient.lastName }
//                .forEach { p2 += ", " + it }
//        println("parent2: ${parentTwoGenom.value}: $p2")
//        println("crossPoint1: ${crossPoint1}, crossPoint2 $crossPoint2")


        val pathSize = parentTwoGenom.places.size
        val newPath: MutableList<Patient?> = mutableListOf<Patient?>();
        val checkForRepeat = mutableListOf<Patient>()
        var notUsed = mutableListOf<Patient>()

        for (i in 0 until pathSize) { //wypelnij pustymi
            newPath.add(null)
        }

//        println("srodek")
        for (i in crossPoint1 until crossPoint2)                //srodek
        {
//            checkForRepeat.add(parentTwoGenom.places[i])
            newPath[i] = parentTwoGenom.places[i]  //środek
//            println("newPath[$i]=${parentTwoGenom.places[i].lastName}")
        }

//        println("pocztek")
        for (i in 0 until crossPoint1)                //poczatek
        {
            val zm = parentOneGenom.places[i];

            if (!newPath.filterNotNull().map { patient -> patient!!.coordinate.id }.contains(zm.coordinate.id)) {
                newPath[i] = zm;
//                println("newPath[$i]=${zm.lastName}")
            } else {
                notUsed.add(zm)
//                println("$i notUsed.add(${zm.lastName})")
            }
        }

//        println("koniec")
        for (i in crossPoint2 until pathSize)                //koniec
        {
            val zm = parentOneGenom.places[i];

            if (!newPath.filterNotNull().map { patient -> patient.coordinate.id }.contains(zm.coordinate.id)) {
                newPath[i] = zm;
//                println("newPath[$i]=${zm.lastName}")
            } else {
                notUsed.add(zm)
//                println("$i notUsed.add(${zm.lastName})")
            }
        }
        notUsed = parentTwoGenom.places.toMutableList();
//        println("notUsed1---------")
//        notUsed.forEach { patient -> println(patient!!.lastName) }
        notUsed = notUsed.filter { patient -> !newPath.filterNotNull().map { patient -> patient.coordinate.id }.contains(patient.coordinate.id) }.toMutableList()
//        println("notUsed2---------")
//        notUsed.forEach { patient -> println(patient!!.lastName) }
//        println("newPath---------")
//        newPath.forEach { patient -> println(patient == null) }
//        newPath.filterNotNull().forEach { patient -> println(patient.lastName) }

        for (index in 0 until newPath.size)            //wypelnienie pustych pozostałymi
        {
            val patient = newPath[index]
            if (patient == null) {
                val foundedPatient = notUsed[0]
                notUsed.removeAt(0)
                newPath[index] = foundedPatient;
            }
        }

        val pathValue: Float = pathQueryResolver.calcPathValue(newPath.filterNotNull().map { patient -> patient.coordinate })
//        println("pathValue: $pathValue")
        val child: Path = Path(newPath.filterNotNull(), pathValue);
        return child;
    }

//    private fun crossover(parentOneGenom: Path, parentTwoGenom: Path, crossPoint1: Int, crossPoint2: Int): Path {
//        val pathSize = parentTwoGenom.places.size
//        val childListOfCoordinates: MutableList<Patient?> = parentTwoGenom.places.toMutableList()  //środek
//        val checkForRepeat = mutableListOf<Patient>()
//
//        for (i in crossPoint1 until crossPoint2)                //srodek
//        {
//            checkForRepeat.add(parentTwoGenom.places[i])
//        }
//
//        for (i in 0 until crossPoint1)                //poczatek
//        {
//            val zm = parentOneGenom.places[i];
//
//            if (!checkForRepeat.contains(zm)) {
//                childListOfCoordinates[i] = zm;
//                checkForRepeat.add(zm);
//            } else {
//                childListOfCoordinates[i] = null;
//            }
//        }
//
//        for (i in crossPoint2 until pathSize)            //koniec
//        {
//            var zm: Patient? = null;
//            try {
//                zm = parentOneGenom.places[i];
//            } catch (e: IndexOutOfBoundsException) {
//                println("" + parentOneGenom + parentTwoGenom + crossPoint1 + crossPoint2)
//            }
//
//            if (!checkForRepeat.contains(zm)) {
//                childListOfCoordinates[i] = zm;
//                if (zm != null) {
//                    checkForRepeat.add(zm)
//                };
//            } else {
//                childListOfCoordinates[i] = null;
//            }
//        }
//
//        for (i in 0 until pathSize) {
//            if (childListOfCoordinates[i] == null) {
//                for (j in 0 until pathSize) {
//                    if (!checkForRepeat.contains(parentOneGenom.places[j])) {
//                        childListOfCoordinates[i] = parentOneGenom.places[j];
//                        checkForRepeat.add(parentOneGenom.places[j]);
//                        break;
//                    }
//                }
//
//            }
//        }
//        val pathValue: Float = pathQueryResolver.calcPathValue(childListOfCoordinates.filterNotNull().map { patient -> patient.coordinate })
//        val child: Path = Path(childListOfCoordinates.filterNotNull(), pathValue);
//        return child;
//    }

    fun mutate(genomPath: Path, mutationsProbability: Int, swapsInMutation: Int): Path {
        val chance = ThreadLocalRandom.current().nextInt(0, 100)
        val updatedGenomPath = genomPath.places.toMutableList()

        if (chance <= mutationsProbability) {
            for (i in 0 until swapsInMutation) {
                val city1 = ThreadLocalRandom.current().nextInt(0, updatedGenomPath.size)
                val city2 = ThreadLocalRandom.current().nextInt(0, updatedGenomPath.size)
                val temp = updatedGenomPath[city1];
                updatedGenomPath[city1] = updatedGenomPath[city2];
                updatedGenomPath[city2] = temp;
            }
        }
        val pathValue: Float = pathQueryResolver.calcPathValue(updatedGenomPath.map { patient -> patient.coordinate })
        return Path(updatedGenomPath, pathValue)
    }


    private fun getRandomGenom(genomPath: List<Patient>): List<Patient> {
        return genomPath.shuffled()
    }

    private fun chooseParentPopulationByRouletteMethod(population: List<Path>, parentPopulationSize: Int): List<Path> {
        var parentPopulation: MutableList<Path> = mutableListOf<Path>()
        var populationValueSum: Float = 0.0f;
        population.forEach { genomPath -> populationValueSum += 1 / genomPath.value.toFloat() }

        var rouletteList = mutableListOf<Float>(0.0f)
        population.forEach { genomPath ->
            val pathPercentage = (1 / genomPath.value.toFloat()) * (1 / populationValueSum) * 100
            rouletteList.add(rouletteList[rouletteList.size - 1] + pathPercentage)
        }

        for (i in 0 until parentPopulationSize) {
            val randomInteger = ThreadLocalRandom.current().nextInt(0, 100)

            loop@ for (i in 0 until rouletteList.size) {
                if (rouletteList[i] < randomInteger && (i + 1 == rouletteList.size || rouletteList[i + 1] > randomInteger)) {
                    parentPopulation.add(population[i])
                    break@loop
                }
            }

        }
        return parentPopulation
    }

}
