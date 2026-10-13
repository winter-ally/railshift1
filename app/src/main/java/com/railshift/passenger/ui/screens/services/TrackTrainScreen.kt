package com.railshift.passenger.ui.screens.services

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.data.models.LiveStation
import com.railshift.passenger.data.models.LiveTrainErrorType
import com.railshift.passenger.data.models.LiveTrainStatus
import com.railshift.passenger.ui.components.SubScreenTopBar
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun TrackTrainScreen(
    onBackClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    viewModel: TrackTrainViewModel = remember { TrackTrainViewModel() },
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    TrackTrainContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onOpenLanguagePicker = onOpenLanguagePicker,
        onTrainNumberChange = { viewModel.onTrainNumberChange(it) },
        onTrackClick = { viewModel.trackTrain() },
        onSelectQuickTrain = { viewModel.selectQuickTrain(it) },
        onLoadDemoClick = { viewModel.loadDemoStatus() },
        modifier = modifier
    )
}

@Composable
fun TrackTrainContent(
    uiState: TrackTrainUiState,
    onBackClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    onTrainNumberChange: (String) -> Unit,
    onTrackClick: () -> Unit,
    onSelectQuickTrain: (String) -> Unit,
    onLoadDemoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        SubScreenTopBar(
            title = stringResource(R.string.svc_track_train),
            subtitle = "Live status & GPS tracking",
            onBackClick = onBackClick,
            onLanguageClick = onOpenLanguagePicker
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp)
                .padding(bottom = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Train Number Input Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(colors.border)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Track Train by Number or Name",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.trainInput,
                        onValueChange = onTrainNumberChange,
                        label = { Text("Train Number or Name") },
                        placeholder = { Text("e.g. 12711, Pinakini, Godavari") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Train, contentDescription = null, tint = colors.accent)
                        },
                        trailingIcon = {
                            if (uiState.trainInput.isNotEmpty()) {
                                IconButton(onClick = { onTrainNumberChange("") }) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Clear", tint = colors.textSecondary)
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.accent,
                            focusedLabelColor = colors.accent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("track_train_input")
                    )

                    // Autocomplete suggestions if user types a query
                    if (uiState.matchingSuggestions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surface2),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                uiState.matchingSuggestions.forEach { train ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSelectQuickTrain(train.number) }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "${train.number} • ${train.name}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = colors.text
                                            )
                                            Text(
                                                text = "${train.fromStation.code} → ${train.toStation.code} (${train.departureTime})",
                                                fontSize = 11.sp,
                                                color = colors.textSecondary
                                            )
                                        }
                                        Icon(
                                            Icons.AutoMirrored.Outlined.ArrowForward,
                                            contentDescription = null,
                                            tint = colors.accent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick suggestion chips
                    Text(
                        text = "Popular trains:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        uiState.quickSuggestions.take(4).forEach { (number, name) ->
                            SuggestionChip(
                                onClick = { onSelectQuickTrain(number) },
                                label = { Text("$number $name", fontSize = 11.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (uiState.trainNumber == number) colors.accentContainer else colors.surface2,
                                    labelColor = if (uiState.trainNumber == number) colors.accent else colors.text
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    enabled = true,
                                    borderColor = if (uiState.trainNumber == number) colors.accent else colors.border
                                ),
                                modifier = Modifier.testTag("chip_train_$number")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onTrackClick,
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_track_train_status")
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = colors.onAccent,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Contacting Backend...", color = colors.onAccent, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Outlined.GpsFixed, contentDescription = null, tint = colors.onAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Track Live Status", fontWeight = FontWeight.Bold, color = colors.onAccent)
                        }
                    }
                }
            }

            // Render Cold Start Notice (Animated when backend is waking up)
            AnimatedVisibility(visible = uiState.isLoading && uiState.isColdStarting) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.accentContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .testTag("cold_start_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = colors.accent,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Waking up Render backend...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = colors.accent
                            )
                            Text(
                                text = "Free cloud instances spin down after inactivity. The first request may take up to 60-75s.",
                                fontSize = 11.sp,
                                color = colors.text,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Error Display Card
            if (uiState.errorMessage != null && !uiState.isLoading) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(colors.danger.copy(alpha = 0.5f))),
                    modifier = Modifier.fillMaxWidth().testTag("track_error_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(colors.danger.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = colors.danger, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (uiState.errorType == LiveTrainErrorType.INVALID_APP_KEY) "API Key Notice" else "Tracking Status",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = colors.danger
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage.orEmpty(),
                            fontSize = 13.sp,
                            color = colors.text,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = onTrackClick,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("btn_retry_track")
                            ) {
                                Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retry")
                            }

                            Button(
                                onClick = onLoadDemoClick,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                                modifier = Modifier.weight(1f).testTag("btn_view_demo_data")
                            ) {
                                Text("View Demo Data")
                            }
                        }
                    }
                }
            }

            // Live Train Tracking Success Card
            if (uiState.liveStatus != null && !uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                LiveTrainDetailCard(
                    status = uiState.liveStatus,
                    onRefresh = onTrackClick,
                    modifier = Modifier.testTag("live_train_detail_card")
                )
            }
        }
    }
}

@Composable
fun LiveTrainDetailCard(
    status: LiveTrainStatus,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(colors.border)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Train number, name & Refresh icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${status.trainNumber} • ${status.trainName}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                    Text(
                        text = "Running Date: ${status.runningDate}",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }

                IconButton(onClick = onRefresh, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh", tint = colors.accent)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row: Status pill + Cached indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val badgeBg = if (status.isRunningOnTime) colors.successContainer else colors.accentContainer
                val badgeColor = if (status.isRunningOnTime) colors.success else colors.accent

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (status.delayMinutes == 0) "On Time" else "Delayed ${status.delayMinutes}m",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }

                if (status.isCached) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surface2)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Cached data",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Updated: ${status.lastUpdated ?: "Live"}",
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(thickness = 1.dp, color = colors.border)
            Spacer(modifier = Modifier.height(14.dp))

            // Current Station vs Next Station Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surface2)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Current / Departed Station
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LAST DEPARTED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary
                    )
                    Text(
                        text = status.currentStation?.stationName ?: "Source Station",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                    Text(
                        text = status.currentStation?.stationCode ?: "",
                        fontSize = 12.sp,
                        color = colors.accent
                    )
                    if (status.currentStation?.actualDepartureTime != null) {
                        Text(
                            text = "Dep: ${status.currentStation.actualDepartureTime}",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                // Middle arrow
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.padding(horizontal = 8.dp).size(20.dp)
                )

                // Next Station
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "NEXT STOP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary
                    )
                    Text(
                        text = status.nextStation?.stationName ?: "Destination",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                    Text(
                        text = status.nextStation?.stationCode ?: "",
                        fontSize = 12.sp,
                        color = colors.accent
                    )
                    if (status.nextStation?.actualArrivalTime != null) {
                        Text(
                            text = "ETA: ${status.nextStation.actualArrivalTime}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.text
                        )
                    }
                }
            }

            // Summary description
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = status.statusSummary,
                fontSize = 13.sp,
                color = colors.text,
                lineHeight = 18.sp
            )

            // Stations Timeline Preview if available
            if (status.stations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Key Stations & Stops",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
                Spacer(modifier = Modifier.height(8.dp))

                status.stations.take(6).forEach { station ->
                    StationTimelineItem(station = station)
                }
            }
        }
    }
}

@Composable
fun StationTimelineItem(station: LiveStation) {
    val colors = RailshiftTheme.colors
    val isPast = station.hasDeparted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (isPast) colors.success else colors.accent)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "${station.stationCode} - ${station.stationName}",
            fontSize = 12.sp,
            fontWeight = if (isPast) FontWeight.Normal else FontWeight.SemiBold,
            color = if (isPast) colors.textSecondary else colors.text,
            modifier = Modifier.weight(1f)
        )
        val time = station.actualArrivalTime ?: station.arrivalTime ?: station.departureTime.orEmpty()
        Text(
            text = time,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isPast) colors.textSecondary else colors.accent
        )
    }
}
