package com.railshift.passenger.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ViewColumn
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
import com.railshift.passenger.ui.components.SubScreenTopBar
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun CoachPositionScreen(
    onBackClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    var trainNumber by remember { mutableStateOf("12711") }
    var coaches by remember { mutableStateOf<List<String>?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        SubScreenTopBar(
            title = stringResource(R.string.svc_coach_position),
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
                onValueChange = { trainNumber = it },
                label = { Text("Enter 5-digit Train Number") },
                leadingIcon = {
                    Icon(Icons.Outlined.ViewColumn, contentDescription = null, tint = colors.accent)
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accent,
                    focusedLabelColor = colors.accent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("coach_train_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    coaches = listOf("ENG", "SLR", "GS", "S1", "S2", "S3", "S4", "S5", "S6", "B1", "B2", "B3", "B4", "A1", "GS", "SLR")
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("btn_find_coach_layout")
            ) {
                Text("Find Coach Layout", fontWeight = FontWeight.Bold, color = colors.onAccent)
            }

            if (coaches != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "$trainNumber Pinakini Express (Platform Rake Layout)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth().testTag("coach_layout_row")
                ) {
                    items(coaches!!) { coach ->
                        val isEngine = coach == "ENG"
                        val isAC = coach.startsWith("B") || coach.startsWith("A")
                        val isSleeper = coach.startsWith("S") && coach != "SLR"

                        Box(
                            modifier = Modifier
                                .width(56.dp)
                                .height(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (isAC) colors.accent else colors.borderStrong,
                                    RoundedCornerShape(8.dp)
                                )
                                .background(
                                    if (isAC) colors.accentContainer
                                    else if (isEngine) colors.textSecondary.copy(alpha = 0.2f)
                                    else colors.surface2
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = coach,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAC) colors.accent else colors.text
                                )
                                Text(
                                    text = if (isEngine) "Engine" else if (isAC) "AC" else if (isSleeper) "SL" else "GEN",
                                    fontSize = 10.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
