package com.railshift.passenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.data.models.TrainClass
import com.railshift.passenger.data.models.UpgradeClassOption
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun ClassOptionCard(
    option: UpgradeClassOption,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    val shape = RoundedCornerShape(12.dp)

    val borderModifier = if (selected) {
        Modifier.border(2.dp, colors.accent, shape)
    } else {
        Modifier.border(1.dp, colors.borderStrong, shape)
    }

    val bgModifier = if (selected) {
        Modifier.background(colors.accentContainer)
    } else {
        Modifier.background(colors.surface)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .then(borderModifier)
            .then(bgModifier)
            .clickable(onClick = onSelect)
            .padding(if (selected) 11.dp else 12.dp)
            .testTag("class_option_${option.trainClass.code}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (selected) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (selected) colors.accent else colors.textSecondary,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.trainClass.displayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text
            )
            val availText = if (option.availableBerths > 0) {
                "${stringResource(R.string.badge_valid)} • ${option.availableBerths} ${stringResource(R.string.label_from).replace("From", "berths")}"
            } else {
                "Waitlisted"
            }
            Text(
                text = "Available • ${option.availableBerths} berths",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.success
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "+₹${option.fareDifferencePerPerson}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                textAlign = TextAlign.End
            )
            Text(
                text = stringResource(R.string.label_per_person),
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = colors.textSecondary,
                textAlign = TextAlign.End
            )
        }
    }
}
