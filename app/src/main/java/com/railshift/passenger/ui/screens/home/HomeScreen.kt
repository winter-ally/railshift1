package com.railshift.passenger.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.data.models.Trip
import com.railshift.passenger.data.models.TripStatus
import com.railshift.passenger.data.models.UserProfile
import com.railshift.passenger.ui.components.SectionHeader
import com.railshift.passenger.ui.components.TabTopBar
import com.railshift.passenger.ui.components.TripCard
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToReserved: () -> Unit,
    onNavigateToUnreserved: () -> Unit,
    onNavigateToPlatform: () -> Unit,
    onNavigateToUpgrade: () -> Unit,
    onNavigateToSearchTrains: () -> Unit,
    onNavigateToPnrStatus: () -> Unit,
    onNavigateToCoachPosition: () -> Unit,
    onNavigateToTrackTrain: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeContent(
        uiState = uiState,
        onNavigateToReserved = onNavigateToReserved,
        onNavigateToUnreserved = onNavigateToUnreserved,
        onNavigateToPlatform = onNavigateToPlatform,
        onNavigateToUpgrade = onNavigateToUpgrade,
        onNavigateToSearchTrains = onNavigateToSearchTrains,
        onNavigateToPnrStatus = onNavigateToPnrStatus,
        onNavigateToCoachPosition = onNavigateToCoachPosition,
        onNavigateToTrackTrain = onNavigateToTrackTrain,
        onNavigateToBookings = onNavigateToBookings,
        onOpenLanguagePicker = onOpenLanguagePicker,
        modifier = modifier
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    onNavigateToReserved: () -> Unit,
    onNavigateToUnreserved: () -> Unit,
    onNavigateToPlatform: () -> Unit,
    onNavigateToUpgrade: () -> Unit,
    onNavigateToSearchTrains: () -> Unit,
    onNavigateToPnrStatus: () -> Unit,
    onNavigateToCoachPosition: () -> Unit,
    onNavigateToTrackTrain: () -> Unit,
    onNavigateToBookings: () -> Unit,
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
            // Greeting
            val userName = uiState.userProfile?.name?.split(" ")?.firstOrNull() ?: "Ravi"
            Text(
                text = "Good morning, $userName",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
            Text(
                text = stringResource(R.string.greeting_question),
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                letterSpacing = (-0.2).sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 14.dp)
            )

            // 2x2 Main Booking Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Reserved Tile (Highlighted primary)
                BookingTile(
                    title = stringResource(R.string.tile_reserved),
                    subtitle = stringResource(R.string.tile_reserved_sub),
                    icon = Icons.Outlined.AirlineSeatReclineExtra,
                    isPrimary = true,
                    onClick = onNavigateToReserved,
                    modifier = Modifier.weight(1f).testTag("tile_reserved")
                )

                // Unreserved Tile
                BookingTile(
                    title = stringResource(R.string.tile_unreserved),
                    subtitle = stringResource(R.string.tile_unreserved_sub),
                    icon = Icons.Outlined.ConfirmationNumber,
                    isPrimary = false,
                    onClick = onNavigateToUnreserved,
                    modifier = Modifier.weight(1f).testTag("tile_unreserved")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Platform Tile
                BookingTile(
                    title = stringResource(R.string.tile_platform),
                    subtitle = stringResource(R.string.tile_platform_sub),
                    icon = Icons.Outlined.MeetingRoom,
                    isPrimary = false,
                    onClick = onNavigateToPlatform,
                    modifier = Modifier.weight(1f).testTag("tile_platform")
                )

                // Upgrade Ticket Tile
                BookingTile(
                    title = stringResource(R.string.tile_upgrade),
                    subtitle = stringResource(R.string.tile_upgrade_sub),
                    icon = Icons.Outlined.Upgrade,
                    isPrimary = false,
                    onClick = onNavigateToUpgrade,
                    modifier = Modifier.weight(1f).testTag("tile_upgrade")
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // More Services section
            Text(
                text = stringResource(R.string.section_more_services),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MoreServiceTile(
                    label = stringResource(R.string.svc_search_trains),
                    icon = Icons.Outlined.Search,
                    onClick = onNavigateToSearchTrains,
                    modifier = Modifier.weight(1f).testTag("svc_search_trains")
                )
                MoreServiceTile(
                    label = stringResource(R.string.svc_pnr_status),
                    icon = Icons.Outlined.FactCheck,
                    onClick = onNavigateToPnrStatus,
                    modifier = Modifier.weight(1f).testTag("svc_pnr_status")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MoreServiceTile(
                    label = stringResource(R.string.svc_coach_position),
                    icon = Icons.Outlined.ViewColumn,
                    onClick = onNavigateToCoachPosition,
                    modifier = Modifier.weight(1f).testTag("svc_coach_position")
                )
                MoreServiceTile(
                    label = stringResource(R.string.svc_track_train),
                    icon = Icons.Outlined.PinDrop,
                    onClick = onNavigateToTrackTrain,
                    modifier = Modifier.weight(1f).testTag("svc_track_train")
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Upcoming Trip section
            SectionHeader(
                title = stringResource(R.string.section_upcoming_trip),
                actionText = stringResource(R.string.action_view_all),
                onActionClick = onNavigateToBookings
            )

            Spacer(modifier = Modifier.height(8.dp))

            val tripToShow = uiState.upcomingTrip ?: Trip(
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

            TripCard(trip = tripToShow)
        }
    }
}

@Composable
private fun BookingTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    val containerBg = if (isPrimary) colors.accentContainer else colors.surface2
    val iconColor = if (isPrimary) colors.accent else colors.text
    val titleColor = if (isPrimary) colors.accent else colors.text

    Column(
        modifier = modifier
            .heightIn(min = 96.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(containerBg)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun MoreServiceTile(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .background(colors.surface)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.text
        )
    }
}

@Preview(name = "Home Screen Light")
@Composable
private fun HomeScreenLightPreview() {
    RailshiftTheme(darkTheme = false) {
        HomeContent(
            uiState = HomeUiState(
                userProfile = UserProfile("Ravi Kumar", "ravi.kumar@example.com", "RK", 1240.0),
                upcomingTrip = Trip(
                    id = "1",
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
            ),
            onNavigateToReserved = {},
            onNavigateToUnreserved = {},
            onNavigateToPlatform = {},
            onNavigateToUpgrade = {},
            onNavigateToSearchTrains = {},
            onNavigateToPnrStatus = {},
            onNavigateToCoachPosition = {},
            onNavigateToTrackTrain = {},
            onNavigateToBookings = {},
            onOpenLanguagePicker = {}
        )
    }
}

@Preview(name = "Home Screen Dark")
@Composable
private fun HomeScreenDarkPreview() {
    RailshiftTheme(darkTheme = true) {
        HomeContent(
            uiState = HomeUiState(
                userProfile = UserProfile("Ravi Kumar", "ravi.kumar@example.com", "RK", 1240.0),
                upcomingTrip = Trip(
                    id = "1",
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
            ),
            onNavigateToReserved = {},
            onNavigateToUnreserved = {},
            onNavigateToPlatform = {},
            onNavigateToUpgrade = {},
            onNavigateToSearchTrains = {},
            onNavigateToPnrStatus = {},
            onNavigateToCoachPosition = {},
            onNavigateToTrackTrain = {},
            onNavigateToBookings = {},
            onOpenLanguagePicker = {}
        )
    }
}
