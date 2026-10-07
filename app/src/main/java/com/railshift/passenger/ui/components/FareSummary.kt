package com.railshift.passenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.railshift.passenger.ui.theme.RailshiftTheme

data class FareLine(
    val label: String,
    val amountText: String
)

@Composable
fun FareSummary(
    lines: List<FareLine>,
    totalLabel: String,
    totalAmountText: String,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface2)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("fare_summary")
    ) {
        lines.forEach { line ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = line.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.textSecondary
                )
                Text(
                    text = line.amountText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(thickness = 1.dp, color = colors.border)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = totalLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = colors.text
            )
            Text(
                text = totalAmountText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text
            )
        }
    }
}
