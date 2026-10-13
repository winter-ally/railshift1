package com.railshift.passenger

import com.railshift.passenger.data.models.LiveTrainErrorType
import com.railshift.passenger.data.network.LiveTrainApiClient
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LiveTrainApiTest {

    @Test
    fun testParseFreshLiveTrainResponse() {
        val json = """
            {
                "cached": false,
                "data": {
                    "train_number": "12711",
                    "train_name": "Pinakini SF Express",
                    "date": "2026-10-13",
                    "status": "Running on time",
                    "delay_minutes": 0,
                    "updated_at": "10:15 AM",
                    "current_station": {
                        "code": "OGL",
                        "name": "Ongole",
                        "actual_departure": "08:25"
                    },
                    "next_station": {
                        "code": "SKM",
                        "name": "Singarayakonda",
                        "eta": "08:50",
                        "platform": "2"
                    },
                    "stations": [
                        {
                            "station_code": "BZA",
                            "station_name": "Vijayawada Jn",
                            "scheduled_departure": "06:25",
                            "actual_departure": "06:25",
                            "has_departed": true
                        },
                        {
                            "station_code": "OGL",
                            "station_name": "Ongole",
                            "scheduled_arrival": "08:24",
                            "actual_arrival": "08:24",
                            "has_departed": true
                        },
                        {
                            "station_code": "SKM",
                            "station_name": "Singarayakonda",
                            "scheduled_arrival": "08:49",
                            "actual_arrival": "08:50",
                            "has_departed": false
                        }
                    ]
                }
            }
        """.trimIndent()

        val parsed = LiveTrainApiClient.parseLiveTrainResponse(json, "12711")

        assertEquals("12711", parsed.trainNumber)
        assertEquals("Pinakini SF Express", parsed.trainName)
        assertEquals("Running on time", parsed.statusSummary)
        assertEquals(0, parsed.delayMinutes)
        assertTrue(parsed.isRunningOnTime)
        assertFalse(parsed.isCached)
        assertEquals("10:15 AM", parsed.lastUpdated)

        assertNotNull(parsed.currentStation)
        assertEquals("OGL", parsed.currentStation?.stationCode)
        assertEquals("Ongole", parsed.currentStation?.stationName)

        assertNotNull(parsed.nextStation)
        assertEquals("SKM", parsed.nextStation?.stationCode)
        assertEquals("Singarayakonda", parsed.nextStation?.stationName)
        assertEquals("08:50", parsed.nextStation?.actualArrivalTime)

        assertEquals(3, parsed.stations.size)
        assertEquals("BZA", parsed.stations[0].stationCode)
    }

    @Test
    fun testParseCachedDelayedLiveTrainResponse() {
        val json = """
            {
                "cached": true,
                "data": {
                    "number": "12727",
                    "name": "Godavari Express",
                    "status_as_of": "Delayed by 25 mins",
                    "delay": 25,
                    "last_updated": "5 mins ago",
                    "stations": []
                }
            }
        """.trimIndent()

        val parsed = LiveTrainApiClient.parseLiveTrainResponse(json, "12727")

        assertEquals("12727", parsed.trainNumber)
        assertEquals("Godavari Express", parsed.trainName)
        assertEquals("Delayed by 25 mins", parsed.statusSummary)
        assertEquals(25, parsed.delayMinutes)
        assertFalse(parsed.isRunningOnTime)
        assertTrue(parsed.isCached)
    }

    @Test
    fun testExtractDetailMessage() {
        val appKeyErrorJson = """{"detail":"Invalid or missing X-App-Key"}"""
        val notFoundJson = """{"detail":"Train 99999 not found"}"""
        val plainText = "Server connection refused"

        assertEquals("Invalid or missing X-App-Key", LiveTrainApiClient.extractDetailMessage(appKeyErrorJson))
        assertEquals("Train 99999 not found", LiveTrainApiClient.extractDetailMessage(notFoundJson))
        assertEquals(plainText, LiveTrainApiClient.extractDetailMessage(plainText))
    }

    @Test
    fun testDemoStatusGenerator() {
        val demo12711 = LiveTrainApiClient.getDemoLiveStatus("12711")
        assertEquals("12711", demo12711.trainNumber)
        assertEquals("Pinakini Express", demo12711.trainName)
        assertTrue(demo12711.stations.isNotEmpty())
        assertNotNull(demo12711.currentStation)
        assertNotNull(demo12711.nextStation)

        val demoGeneric = LiveTrainApiClient.getDemoLiveStatus("12345")
        assertEquals("12345", demoGeneric.trainNumber)
    }

    @Test
    fun testErrorTypeEnumValues() {
        val types = LiveTrainErrorType.entries
        assertTrue(types.contains(LiveTrainErrorType.INVALID_APP_KEY))
        assertTrue(types.contains(LiveTrainErrorType.TRAIN_NOT_FOUND))
        assertTrue(types.contains(LiveTrainErrorType.INVALID_TRAIN_NUMBER))
        assertTrue(types.contains(LiveTrainErrorType.SERVICE_UNAVAILABLE))
        assertTrue(types.contains(LiveTrainErrorType.TIMEOUT))
        assertTrue(types.contains(LiveTrainErrorType.NETWORK_ERROR))
    }
}
