package com.railshift.passenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.data.models.Station
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun StationCard(
    fromStation: Station,
    toStation: Station,
    onSwapClick: () -> Unit,
    onFromClick: (() -> Unit)? = null,
    onToClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.accentContainer)
            .padding(12.dp)
            .testTag("station_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surface)
                .padding(horizontal = 12.dp, vertical = 2.dp)
        ) {
            // From Station Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 11.dp)
                    .then(
                        if (onFromClick != null) Modifier.clickable(onClick = onFromClick)
                        else Modifier
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.label_from),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "${fromStation.name} • ${fromStation.code}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                }
                // Swap Button
                IconButton(
                    onClick = onSwapClick,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(1.dp, colors.borderStrong, CircleShape)
                        .background(colors.surface)
                        .testTag("station_swap_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SwapVert,
                        contentDescription = stringResource(R.string.swap_stations),
                        tint = colors.accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            HorizontalDivider(thickness = 1.dp, color = colors.border)

            // To Station Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 11.dp)
                    .then(
                        if (onToClick != null) Modifier.clickable(onClick = onToClick)
                        else Modifier
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.label_to),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "${toStation.name} • ${toStation.code}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                }
            }
        }
    }
}
