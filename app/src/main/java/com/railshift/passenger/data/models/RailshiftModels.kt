package com.railshift.passenger.data.models

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिंदी (Hindi)"),
    TELUGU("te", "తెలుగు (Telugu)")
}

data class Station(
    val code: String,
    val name: String,
    val city: String
)

enum class TrainClass(val code: String, val displayName: String) {
    SL("SL", "Sleeper (SL)"),
    THREE_A("3A", "AC 3 Tier (3A)"),
    TWO_A("2A", "AC 2 Tier (2A)"),
    ONE_A("1A", "AC First Class (1A)"),
    CC("CC", "AC Chair Car (CC)")
}

data class ClassOption(
    val trainClass: TrainClass,
    val fare: Int,
    val availableBerths: Int,
    val statusText: String = "Available • $availableBerths berths"
)

data class Train(
    val number: String,
    val name: String,
    val fromStation: Station,
    val toStation: Station,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val runsOn: String = "Daily",
    val classes: List<ClassOption>
)

enum class TripStatus {
    CONFIRMED,
    COMPLETED,
    WAITLISTED,
    CANCELLED
}

data class Trip(
    val id: String,
    val pnr: String,
    val dateText: String,
    val trainNumber: String,
    val trainName: String,
    val fromCode: String,
    val toCode: String,
    val departureTime: String,
    val arrivalTime: String,
    val status: TripStatus,
    val coach: String = "B3",
    val berth: String = "24 (LB)",
    val passengers: Int = 1,
    val totalFare: Int = 890
)

data class PaperTicket(
    val ticketNumber: String,
    val fromCode: String,
    val fromCity: String,
    val toCode: String,
    val toCity: String,
    val passengersText: String,
    val passengersCount: Int,
    val farePaid: Int,
    val isValid: Boolean = true
)

data class UpgradeClassOption(
    val trainClass: TrainClass,
    val availableBerths: Int,
    val fareDifferencePerPerson: Int
)

data class UserProfile(
    val name: String,
    val email: String,
    val initials: String,
    val walletBalance: Double
)

data class RecentSearch(
    val fromCode: String,
    val toCode: String,
    val dateText: String,
    val classCode: String
)

data class RecentUnreservedTicket(
    val fromCode: String,
    val toCode: String,
    val passengersText: String,
    val dateText: String
)
