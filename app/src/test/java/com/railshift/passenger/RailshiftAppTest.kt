package com.railshift.passenger

import com.railshift.passenger.data.models.ThemeMode
import com.railshift.passenger.data.models.TrainClass
import com.railshift.passenger.data.repository.FakeRailshiftRepository
import com.railshift.passenger.ui.screens.home.HomeViewModel
import com.railshift.passenger.ui.screens.reserved.ReservedViewModel
import com.railshift.passenger.ui.screens.unreserved.UnreservedViewModel
import com.railshift.passenger.ui.screens.upgrade.UpgradeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RailshiftAppTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeRailshiftRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeRailshiftRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testRepositoryStations() {
        val stations = repository.getStations()
        assertTrue(stations.isNotEmpty())
        assertEquals("BZA", stations[0].code)
        assertEquals("Vijayawada Jn", stations[0].name)
        assertNotNull(repository.getStationByCode("MAS"))
    }

    @Test
    fun testUnreservedFareCalculation() {
        val viewModel = UnreservedViewModel(repository)
        assertEquals(2, viewModel.uiState.value.adults)
        assertEquals(1, viewModel.uiState.value.children)
        // 2 * 30 + 1 * 15 = 75
        assertEquals(75, viewModel.uiState.value.totalFare)

        viewModel.incrementAdults()
        assertEquals(3, viewModel.uiState.value.adults)
        // 3 * 30 + 1 * 15 = 105
        assertEquals(105, viewModel.uiState.value.totalFare)

        viewModel.decrementChildren()
        assertEquals(0, viewModel.uiState.value.children)
        assertEquals(90, viewModel.uiState.value.totalFare)
    }

    @Test
    fun testReservedStationSwap() {
        val viewModel = ReservedViewModel(repository)
        val initialFrom = viewModel.uiState.value.fromStation.code
        val initialTo = viewModel.uiState.value.toStation.code

        viewModel.swapStations()
        assertEquals(initialTo, viewModel.uiState.value.fromStation.code)
        assertEquals(initialFrom, viewModel.uiState.value.toStation.code)
    }

    @Test
    fun testUpgradeFareDifference() {
        val viewModel = UpgradeViewModel(repository)
        assertEquals(TrainClass.THREE_A, viewModel.uiState.value.selectedClass)
        // 640 * 2 = 1280
        assertEquals(1280, viewModel.uiState.value.totalFareDifference)

        viewModel.selectClass(TrainClass.SL)
        // 280 * 2 = 560
        assertEquals(560, viewModel.uiState.value.totalFareDifference)
    }

    @Test
    fun testBookingsAndUpgradeFlow() = runTest {
        var booked = false
        val unreservedVm = UnreservedViewModel(repository)
        unreservedVm.bookTicket {
            booked = true
        }
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(booked)
        assertNotNull(unreservedVm.uiState.value.bookedTrip)
    }

    @Test
    fun testThemeModes() {
        assertEquals(3, ThemeMode.entries.size)
        assertTrue(ThemeMode.entries.contains(ThemeMode.LIGHT))
        assertTrue(ThemeMode.entries.contains(ThemeMode.DARK))
        assertTrue(ThemeMode.entries.contains(ThemeMode.SYSTEM))
    }
}
