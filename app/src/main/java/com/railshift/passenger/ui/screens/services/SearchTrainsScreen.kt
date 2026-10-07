package com.railshift.passenger.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
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
fun SearchTrainsScreen(
    onBackClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    var query by remember { mutableStateOf("Pinakini") }
    var resultText by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        SubScreenTopBar(
            title = stringResource(R.string.svc_search_trains),
            onBackClick = onBackClick,
            onLanguageClick = onOpenLanguagePicker
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Train number or name") },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = colors.accent)
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

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    resultText = "Found: 12711 Pinakini Express (BZA 06:25 → MAS 12:40)\nDaily Superfast Express • Runs Mon, Tue, Wed, Thu, Fri, Sat, Sun"
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("btn_search_train_schedule")
            ) {
                Text("Search Schedule", fontWeight = FontWeight.Bold, color = colors.onAccent)
            }

            if (resultText != null) {
                Spacer(modifier = Modifier.height(18.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface2)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Train Schedule",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = colors.text
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = resultText.orEmpty(),
                            fontSize = 13.sp,
                            color = colors.textSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
