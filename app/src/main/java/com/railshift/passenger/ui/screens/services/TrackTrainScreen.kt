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
import com.railshift.passenger.ui.components.SubScreenTopBar
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun TrackTrainScreen(
    onBackClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    var trainNumber by remember { mutableStateOf("12711") }
    var trackingInfo by remember { mutableStateOf<String?>(null) }

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
                onValueChange = { trainNumber = it },
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
                    trackingInfo = "12711 Pinakini Express\nStatus: Running on time • Next station: Ongole (OGL) at 09:12 AM\nDeparted: Vijayawada Jn (06:25 AM)\nDestination: Chennai Central (ETA 12:40 PM)"
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("btn_track_train_status")
            ) {
                Text("Track Live Status", fontWeight = FontWeight.Bold, color = colors.onAccent)
            }

            if (trackingInfo != null) {
                Spacer(modifier = Modifier.height(18.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("track_train_result_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface2)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Live Running Status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = colors.text
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = trackingInfo.orEmpty(),
                            fontSize = 13.sp,
                            color = colors.textSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}
