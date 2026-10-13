package com.railshift.passenger.data.repository

import com.railshift.passenger.data.models.LiveTrainStatus
import com.railshift.passenger.data.remote.LiveTrainApi

/**
 * Real backend for live train status; everything else still comes from [offline]
 * until those features get their own backend endpoints.
 */
class LiveRailshiftRepository(
    private val offline: RailshiftRepository,
    private val api: LiveTrainApi
) : RailshiftRepository by offline {

    override suspend fun getLiveTrainStatus(trainNumber: String): Result<LiveTrainStatus> =
        api.fetch(trainNumber)
}
