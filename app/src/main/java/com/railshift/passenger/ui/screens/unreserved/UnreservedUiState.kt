package com.railshift.passenger.ui.screens.unreserved

import com.railshift.passenger.data.models.RecentUnreservedTicket
import com.railshift.passenger.data.models.Station
import com.railshift.passenger.data.models.Trip

data class UnreservedUiState(
    val fromStation: Station = Station("BZA", "Vijayawada Jn", "Vijayawada"),
    val toStation: Station = Station("GNT", "Guntur Jn", "Guntur"),
    val adults: Int = 2,
    val children: Int = 1,
    val adultFareEach: Int = 30,
    val childFareEach: Int = 15,
    val recentTickets: List<RecentUnreservedTicket> = emptyList(),
    val isBooking: Boolean = false,
    val bookedTrip: Trip? = null
) {
    val totalAdultFare: Int get() = adults * adultFareEach
    val totalChildFare: Int get() = children * childFareEach
    val totalFare: Int get() = totalAdultFare + totalChildFare
}
