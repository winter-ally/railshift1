package com.railshift.passenger.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.data.models.Train
import com.railshift.passenger.data.models.TrainDirectory
import com.railshift.passenger.ui.components.SubScreenTopBar
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun SearchTrainsScreen(
    onBackClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    onNavigateToTrack: (String) -> Unit = {},
    onNavigateToBook: (Train) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filterChips = listOf("All", "Superfast", "Express", "Vande Bharat", "Rajdhani", "Daily")

    val allTrains = remember { TrainDirectory.TRAINS }
    val filteredTrains = remember(searchQuery, selectedFilter) {
        val baseList = if (searchQuery.isBlank()) {
            allTrains
        } else {
            TrainDirectory.searchTrainsByQuery(searchQuery)
        }

        when (selectedFilter) {
            "Superfast" -> baseList.filter { it.name.contains("SF", ignoreCase = true) || it.name.contains("Superfast", ignoreCase = true) }
            "Vande Bharat" -> baseList.filter { it.name.contains("Vande Bharat", ignoreCase = true) }
            "Rajdhani" -> baseList.filter { it.name.contains("Rajdhani", ignoreCase = true) }
            "Daily" -> baseList.filter { it.runsOn.equals("Daily", ignoreCase = true) }
            else -> baseList
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        SubScreenTopBar(
            title = stringResource(R.string.svc_search_trains),
            subtitle = "Search schedules, routes & live tracking",
            onBackClick = onBackClick,
            onLanguageClick = onOpenLanguagePicker
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Main Search Input with live typing
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Train Number, Name or Station") },
                placeholder = { Text("e.g. 12711, Pinakini, Godavari, BZA, Chennai") },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = colors.accent)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
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
                    .testTag("search_trains_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filterChips) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.accentContainer,
                            selectedLabelColor = colors.accent
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) colors.accent else colors.border
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Results Count Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredTrains.size} trains found",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary
                )
                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = "Clear filter",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accent,
                        modifier = Modifier.clickable {
                            searchQuery = ""
                            selectedFilter = "All"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Trains Results List
            if (filteredTrains.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(colors.border))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Outlined.Train,
                                contentDescription = null,
                                tint = colors.accent,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No trains matching \"$searchQuery\"",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = colors.text
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try searching by train number (e.g. 12711, 12727) or station code (BZA, MAS, SC).",
                                fontSize = 12.sp,
                                color = colors.textSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedFilter = "All"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                            ) {
                                Text("View All Trains", color = colors.onAccent)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTrains) { train ->
                        TrainDirectoryCard(
                            train = train,
                            onTrackLiveClick = { onNavigateToTrack(train.number) },
                            onBookClick = { onNavigateToBook(train) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainDirectoryCard(
    train: Train,
    onTrackLiveClick: () -> Unit,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("train_card_${train.number}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(colors.border))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Train Number & Name + Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${train.number} • ${train.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = colors.text
                    )
                    Text(
                        text = "Runs: ${train.runsOn}",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.accentContainer
                ) {
                    Text(
                        text = train.duration,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = colors.accent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Route & Timings Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = train.departureTime,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.text
                    )
                    Text(
                        text = "${train.fromStation.name} (${train.fromStation.code})",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }
                Icon(
                    Icons.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(18.dp)
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = train.arrivalTime,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.text
                    )
                    Text(
                        text = "${train.toStation.name} (${train.toStation.code})",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Class Fares Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(train.classes) { opt ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colors.surface2,
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = opt.trainClass.code,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = colors.text
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "₹${opt.fare}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = colors.accent
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Track Live Status Button
                Button(
                    onClick = onTrackLiveClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(38.dp)
                        .testTag("btn_track_train_${train.number}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.GpsFixed,
                        contentDescription = null,
                        tint = colors.onAccent,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Track Live",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = colors.onAccent
                    )
                }

                // Book Seats Button
                OutlinedButton(
                    onClick = onBookClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("btn_book_train_${train.number}")
                ) {
                    Text(
                        text = "Book Seats",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = colors.text
                    )
                }
            }
        }
    }
}
