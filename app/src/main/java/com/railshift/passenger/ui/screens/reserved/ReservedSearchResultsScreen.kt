package com.railshift.passenger.ui.screens.reserved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.railshift.passenger.data.models.Train
import com.railshift.passenger.data.models.TrainClass
import com.railshift.passenger.ui.components.SubScreenTopBar
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun ReservedSearchResultsScreen(
    viewModel: ReservedViewModel,
    onBackClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    onBookingSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = RailshiftTheme.colors
    var selectedTrainNumber by remember { mutableStateOf<String?>(null) }
    var selectedClass by remember { mutableStateOf(uiState.selectedClass) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        SubScreenTopBar(
            title = "${uiState.fromStation.code} → ${uiState.toStation.code}",
            subtitle = "${uiState.journeyDateText} • ${uiState.searchResults.size} trains",
            onBackClick = onBackClick,
            onLanguageClick = onOpenLanguagePicker
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.searchResults) { train ->
                TrainSearchResultCard(
                    train = train,
                    selectedClass = if (selectedTrainNumber == train.number) selectedClass else null,
                    onSelectClass = { chosenClass ->
                        selectedTrainNumber = train.number
                        selectedClass = chosenClass
                    },
                    onBookClick = {
                        viewModel.bookTrain(train.number, selectedClass)
                        showSuccessDialog = true
                    }
                )
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onBookingSuccess()
            },
            title = {
                Text(
                    text = "Booking Confirmed",
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
            },
            text = {
                Text(
                    text = "Your booking for train $selectedTrainNumber (${selectedClass.displayName}) has been confirmed successfully! View in My bookings.",
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onBookingSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("Go to Bookings", color = colors.onAccent)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                    }
                ) {
                    Text("OK", color = colors.accent)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun TrainSearchResultCard(
    train: Train,
    selectedClass: TrainClass?,
    onSelectClass: (TrainClass) -> Unit,
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
            // Train Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${train.number} • ${train.name}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                    Text(
                        text = "Runs: ${train.runsOn}",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.accentContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = train.duration,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accent
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = train.departureTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                    Text(
                        text = train.fromStation.code,
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }
                Text(
                    text = "─────────",
                    fontSize = 12.sp,
                    color = colors.borderStrong
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = train.arrivalTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                    Text(
                        text = train.toStation.code,
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Class Fares Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(train.classes) { opt ->
                    val isSelected = selectedClass == opt.trainClass
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .then(
                                if (isSelected) Modifier
                                    .border(2.dp, colors.accent, RoundedCornerShape(10.dp))
                                    .background(colors.accentContainer)
                                else Modifier
                                    .border(1.dp, colors.borderStrong, RoundedCornerShape(10.dp))
                                    .background(colors.surface2)
                            )
                            .clickable { onSelectClass(opt.trainClass) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = opt.trainClass.code,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) colors.accent else colors.text
                                )
                                Text(
                                    text = "₹${opt.fare}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.text
                                )
                            }
                            Text(
                                text = if (opt.availableBerths > 0) "AVL ${opt.availableBerths}" else "WL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (opt.availableBerths > 0) colors.success else colors.danger
                            )
                        }
                    }
                }
            }

            if (selectedClass != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onBookClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    Text(
                        text = "Book ${selectedClass.code}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onAccent
                    )
                }
            }
        }
    }
}
