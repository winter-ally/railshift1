package com.railshift.passenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.ui.theme.RailshiftTheme

enum class BottomBarTab(val route: String) {
    HOME("home"),
    BOOKINGS("my_bookings"),
    HELP("help"),
    PROFILE("profile")
}

@Composable
fun RailshiftBottomBar(
    currentRoute: String,
    onTabSelected: (String) -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
    ) {
        HorizontalDivider(thickness = 1.dp, color = colors.border)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 10.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Tab 1: Home
            BottomBarItem(
                label = stringResource(R.string.nav_home),
                icon = Icons.Outlined.Home,
                selected = currentRoute == "home" || currentRoute == "platform",
                onClick = { onTabSelected("home") },
                modifier = Modifier.weight(1f).testTag("bottom_nav_home")
            )

            // Tab 2: My bookings
            BottomBarItem(
                label = stringResource(R.string.nav_bookings),
                icon = Icons.Outlined.ConfirmationNumber,
                selected = currentRoute == "my_bookings",
                onClick = { onTabSelected("my_bookings") },
                modifier = Modifier.weight(1f).testTag("bottom_nav_bookings")
            )

            // Center slot: Scan (Raised 52dp button)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onScanClick
                        )
                        .testTag("bottom_nav_scan")
                ) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-18).dp)
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(colors.accent)
                            .border(4.dp, colors.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.QrCodeScanner,
                            contentDescription = stringResource(R.string.cd_scan),
                            tint = colors.onAccent,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.nav_scan),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accent,
                        modifier = Modifier.offset(y = (-14).dp)
                    )
                }
            }

            // Tab 4: Help
            BottomBarItem(
                label = stringResource(R.string.nav_help),
                icon = Icons.Outlined.HelpOutline,
                selected = currentRoute == "help",
                onClick = { onTabSelected("help") },
                modifier = Modifier.weight(1f).testTag("bottom_nav_help")
            )

            // Tab 5: Profile
            BottomBarItem(
                label = stringResource(R.string.nav_profile),
                icon = Icons.Outlined.PersonOutline,
                selected = currentRoute == "profile" || currentRoute == "edit_profile",
                onClick = { onTabSelected("profile") },
                modifier = Modifier.weight(1f).testTag("bottom_nav_profile")
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    val contentColor = if (selected) colors.accent else colors.textSecondary
    val fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium

    Column(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = fontWeight,
            color = contentColor
        )
    }
}
