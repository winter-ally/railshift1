package com.railshift.passenger

import com.railshift.passenger.data.remote.LiveTrainApi
import com.railshift.passenger.data.repository.FakeRailshiftRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Robolectric supplies the real org.json implementation that LiveTrainApi.parse uses on Android.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LiveTrainApiTest {

    @Test
    fun `parses a full backend response`() {
        val status = LiveTrainApi.parse(
            """{"trainNumber":"12711","trainName":"Pinakini Express","journeyDate":"2026-10-13",
               "status":"RUNNING","delayMinutes":12,"currentStation":"BZA","nextStation":"OGL",
               "lat":16.51,"lon":80.62,"speedKmh":71.5,"segmentProgress":0.4,"platform":"1",
               "updatedAt":"2026-10-13T05:00:00+00:00","source":"cache"}"""
        )
        assertEquals("12711", status.trainNumber)
        assertEquals("Pinakini Express", status.trainName)
        assertEquals(12, status.delayMinutes)
        assertEquals("BZA", status.currentStation)
        assertEquals("OGL", status.nextStation)
        assertEquals(71.5, status.speedKmh!!, 0.0001)
        assertEquals("1", status.platform)
        assertEquals("cache", status.source)
    }

    @Test
    fun `null fields stay null`() {
        val status = LiveTrainApi.parse(
            """{"trainNumber":"12711","trainName":null,"status":null,"delayMinutes":0,
               "currentStation":null,"nextStation":null,"speedKmh":null,"platform":null,"source":"stale"}"""
        )
        assertNull(status.trainName)
        assertNull(status.currentStation)
        assertNull(status.speedKmh)
        assertNull(status.platform)
        assertEquals("stale", status.source)
    }

    @Test
    fun `error messages are friendly`() {
        listOf(400, 401, 404, 422, 502, 503).forEach {
            assertTrue(!LiveTrainApi.errorMessage(it).startsWith("Server error"))
        }
        assertEquals("Server error (500).", LiveTrainApi.errorMessage(500))
    }

    @Test
    fun `fake repository returns live status`() = runTest {
        val status = FakeRailshiftRepository().getLiveTrainStatus("12711").getOrThrow()
        assertEquals("12711", status.trainNumber)
        assertEquals(0, status.delayMinutes)
    }
}
