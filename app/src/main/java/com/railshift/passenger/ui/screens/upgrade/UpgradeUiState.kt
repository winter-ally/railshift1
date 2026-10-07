package com.railshift.passenger.ui.screens.upgrade

import com.railshift.passenger.data.models.PaperTicket
import com.railshift.passenger.data.models.TrainClass
import com.railshift.passenger.data.models.Trip
import com.railshift.passenger.data.models.UpgradeClassOption

data class UpgradeUiState(
    val isTicketScanned: Boolean = true, // Default to true to show full UI matching design spec
    val paperTicket: PaperTicket = PaperTicket(
        ticketNumber = "7 3 2 0 5 1 8 4",
        fromCode = "BZA",
        fromCity = "Vijayawada",
        toCode = "MAS",
        toCity = "Chennai",
        passengersText = "2 adults",
        passengersCount = 2,
        farePaid = 190,
        isValid = true
    ),
    val selectedTrainNumber: String = "12711",
    val selectedTrainName: String = "Pinakini Express",
    val selectedTrainDeparture: String = "Today • departs 06:25",
    val upgradeOptions: List<UpgradeClassOption> = listOf(
        UpgradeClassOption(TrainClass.SL, availableBerths = 38, fareDifferencePerPerson = 280),
        UpgradeClassOption(TrainClass.THREE_A, availableBerths = 22, fareDifferencePerPerson = 640),
        UpgradeClassOption(TrainClass.TWO_A, availableBerths = 9, fareDifferencePerPerson = 960)
    ),
    val selectedClass: TrainClass = TrainClass.THREE_A,
    val isUpgrading: Boolean = false,
    val upgradedTrip: Trip? = null
) {
    val selectedOption: UpgradeClassOption
        get() = upgradeOptions.find { it.trainClass == selectedClass } ?: upgradeOptions[1]

    val totalFareDifference: Int
        get() = selectedOption.fareDifferencePerPerson * paperTicket.passengersCount
}
