package com.railshift.passenger.data.repository

import com.railshift.passenger.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeRailshiftRepository : RailshiftRepository {

    private val stations = listOf(
        Station(code = "BZA", name = "Vijayawada Jn", city = "Vijayawada"),
        Station(code = "MAS", name = "Chennai Central", city = "Chennai"),
        Station(code = "GNT", name = "Guntur Jn", city = "Guntur"),
        Station(code = "HYB", name = "Hyderabad", city = "Hyderabad"),
        Station(code = "SC", name = "Secunderabad", city = "Secunderabad"),
        Station(code = "VSKP", name = "Visakhapatnam", city = "Visakhapatnam"),
        Station(code = "RJY", name = "Rajahmundry", city = "Rajahmundry")
    )

    private val upcomingTripsFlow = MutableStateFlow(
        listOf(
            Trip(
                id = "trip-1",
                pnr = "4218903512",
                dateText = "Mon, 12 Oct",
                trainNumber = "12711",
                trainName = "Pinakini Express",
                fromCode = "BZA",
                toCode = "MAS",
                departureTime = "06:25",
                arrivalTime = "12:40",
                status = TripStatus.CONFIRMED,
                coach = "B3",
                berth = "24 (LB)",
                passengers = 1,
                totalFare = 640
            )
        )
    )

    private val pastTripsFlow = MutableStateFlow(
        listOf(
            Trip(
                id = "trip-2",
                pnr = "6127891234",
                dateText = "Sun, 04 Oct",
                trainNumber = "17215",
                trainName = "Visakhapatnam Express",
                fromCode = "VSKP",
                toCode = "BZA",
                departureTime = "14:00",
                arrivalTime = "20:00",
                status = TripStatus.COMPLETED,
                coach = "S4",
                berth = "32 (MB)",
                passengers = 2,
                totalFare = 560
            ),
            Trip(
                id = "trip-3",
                pnr = "8890124567",
                dateText = "Fri, 25 Sep",
                trainNumber = "12839",
                trainName = "Howrah Mail",
                fromCode = "VSKP",
                toCode = "BZA",
                departureTime = "09:30",
                arrivalTime = "16:30",
                status = TripStatus.COMPLETED,
                coach = "A1",
                berth = "12 (LB)",
                passengers = 1,
                totalFare = 960
            )
        )
    )

    private val userProfileFlow = MutableStateFlow(
        UserProfile(
            name = "Ravi Kumar",
            email = "ravi.kumar@example.com",
            initials = "RK",
            walletBalance = 1240.00
        )
    )

    override fun getStations(): List<Station> = stations

    override fun getStationByCode(code: String): Station? {
        return stations.find { it.code.equals(code, ignoreCase = true) }
    }

    override fun getUpcomingTrips(): Flow<List<Trip>> = upcomingTripsFlow.asStateFlow()

    override fun getPastTrips(): Flow<List<Trip>> = pastTripsFlow.asStateFlow()

    override fun getRecentSearches(): List<RecentSearch> = listOf(
        RecentSearch(fromCode = "BZA", toCode = "HYB", dateText = "Sat, 17 Oct", classCode = "3A"),
        RecentSearch(fromCode = "BZA", toCode = "VSKP", dateText = "Fri, 23 Oct", classCode = "SL")
    )

    override fun getRecentUnreservedTickets(): List<RecentUnreservedTicket> = listOf(
        RecentUnreservedTicket(fromCode = "BZA", toCode = "GNT", passengersText = "2 adults", dateText = "28 Sep"),
        RecentUnreservedTicket(fromCode = "BZA", toCode = "RJY", passengersText = "1 adult", dateText = "14 Sep")
    )

    override fun getRecentPlatformStations(): List<Station> = listOf(
        Station(code = "BZA", name = "Vijayawada Jn", city = "Vijayawada"),
        Station(code = "MAS", name = "Chennai Central", city = "Chennai"),
        Station(code = "HYB", name = "Hyderabad", city = "Hyderabad"),
        Station(code = "SC", name = "Secunderabad", city = "Secunderabad")
    )

    override fun searchTrains(
        fromCode: String,
        toCode: String,
        date: String,
        trainClass: TrainClass?,
        availableOnly: Boolean
    ): List<Train> {
        val fromStation = getStationByCode(fromCode) ?: stations[0]
        val toStation = getStationByCode(toCode) ?: stations[1]

        val allMockTrains = listOf(
            Train(
                number = "12711",
                name = "Pinakini Express",
                fromStation = fromStation,
                toStation = toStation,
                departureTime = "06:25",
                arrivalTime = "12:40",
                duration = "6h 15m",
                classes = listOf(
                    ClassOption(TrainClass.SL, fare = 280, availableBerths = 38),
                    ClassOption(TrainClass.THREE_A, fare = 640, availableBerths = 22),
                    ClassOption(TrainClass.TWO_A, fare = 960, availableBerths = 9),
                    ClassOption(TrainClass.CC, fare = 420, availableBerths = 45)
                )
            ),
            Train(
                number = "12727",
                name = "Godavari Express",
                fromStation = fromStation,
                toStation = toStation,
                departureTime = "17:15",
                arrivalTime = "23:45",
                duration = "6h 30m",
                classes = listOf(
                    ClassOption(TrainClass.SL, fare = 280, availableBerths = 14),
                    ClassOption(TrainClass.THREE_A, fare = 640, availableBerths = 6),
                    ClassOption(TrainClass.TWO_A, fare = 960, availableBerths = 0),
                    ClassOption(TrainClass.ONE_A, fare = 1520, availableBerths = 4)
                )
            ),
            Train(
                number = "12704",
                name = "Falaknuma Express",
                fromStation = fromStation,
                toStation = toStation,
                departureTime = "21:30",
                arrivalTime = "04:15",
                duration = "6h 45m",
                classes = listOf(
                    ClassOption(TrainClass.SL, fare = 280, availableBerths = 52),
                    ClassOption(TrainClass.THREE_A, fare = 640, availableBerths = 18),
                    ClassOption(TrainClass.TWO_A, fare = 960, availableBerths = 11)
                )
            )
        )

        return allMockTrains.filter { train ->
            if (availableOnly) {
                train.classes.any { it.availableBerths > 0 }
            } else true
        }
    }

    override suspend fun bookUnreservedTicket(
        fromCode: String,
        toCode: String,
        adults: Int,
        children: Int
    ): Result<Trip> {
        val fare = (adults * 30) + (children * 15)
        val newTrip = Trip(
            id = "unres-${System.currentTimeMillis()}",
            pnr = "UTS${(10000000..99999999).random()}",
            dateText = "Today",
            trainNumber = "General UTS",
            trainName = "Unreserved Superfast/Mail",
            fromCode = fromCode,
            toCode = toCode,
            departureTime = "Valid 3 hrs",
            arrivalTime = "Same day",
            status = TripStatus.CONFIRMED,
            coach = "GEN",
            berth = "Any coach",
            passengers = adults + children,
            totalFare = fare
        )
        upcomingTripsFlow.update { listOf(newTrip) + it }
        return Result.success(newTrip)
    }

    override fun getScannedPaperTicket(ticketNumber: String): PaperTicket? {
        return PaperTicket(
            ticketNumber = if (ticketNumber.isNotBlank()) ticketNumber else "73205184",
            fromCode = "BZA",
            fromCity = "Vijayawada",
            toCode = "MAS",
            toCity = "Chennai",
            passengersText = "2 adults",
            passengersCount = 2,
            farePaid = 190,
            isValid = true
        )
    }

    override suspend fun upgradeTicket(
        ticketNumber: String,
        trainNumber: String,
        targetClass: TrainClass
    ): Result<Trip> {
        val diffPerPerson = when (targetClass) {
            TrainClass.SL -> 280
            TrainClass.THREE_A -> 640
            TrainClass.TWO_A -> 960
            TrainClass.ONE_A -> 1350
            TrainClass.CC -> 350
        }
        val upgradedTrip = Trip(
            id = "upg-${System.currentTimeMillis()}",
            pnr = "UPG${(10000000..99999999).random()}",
            dateText = "Today",
            trainNumber = trainNumber,
            trainName = "12711 • Pinakini Express",
            fromCode = "BZA",
            toCode = "MAS",
            departureTime = "06:25",
            arrivalTime = "12:40",
            status = TripStatus.CONFIRMED,
            coach = when (targetClass) {
                TrainClass.SL -> "S2"
                TrainClass.THREE_A -> "B2"
                TrainClass.TWO_A -> "A1"
                TrainClass.ONE_A -> "H1"
                TrainClass.CC -> "C1"
            },
            berth = "18 (LB), 19 (MB)",
            passengers = 2,
            totalFare = diffPerPerson * 2
        )
        upcomingTripsFlow.update { listOf(upgradedTrip) + it }
        return Result.success(upgradedTrip)
    }

    override suspend fun bookPlatformTicket(stationCode: String, date: String): Result<String> {
        val ticketId = "PF-${stationCode}-${(100000..999999).random()}"
        return Result.success(ticketId)
    }

    override fun getUserProfile(): Flow<UserProfile> = userProfileFlow.asStateFlow()

    override suspend fun updateProfile(name: String, email: String) {
        val initials = name.split(" ")
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
            .ifEmpty { "RK" }

        userProfileFlow.update {
            it.copy(name = name, email = email, initials = initials)
        }
    }

    override suspend fun addWalletMoney(amount: Double) {
        userProfileFlow.update {
            it.copy(walletBalance = it.walletBalance + amount)
        }
    }

    override fun checkPNR(pnr: String): Trip? {
        val clean = pnr.trim()
        val allTrips = upcomingTripsFlow.value + pastTripsFlow.value
        return allTrips.find { it.pnr.contains(clean, ignoreCase = true) } ?: Trip(
            id = "pnr-res",
            pnr = clean.ifEmpty { "4218903512" },
            dateText = "Mon, 12 Oct 2026",
            trainNumber = "12711",
            trainName = "Pinakini Express",
            fromCode = "BZA",
            toCode = "MAS",
            departureTime = "06:25",
            arrivalTime = "12:40",
            status = TripStatus.CONFIRMED,
            coach = "B3",
            berth = "24 (LB)",
            passengers = 1,
            totalFare = 640
        )
    }

    override fun getCoachLayout(trainNumber: String): String {
        return "Engine -> SLR -> GS -> S1 -> S2 -> S3 -> S4 -> S5 -> S6 -> B1 -> B2 -> B3 -> B4 -> A1 -> GS -> SLR"
    }

    override fun trackTrain(trainNumber: String): String {
        return "12711 Pinakini Express: Running on time. Departed BZA at 06:25. Next halt: Tenali Jn (06:55)."
    }
}
