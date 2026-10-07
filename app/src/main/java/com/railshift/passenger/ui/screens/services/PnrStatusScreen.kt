package com.railshift.passenger.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.data.repository.RailshiftRepository
import com.railshift.passenger.ui.components.SubScreenTopBar
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun PnrStatusScreen(
    repository: RailshiftRepository,
    onBackClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    var pnrInput by remember { mutableStateOf("4218903512") }
    var searchedTrip by remember { mutableStateOf<com.railshift.passenger.data.models.Trip?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        SubScreenTopBar(
            title = stringResource(R.string.svc_pnr_status),
            onBackClick = onBackClick,
            onLanguageClick = onOpenLanguagePicker
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            OutlinedTextField(
                value = pnrInput,
                onValueChange = { if (it.length <= 10) pnrInput = it },
                label = { Text("Enter 10-digit PNR") },
                leadingIcon = {
                    Icon(Icons.Outlined.FactCheck, contentDescription = null, tint = colors.accent)
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accent,
                    focusedLabelColor = colors.accent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pnr_input_field")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    searchedTrip = repository.checkPNR(pnrInput)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("btn_check_pnr")
            ) {
                Text("Get Status", fontWeight = FontWeight.Bold, color = colors.onAccent)
            }

            if (searchedTrip != null) {
                val trip = searchedTrip!!
                Spacer(modifier = Modifier.height(18.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("pnr_result_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface2)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "PNR ${trip.pnr}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = colors.text
                            )
                            Box(
                                modifier = Modifier
                                    .background(colors.successContainer, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "CNF (Confirmed)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.success
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${trip.trainNumber} • ${trip.trainName}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.text
                        )
                        Text(
                            text = "${trip.fromCode} → ${trip.toCode} • ${trip.dateText}",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(thickness = 1.dp, color = colors.border)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Coach", fontSize = 11.sp, color = colors.textSecondary)
                                Text(trip.coach, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.text)
                            }
                            Column {
                                Text("Berth", fontSize = 11.sp, color = colors.textSecondary)
                                Text(trip.berth, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.text)
                            }
                            Column {
                                Text("Passengers", fontSize = 11.sp, color = colors.textSecondary)
                                Text("${trip.passengers} Adult", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.text)
                            }
                        }
                    }
                }
            }
        }
    }
}
