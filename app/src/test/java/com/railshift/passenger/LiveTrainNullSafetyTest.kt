package com.railshift.passenger

import com.railshift.passenger.data.network.LiveTrainApiClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Android's JSONObject.optString returns the text "null" for JSON null; the parser must not show that.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LiveTrainNullSafetyTest {

    @Test
    fun `missing values are never shown as the text null`() {
        val status = LiveTrainApiClient.parseLiveTrainResponse(
            """{"trainNumber":"12713","trainName":null,"status":null,"delayMinutes":0,
               "currentStation":null,"nextStation":null,"source":"live",
               "route":[{"stationCode":"BZA","stationName":"Vijayawada Jn","scheduledArrival":null,
                         "scheduledDeparture":"2026-10-13T06:25:00+05:30","platform":null}]}""",
            "12713"
        )
        assertEquals("Express", status.trainName)
        val origin = status.stations.first()
        assertNull(origin.arrivalTime)
        assertNull(origin.platformNumber)
        assertEquals("06:25", origin.departureTime)
        assertFalse(status.currentStation?.stationCode == "null")
        assertFalse(status.nextStation?.stationCode == "null")
        assertFalse(status.statusSummary.contains("null"))
    }

    @Test
    fun `server error detail that is null falls back to the raw text`() {
        assertEquals("""{"detail":null}""", LiveTrainApiClient.extractDetailMessage("""{"detail":null}"""))
    }
}
