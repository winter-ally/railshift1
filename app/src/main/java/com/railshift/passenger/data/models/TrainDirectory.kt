package com.railshift.passenger.data.models

object TrainDirectory {

    val STATIONS: List<Station> = listOf(
        Station(code = "BZA", name = "Vijayawada Jn", city = "Vijayawada"),
        Station(code = "MAS", name = "MGR Chennai Central", city = "Chennai"),
        Station(code = "HYB", name = "Hyderabad Deccan", city = "Hyderabad"),
        Station(code = "SC", name = "Secunderabad Jn", city = "Secunderabad"),
        Station(code = "VSKP", name = "Visakhapatnam Jn", city = "Visakhapatnam"),
        Station(code = "GNT", name = "Guntur Jn", city = "Guntur"),
        Station(code = "RJY", name = "Rajahmundry", city = "Rajahmundry"),
        Station(code = "NLR", name = "Nellore", city = "Nellore"),
        Station(code = "OGL", name = "Ongole", city = "Ongole"),
        Station(code = "GDR", name = "Gudur Jn", city = "Gudur"),
        Station(code = "NDLS", name = "New Delhi", city = "New Delhi"),
        Station(code = "NZM", name = "Hazrat Nizamuddin", city = "Delhi"),
        Station(code = "HWH", name = "Howrah Jn", city = "Kolkata"),
        Station(code = "SBC", name = "KSR Bengaluru", city = "Bengaluru"),
        Station(code = "MMCT", name = "Mumbai Central", city = "Mumbai"),
        Station(code = "CSMT", name = "Mumbai CSMT", city = "Mumbai"),
        Station(code = "PUNE", name = "Pune Jn", city = "Pune"),
        Station(code = "ADI", name = "Ahmedabad Jn", city = "Ahmedabad"),
        Station(code = "TVC", name = "Thiruvananthapuram Central", city = "Trivandrum"),
        Station(code = "ERS", name = "Ernakulam Jn", city = "Kochi"),
        Station(code = "BBS", name = "Bhubaneswar", city = "Bhubaneswar"),
        Station(code = "PNBE", name = "Patna Jn", city = "Patna"),
        Station(code = "CNB", name = "Kanpur Central", city = "Kanpur"),
        Station(code = "BPL", name = "Bhopal Jn", city = "Bhopal")
    )

    fun getStation(code: String): Station {
        return STATIONS.find { it.code.equals(code, ignoreCase = true) }
            ?: Station(code = code.uppercase(), name = "$code Station", city = code.uppercase())
    }

    val TRAINS: List<Train> = listOf(
        Train(
            number = "12711",
            name = "Pinakini SF Express",
            fromStation = getStation("BZA"),
            toStation = getStation("MAS"),
            departureTime = "06:10",
            arrivalTime = "13:05",
            duration = "6h 55m",
            runsOn = "Daily",
            classes = listOf(
                ClassOption(TrainClass.CC, fare = 485, availableBerths = 42),
                ClassOption(TrainClass.SL, fare = 240, availableBerths = 118)
            )
        ),
        Train(
            number = "12712",
            name = "Pinakini SF Express",
            fromStation = getStation("MAS"),
            toStation = getStation("BZA"),
            departureTime = "14:10",
            arrivalTime = "21:10",
            duration = "7h 00m",
            runsOn = "Daily",
            classes = listOf(
                ClassOption(TrainClass.CC, fare = 485, availableBerths = 35),
                ClassOption(TrainClass.SL, fare = 240, availableBerths = 95)
            )
        ),
        Train(
            number = "12727",
            name = "Godavari SF Express",
            fromStation = getStation("VSKP"),
            toStation = getStation("HYB"),
            departureTime = "17:20",
            arrivalTime = "06:15",
            duration = "12h 55m",
            runsOn = "Daily",
            classes = listOf(
                ClassOption(TrainClass.ONE_A, fare = 2240, availableBerths = 4),
                ClassOption(TrainClass.TWO_A, fare = 1340, availableBerths = 12),
                ClassOption(TrainClass.THREE_A, fare = 945, availableBerths = 34),
                ClassOption(TrainClass.SL, fare = 360, availableBerths = 68)
            )
        ),
        Train(
            number = "12728",
            name = "Godavari SF Express",
            fromStation = getStation("HYB"),
            toStation = getStation("VSKP"),
            departureTime = "17:05",
            arrivalTime = "05:45",
            duration = "12h 40m",
            runsOn = "Daily",
            classes = listOf(
                ClassOption(TrainClass.ONE_A, fare = 2240, availableBerths = 6),
                ClassOption(TrainClass.TWO_A, fare = 1340, availableBerths = 18),
                ClassOption(TrainClass.THREE_A, fare = 945, availableBerths = 45),
                ClassOption(TrainClass.SL, fare = 360, availableBerths = 82)
            )
        ),
        Train(
            number = "12704",
            name = "Falaknuma SF Express",
            fromStation = getStation("SC"),
            toStation = getStation("HWH"),
            departureTime = "15:55",
            arrivalTime = "17:40",
            duration = "25h 45m",
            runsOn = "Daily",
            classes = listOf(
                ClassOption(TrainClass.TWO_A, fare = 2450, availableBerths = 8),
                ClassOption(TrainClass.THREE_A, fare = 1710, availableBerths = 26),
                ClassOption(TrainClass.SL, fare = 650, availableBerths = 48)
            )
        ),
        Train(
            number = "12703",
            name = "Falaknuma SF Express",
            fromStation = getStation("HWH"),
            toStation = getStation("SC"),
            departureTime = "08:35",
            arrivalTime = "10:30",
            duration = "25h 55m",
            runsOn = "Daily",
            classes = listOf(
                ClassOption(TrainClass.TWO_A, fare = 2450, availableBerths = 11),
                ClassOption(TrainClass.THREE_A, fare = 1710, availableBerths = 32),
                ClassOption(TrainClass.SL, fare = 650, availableBerths = 55)
            )
        ),
        Train(
            number = "20833",
            name = "Vande Bharat Express",
            fromStation = getStation("VSKP"),
            toStation = getStation("SC"),
            departureTime = "05:45",
            arrivalTime = "14:15",
            duration = "8h 30m",
            runsOn = "Mon, Tue, Wed, Thu, Fri, Sat",
            classes = listOf(
                ClassOption(TrainClass.CC, fare = 1665, availableBerths = 88),
                ClassOption(TrainClass.ONE_A, fare = 3120, availableBerths = 24)
            )
        ),
        Train(
            number = "20834",
            name = "Vande Bharat Express",
            fromStation = getStation("SC"),
            toStation = getStation("VSKP"),
            departureTime = "15:00",
            arrivalTime = "23:30",
            duration = "8h 30m",
            runsOn = "Mon, Tue, Wed, Thu, Fri, Sat",
            classes = listOf(
                ClassOption(TrainClass.CC, fare = 1665, availableBerths = 76),
                ClassOption(TrainClass.ONE_A, fare = 3120, availableBerths = 19)
            )
        ),
        Train(
            number = "12603",
            name = "Hyderabad SF Express",
            fromStation = getStation("MAS"),
            toStation = getStation("HYB"),
            departureTime = "16:45",
            arrivalTime = "05:45",
            duration = "13h 00m",
            runsOn = "Daily",
            classes = listOf(
                ClassOption(TrainClass.TWO_A, fare = 1620, availableBerths = 14),
                ClassOption(TrainClass.THREE_A, fare = 1140, availableBerths = 38),
                ClassOption(TrainClass.SL, fare = 430, availableBerths = 72)
            )
        ),
        Train(
            number = "12604",
            name = "Chennai SF Express",
            fromStation = getStation("HYB"),
            toStation = getStation("MAS"),
            departureTime = "16:45",
            arrivalTime = "05:45",
            duration = "13h 00m",
            runsOn = "Daily",
            classes = listOf(
                ClassOption(TrainClass.TWO_A, fare = 1620, availableBerths = 16),
                ClassOption(TrainClass.THREE_A, fare = 1140, availableBerths = 41),
                ClassOption(TrainClass.SL, fare = 430, availableBerths = 85)
            )
        ),
        Train(
            number = "12434",
            name = "Chennai Rajdhani",
            fromStation = getStation("NZM"),
            toStation = getStation("MAS"),
            departureTime = "15:35",
            arrivalTime = "20:45",
            duration = "29h 10m",
            runsOn = "Wed, Fri",
            classes = listOf(
                ClassOption(TrainClass.ONE_A, fare = 4820, availableBerths = 8),
                ClassOption(TrainClass.TWO_A, fare = 3190, availableBerths = 22),
                ClassOption(TrainClass.THREE_A, fare = 2380, availableBerths = 54)
            )
        ),
        Train(
            number = "12622",
            name = "Tamil Nadu SF Express",
            fromStation = getStation("NDLS"),
            toStation = getStation("MAS"),
            departureTime = "21:05",
            arrivalTime = "06:15",
            duration = "33h 10m",
            runsOn = "Daily",
            classes = listOf(
                ClassOption(TrainClass.TWO_A, fare = 2840, availableBerths = 15),
                ClassOption(TrainClass.THREE_A, fare = 1960, availableBerths = 42),
                ClassOption(TrainClass.SL, fare = 750, availableBerths = 90)
            )
        ),
        Train(
            number = "12628",
            name = "Karnataka SF Express",
            fromStation = getStation("NDLS"),
            toStation = getStation("SBC"),
            departureTime = "20:20",
            arrivalTime = "12:00",
            duration = "39h 40m",
            runsOn = "Daily",
            classes = listOf(
                ClassOption(TrainClass.TWO_A, fare = 3120, availableBerths = 12),
                ClassOption(TrainClass.THREE_A, fare = 2150, availableBerths = 36),
                ClassOption(TrainClass.SL, fare = 820, availableBerths = 104)
            )
        ),
        Train(
            number = "12841",
            name = "Coromandel Express",
            fromStation = getStation("HWH"),
            toStation = getStation("MAS"),
            departureTime = "15:20",
            arrivalTime = "17:00",
            duration = "25h 40m",
            runsOn = "Daily",
            classes = listOf(
                ClassOption(TrainClass.TWO_A, fare = 2510, availableBerths = 18),
                ClassOption(TrainClass.THREE_A, fare = 1750, availableBerths = 45),
                ClassOption(TrainClass.SL, fare = 660, availableBerths = 78)
            )
        ),
        Train(
            number = "12952",
            name = "Mumbai Rajdhani Express",
            fromStation = getStation("NDLS"),
            toStation = getStation("MMCT"),
            departureTime = "16:55",
            arrivalTime = "08:35",
            duration = "15h 40m",
            runsOn = "Daily",
            classes = listOf(
                ClassOption(TrainClass.ONE_A, fare = 3980, availableBerths = 10),
                ClassOption(TrainClass.TWO_A, fare = 2650, availableBerths = 28),
                ClassOption(TrainClass.THREE_A, fare = 1980, availableBerths = 62)
            )
        ),
        Train(
            number = "17215",
            name = "Visakhapatnam Express",
            fromStation = getStation("VSKP"),
            toStation = getStation("BZA"),
            departureTime = "14:00",
            arrivalTime = "20:00",
            duration = "6h 00m",
            runsOn = "Daily",
            classes = listOf(
                ClassOption(TrainClass.THREE_A, fare = 620, availableBerths = 28),
                ClassOption(TrainClass.SL, fare = 230, availableBerths = 64)
            )
        ),
        Train(
            number = "12760",
            name = "Charminar SF Express",
            fromStation = getStation("HYB"),
            toStation = getStation("MAS"),
            departureTime = "18:00",
            arrivalTime = "08:00",
            duration = "14h 00m",
            runsOn = "Daily",
            classes = listOf(
                ClassOption(TrainClass.ONE_A, fare = 2390, availableBerths = 4),
                ClassOption(TrainClass.TWO_A, fare = 1430, availableBerths = 14),
                ClassOption(TrainClass.THREE_A, fare = 1010, availableBerths = 36),
                ClassOption(TrainClass.SL, fare = 380, availableBerths = 70)
            )
        )
    )

    fun searchStations(query: String): List<Station> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return STATIONS
        return STATIONS.filter {
            it.code.lowercase().contains(q) ||
            it.name.lowercase().contains(q) ||
            it.city.lowercase().contains(q)
        }
    }

    fun searchTrainsByQuery(query: String): List<Train> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return TRAINS
        return TRAINS.filter {
            it.number.contains(q) ||
            it.name.lowercase().contains(q) ||
            it.fromStation.code.lowercase().contains(q) ||
            it.fromStation.name.lowercase().contains(q) ||
            it.toStation.code.lowercase().contains(q) ||
            it.toStation.name.lowercase().contains(q)
        }
    }

    fun findTrainByNumberOrName(query: String): Train? {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return null
        return TRAINS.find { it.number == q }
            ?: TRAINS.find { it.name.lowercase().contains(q) }
            ?: TRAINS.find { it.number.contains(q) }
    }

    fun getTrainsBetween(fromCode: String, toCode: String, availableOnly: Boolean = false): List<Train> {
        val from = fromCode.trim().uppercase()
        val to = toCode.trim().uppercase()

        val matching = TRAINS.filter {
            it.fromStation.code.equals(from, ignoreCase = true) &&
            it.toStation.code.equals(to, ignoreCase = true)
        }

        if (matching.isNotEmpty()) {
            return if (availableOnly) {
                matching.filter { t -> t.classes.any { it.availableBerths > 0 } }
            } else matching
        }

        // Realistic fallback for ANY station combination
        val fromStn = getStation(from)
        val toStn = getStation(to)
        val generated = listOf(
            Train(
                number = "12${(100..999).random()}",
                name = "${fromStn.city} - ${toStn.city} Superfast Express",
                fromStation = fromStn,
                toStation = toStn,
                departureTime = "06:30",
                arrivalTime = "14:15",
                duration = "7h 45m",
                runsOn = "Daily",
                classes = listOf(
                    ClassOption(TrainClass.TWO_A, fare = 1250, availableBerths = 14),
                    ClassOption(TrainClass.THREE_A, fare = 890, availableBerths = 36),
                    ClassOption(TrainClass.SL, fare = 340, availableBerths = 62)
                )
            ),
            Train(
                number = "17${(100..999).random()}",
                name = "${fromStn.city} - ${toStn.city} Intercity Mail",
                fromStation = fromStn,
                toStation = toStn,
                departureTime = "15:45",
                arrivalTime = "23:30",
                duration = "7h 45m",
                runsOn = "Daily",
                classes = listOf(
                    ClassOption(TrainClass.CC, fare = 520, availableBerths = 48),
                    ClassOption(TrainClass.THREE_A, fare = 890, availableBerths = 22),
                    ClassOption(TrainClass.SL, fare = 340, availableBerths = 88)
                )
            ),
            Train(
                number = "22${(100..999).random()}",
                name = "${fromStn.city} - ${toStn.city} Night Express",
                fromStation = fromStn,
                toStation = toStn,
                departureTime = "21:15",
                arrivalTime = "06:00",
                duration = "8h 45m",
                runsOn = "Daily",
                classes = listOf(
                    ClassOption(TrainClass.ONE_A, fare = 2150, availableBerths = 6),
                    ClassOption(TrainClass.TWO_A, fare = 1350, availableBerths = 18),
                    ClassOption(TrainClass.THREE_A, fare = 940, availableBerths = 44),
                    ClassOption(TrainClass.SL, fare = 360, availableBerths = 75)
                )
            )
        )

        return if (availableOnly) {
            generated.filter { t -> t.classes.any { it.availableBerths > 0 } }
        } else generated
    }
}
