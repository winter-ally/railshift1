package com.railshift.passenger.data.repository

import com.railshift.passenger.data.models.*
import kotlinx.coroutines.flow.Flow

interface RailshiftRepository {
    fun getStations(): List<Station>
    fun getStationByCode(code: String): Station?
    fun getUpcomingTrips(): Flow<List<Trip>>
    fun getPastTrips(): Flow<List<Trip>>
    fun getRecentSearches(): List<RecentSearch>
    fun getRecentUnreservedTickets(): List<RecentUnreservedTicket>
    fun getRecentPlatformStations(): List<Station>
    fun searchTrains(
        fromCode: String,
        toCode: String,
        date: String,
        trainClass: TrainClass?,
        availableOnly: Boolean
    ): List<Train>
    suspend fun bookUnreservedTicket(
        fromCode: String,
        toCode: String,
        adults: Int,
        children: Int
    ): Result<Trip>
    fun getScannedPaperTicket(ticketNumber: String): PaperTicket?
    suspend fun upgradeTicket(
        ticketNumber: String,
        trainNumber: String,
        targetClass: TrainClass
    ): Result<Trip>
    suspend fun bookPlatformTicket(stationCode: String, date: String): Result<String>
    fun getUserProfile(): Flow<UserProfile>
    suspend fun updateProfile(name: String, email: String)
    suspend fun addWalletMoney(amount: Double)
    fun checkPNR(pnr: String): Trip?
    fun getCoachLayout(trainNumber: String): String
    fun trackTrain(trainNumber: String): String
}
