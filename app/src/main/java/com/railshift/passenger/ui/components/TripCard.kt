package com.railshift.passenger.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.data.models.Trip
import com.railshift.passenger.data.models.TripStatus
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun TripCard(
    trip: Trip,
    modifier: Modifier = Modifier,
    showTrainPrefix: Boolean = false
) {
    val colors = RailshiftTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, colors.border, RoundedCornerShape(14.dp))
            .background(colors.surface)
            .padding(12.dp)
            .testTag("trip_card_${trip.id}")
    ) {
        Column {
            // Row 1: Date & Train info + Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val trainText = if (showTrainPrefix) {
                    "${trip.dateText} • Train ${trip.trainNumber}"
                } else {
                    "${trip.dateText} • ${trip.trainNumber}"
                }

                Text(
                    text = trainText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )

                val (badgeBg, badgeTextColor, badgeLabelRes) = when (trip.status) {
                    TripStatus.CONFIRMED -> Triple(
                        colors.successContainer,
                        colors.success,
                        R.string.badge_confirmed
                    )
                    TripStatus.COMPLETED -> Triple(
                        colors.surface2,
                        colors.textSecondary,
                        R.string.badge_completed
                    )
                    else -> Triple(
                        colors.accentContainer,
                        colors.accent,
                        R.string.badge_confirmed
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(badgeBg)
                        .padding(horizontal = 9.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(badgeLabelRes),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = badgeTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Times & Stations with dashed line and train icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Departure
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = trip.departureTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = trip.fromCode,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary
                    )
                }

                // Center connecting line with train icon
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val lineColor = colors.borderStrong
                    Canvas(modifier = Modifier.weight(1f).height(1.dp)) {
                        drawLine(
                            color = lineColor,
                            start = Offset.Zero,
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Outlined.Train,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(16.dp)
                    )
                    Canvas(modifier = Modifier.weight(1f).height(1.dp)) {
                        drawLine(
                            color = lineColor,
                            start = Offset.Zero,
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        )
                    }
                }

                // Arrival
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = trip.arrivalTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text,
                        textAlign = TextAlign.End,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = trip.toCode,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}
