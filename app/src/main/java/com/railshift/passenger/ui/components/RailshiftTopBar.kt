package com.railshift.passenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.ui.theme.LocalToggleTheme
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun TabTopBar(
    onLanguageClick: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    onThemeToggle: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    val isDark = RailshiftTheme.isDark
    val toggleTheme = onThemeToggle ?: LocalToggleTheme.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Language button (38dp round)
        IconButton(
            onClick = onLanguageClick,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(colors.surface2)
                .testTag("topbar_language_button")
        ) {
            Icon(
                imageVector = Icons.Outlined.Translate,
                contentDescription = stringResource(R.string.cd_language),
                tint = colors.text,
                modifier = Modifier.size(20.dp)
            )
        }

        // Brand center
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Train,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text
            )
        }

        // Right actions: Theme toggle + Notifications button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Theme toggle button
            IconButton(
                onClick = toggleTheme,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(colors.surface2)
                    .testTag("topbar_theme_toggle_button")
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                    contentDescription = if (isDark) stringResource(R.string.cd_theme_toggle_light) else stringResource(R.string.cd_theme_toggle_dark),
                    tint = if (isDark) Color(0xFFFFB300) else colors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Notifications button with red dot
            Box(
                modifier = Modifier.size(38.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(colors.surface2)
                        .testTag("topbar_notifications_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = stringResource(R.string.cd_notifications),
                        tint = colors.text,
                        modifier = Modifier.size(20.dp)
                    )
                }
                // Red dot
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 7.dp, end = 8.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(colors.danger)
                        .border(1.5.dp, colors.surface2, CircleShape)
                )
            }
        }
    }
}

@Composable
fun SubScreenTopBar(
    title: String,
    subtitle: String? = null,
    onBackClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onThemeToggle: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    val isDark = RailshiftTheme.isDark
    val toggleTheme = onThemeToggle ?: LocalToggleTheme.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button (38dp round)
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(colors.surface2)
                .testTag("topbar_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.cd_back),
                tint = colors.text,
                modifier = Modifier.size(20.dp)
            )
        }

        // Title and optional subtitle
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                maxLines = 1
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.textSecondary,
                    maxLines = 1
                )
            }
        }

        // Right actions: Theme toggle + Language button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = toggleTheme,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(colors.surface2)
                    .testTag("sub_topbar_theme_toggle_button")
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                    contentDescription = if (isDark) stringResource(R.string.cd_theme_toggle_light) else stringResource(R.string.cd_theme_toggle_dark),
                    tint = if (isDark) Color(0xFFFFB300) else colors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Language button (38dp round)
            IconButton(
                onClick = onLanguageClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(colors.surface2)
                    .testTag("sub_topbar_language_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Translate,
                    contentDescription = stringResource(R.string.cd_language),
                    tint = colors.text,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
