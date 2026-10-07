package com.railshift.passenger.ui.screens.bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.data.models.Trip
import com.railshift.passenger.data.models.TripStatus
import com.railshift.passenger.ui.components.SectionHeader
import com.railshift.passenger.ui.components.TabTopBar
import com.railshift.passenger.ui.components.TripCard
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun MyBookingsScreen(
    viewModel: BookingsViewModel,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    MyBookingsContent(
        uiState = uiState,
        onSelectTab = { viewModel.selectTab(it) },
        onOpenLanguagePicker = onOpenLanguagePicker,
        modifier = modifier
    )
}

@Composable
fun MyBookingsContent(
    uiState: BookingsUiState,
    onSelectTab: (BookingsTab) -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        TabTopBar(onLanguageClick = onOpenLanguagePicker)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp)
                .padding(bottom = 16.dp)
        ) {
            // Screen title
            Text(
                text = stringResource(R.string.nav_bookings),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                letterSpacing = (-0.3).sp,
                modifier = Modifier.padding(top = 6.dp, start = 4.dp)
            )

            // Tabs (Active / Past)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp)
            ) {
                // Active tab
                val isActiveSelected = uiState.selectedTab == BookingsTab.ACTIVE
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectTab(BookingsTab.ACTIVE) }
                        .padding(vertical = 10.dp)
                        .testTag("tab_active_bookings"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.tab_active),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isActiveSelected) colors.accent else colors.textSecondary
                    )
                }

                // Past tab
                val isPastSelected = uiState.selectedTab == BookingsTab.PAST
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectTab(BookingsTab.PAST) }
                        .padding(vertical = 10.dp)
                        .testTag("tab_past_bookings"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.tab_past),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPastSelected) colors.accent else colors.textSecondary
                    )
                }
            }

            // Tab underline bar
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(if (uiState.selectedTab == BookingsTab.ACTIVE) colors.accent else colors.border)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(if (uiState.selectedTab == BookingsTab.PAST) colors.accent else colors.border)
                )
            }

            if (uiState.selectedTab == BookingsTab.ACTIVE) {
                // Upcoming trip section
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = stringResource(R.string.section_upcoming_trip),
                    actionText = stringResource(R.string.action_view_all),
                    onActionClick = {}
                )
                Spacer(modifier = Modifier.height(8.dp))

                val upcoming = uiState.activeTrips.firstOrNull() ?: Trip(
                    id = "def-upcoming",
                    pnr = "4218903512",
                    dateText = "Mon, 12 Oct",
                    trainNumber = "12711",
                    trainName = "Pinakini Express",
                    fromCode = "BZA",
                    toCode = "MAS",
                    departureTime = "06:25",
                    arrivalTime = "12:40",
                    status = TripStatus.CONFIRMED
                )
                TripCard(trip = upcoming, showTrainPrefix = true)

                // Past bookings (last 6 months) stack
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = stringResource(R.string.section_past_bookings),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.pastTrips.forEach { trip ->
                        TripCard(trip = trip, showTrainPrefix = true)
                    }
                }
            } else {
                // Past tab view
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.section_past_bookings),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.pastTrips.forEach { trip ->
                        TripCard(trip = trip, showTrainPrefix = true)
                    }
                }
            }
        }
    }
}

@Preview(name = "My Bookings Light")
@Composable
private fun MyBookingsLightPreview() {
    RailshiftTheme(darkTheme = false) {
        MyBookingsContent(
            uiState = BookingsUiState(
                activeTrips = listOf(
                    Trip("1", "4218903512", "Mon, 12 Oct", "12711", "Pinakini Express", "BZA", "MAS", "06:25", "12:40", TripStatus.CONFIRMED)
                ),
                pastTrips = listOf(
                    Trip("2", "6127891234", "Sun, 04 Oct", "17215", "Visakhapatnam Exp", "VSKP", "BZA", "14:00", "20:00", TripStatus.COMPLETED),
                    Trip("3", "8890124567", "Fri, 25 Sep", "12839", "Howrah Mail", "VSKP", "BZA", "09:30", "16:30", TripStatus.COMPLETED)
                )
            ),
            onSelectTab = {},
            onOpenLanguagePicker = {}
        )
    }
}

@Preview(name = "My Bookings Dark")
@Composable
private fun MyBookingsDarkPreview() {
    RailshiftTheme(darkTheme = true) {
        MyBookingsContent(
            uiState = BookingsUiState(
                activeTrips = listOf(
                    Trip("1", "4218903512", "Mon, 12 Oct", "12711", "Pinakini Express", "BZA", "MAS", "06:25", "12:40", TripStatus.CONFIRMED)
                ),
                pastTrips = listOf(
                    Trip("2", "6127891234", "Sun, 04 Oct", "17215", "Visakhapatnam Exp", "VSKP", "BZA", "14:00", "20:00", TripStatus.COMPLETED),
                    Trip("3", "8890124567", "Fri, 25 Sep", "12839", "Howrah Mail", "VSKP", "BZA", "09:30", "16:30", TripStatus.COMPLETED)
                )
            ),
            onSelectTab = {},
            onOpenLanguagePicker = {}
        )
    }
}
