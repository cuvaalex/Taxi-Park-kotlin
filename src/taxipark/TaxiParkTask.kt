package taxipark

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> =
        this.allDrivers.filter { driver -> this.trips.none {it.driver == driver} }
                .toSet()

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
        this.allPassengers.filter {
            passenger -> this.trips.count {passenger in it.passengers} >= minTrips}
                .toSet()

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
        this.allPassengers.filter {
            passenger -> this.trips.count { it.driver == driver && passenger in it.passengers } > 1
        }.toSet()

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> =

        trips.flatMap { trip -> trip.passengers.map { passenger -> passenger to trip } }
                .groupBy(Pair<Passenger, Trip>::first)
                .mapValues { (_, list) -> list.map { it.second } }
                .mapValues { (_, list) -> list.partition { trip -> (trip.discount ?: 0.0) > 0.0 } }
                .filter { (_, pair) -> pair.first.size > pair.second.size }
                .keys

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    val tripDurationRange: Int? = trips.map { it.duration }
            .groupBy { it / 10 }
            .maxBy { (_, durations) -> durations.size }
            ?.key

    return tripDurationRange?.let { it ->
        val start = it * 10
        val end = start + 9
        IntRange(start, end)
    }
}

/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    return if (trips.isEmpty()) {
        false
    } else {
        val descSortedIncomeByDriver = trips.groupBy { it.driver }
                .mapValues { (_, trips) -> trips.sumByDouble { it.cost } }
                .entries
                .sortedByDescending { it.value }

        val pareto20Threshold = allDrivers.size / 5                 // 20% of the drivers
        val pareto80Threshold = trips.sumByDouble { it.cost } * 0.8 // 80% of the income

        descSortedIncomeByDriver.take(pareto20Threshold).sumByDouble { it.value } >= pareto80Threshold
    }
}