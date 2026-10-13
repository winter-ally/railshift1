package com.railshift.passenger.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.data.models.LiveTrainStatus
import com.railshift.passenger.data.repository.RailshiftRepository
import com.railshift.passenger.ui.components.SubScreenTopBar
import com.railshift.passenger.ui.theme.RailshiftTheme
import kotlinx.coroutines.launch

private sealed interface TrackState {
    data object Idle : TrackState
    data object Loading : TrackState
    data class Success(val status: LiveTrainStatus) : TrackState
    data class Error(val message: String) : TrackState
}

@Composable
fun TrackTrainScreen(
    repository: RailshiftRepository,
    onBackClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    val scope = rememberCoroutineScope()
    var trainNumber by remember { mutableStateOf("12711") }
    var state by remember { mutableStateOf<TrackState>(TrackState.Idle) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        SubScreenTopBar(
            title = stringResource(R.string.svc_track_train),
            onBackClick = onBackClick,
            onLanguageClick = onOpenLanguagePicker
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            OutlinedTextField(
                value = trainNumber,
                onValueChange = { trainNumber = it.filter { ch -> ch.isDigit() }.take(5) },
                label = { Text("Enter Train Number") },
                leadingIcon = {
                    Icon(Icons.Outlined.PinDrop, contentDescription = null, tint = colors.accent)
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

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    val number = trainNumber.trim()
                    if (number.length != 5) {
                        state = TrackState.Error("Enter a 5-digit train number.")
                    } else {
                        state = TrackState.Loading
                        scope.launch {
                            val result = repository.getLiveTrainStatus(number)
                            state = result.fold(
                                onSuccess = { TrackState.Success(it) },
                                onFailure = { TrackState.Error(it.message ?: "Could not load live status.") }
                            )
                        }
                    }
                },
                enabled = state !is TrackState.Loading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("btn_track_train_status")
            ) {
                Text("Track Live Status", fontWeight = FontWeight.Bold, color = colors.onAccent)
            }

            when (val current = state) {
                TrackState.Idle -> Unit
                TrackState.Loading -> {
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        modifier = Modifier.testTag("track_train_loading")
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = colors.accent
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Getting live status… the first request can take up to a minute.",
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                    }
                }
                is TrackState.Error -> {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = current.message,
                        fontSize = 13.sp,
                        color = colors.danger,
                        modifier = Modifier.testTag("track_train_error")
                    )
                }
                is TrackState.Success -> {
                    Spacer(modifier = Modifier.height(18.dp))
                    StatusCard(current.status)
                }
            }
        }
    }
}

@Composable
private fun StatusCard(status: LiveTrainStatus) {
    val colors = RailshiftTheme.colors
    val delayText = if (status.delayMinutes <= 0) "On time" else "Running ${status.delayMinutes} min late"
    val lines = buildList {
        add("Status: $delayText")
        status.currentStation?.let { add("Last station: $it") }
        status.nextStation?.let { add("Next halt: $it") }
        status.speedKmh?.let { add("Speed: ${it.toInt()} km/h") }
        status.platform?.let { add("Next platform: $it") }
    }
    val freshness = when (status.source) {
        "stale" -> "Live data is unavailable, so this is the last saved status."
        "cache" -> "Updated within the last few minutes."
        else -> "Just updated."
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("track_train_result_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface2)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = listOfNotNull(status.trainNumber, status.trainName).joinToString(" "),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = colors.text
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = lines.joinToString("\n"),
                fontSize = 13.sp,
                color = colors.textSecondary,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = freshness,
                fontSize = 12.sp,
                color = if (status.source == "stale") colors.danger else colors.textSecondary
            )
        }
    }
}
