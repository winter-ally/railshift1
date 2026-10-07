package com.railshift.passenger.navigation

object RailshiftRoutes {
    const val HOME = "home"
    const val MY_BOOKINGS = "my_bookings"
    const val HELP = "help"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val PLATFORM = "platform"

    const val RESERVED = "reserved"
    const val RESERVED_RESULTS = "reserved_results"
    const val UNRESERVED = "unreserved"
    const val UPGRADE = "upgrade"
    const val SCAN = "scan"

    const val SEARCH_TRAINS = "search_trains"
    const val PNR_STATUS = "pnr_status"
    const val COACH_POSITION = "coach_position"
    const val TRACK_TRAIN = "track_train"

    fun shouldShowBottomBar(route: String?): Boolean {
        return when (route) {
            HOME, MY_BOOKINGS, HELP, PROFILE, EDIT_PROFILE, PLATFORM -> true
            else -> false
        }
    }
}
