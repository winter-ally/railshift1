package com.railshift.passenger.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.data.models.ThemeMode
import com.railshift.passenger.data.models.UserProfile
import com.railshift.passenger.ui.components.TabTopBar
import com.railshift.passenger.ui.theme.AvatarGradient
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showActionDialog by remember { mutableStateOf<String?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    ProfileContent(
        uiState = uiState,
        onEditProfileClick = onNavigateToEditProfile,
        onPaymentMethodsClick = { showActionDialog = "Payment Methods: Saved UPI (ravi@upi) and credit/debit cards are linked to your Railshift account." },
        onBookingHistoryClick = onNavigateToBookings,
        onSetThemeMode = { mode ->
            viewModel.setThemeMode(mode)
        },
        onPreferencesClick = {
            viewModel.toggleTheme()
        },
        onLogoutClick = { showLogoutDialog = true },
        onOpenLanguagePicker = onOpenLanguagePicker,
        modifier = modifier
    )

    if (showActionDialog != null) {
        val colors = RailshiftTheme.colors
        AlertDialog(
            onDismissRequest = { showActionDialog = null },
            title = {
                Text(
                    text = "Profile Action",
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
            },
            text = {
                Text(
                    text = showActionDialog.orEmpty(),
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showActionDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("OK", color = colors.onAccent)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showLogoutDialog) {
        val colors = RailshiftTheme.colors
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.menu_log_out),
                    fontWeight = FontWeight.Bold,
                    color = colors.danger
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to log out of Railshift?",
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.danger)
                ) {
                    Text("Log out", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = colors.text)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onEditProfileClick: () -> Unit,
    onPaymentMethodsClick: () -> Unit,
    onBookingHistoryClick: () -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onPreferencesClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    val isSystemDark = RailshiftTheme.isDark
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        TabTopBar(onLanguageClick = onOpenLanguagePicker)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Avatar 88dp with gradient & initials
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(AvatarGradient)
                    .testTag("profile_avatar"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.userProfile.initials,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Name & Email
            Text(
                text = uiState.userProfile.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = uiState.userProfile.email,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Theme & Appearance Customization Card
            AppearanceCustomizerCard(
                currentMode = uiState.themeMode,
                isCurrentlyDark = isSystemDark,
                onSetThemeMode = onSetThemeMode,
                modifier = Modifier.testTag("appearance_customizer_card")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Menu List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileMenuItem(
                    label = stringResource(R.string.menu_edit_profile),
                    icon = Icons.Outlined.Edit,
                    onClick = onEditProfileClick,
                    modifier = Modifier.testTag("menu_edit_profile")
                )
                ProfileMenuItem(
                    label = stringResource(R.string.menu_payment_methods),
                    icon = Icons.Outlined.CreditCard,
                    onClick = onPaymentMethodsClick,
                    modifier = Modifier.testTag("menu_payment_methods")
                )
                ProfileMenuItem(
                    label = stringResource(R.string.menu_booking_history),
                    icon = Icons.Outlined.History,
                    onClick = onBookingHistoryClick,
                    modifier = Modifier.testTag("menu_booking_history")
                )
                ProfileMenuItem(
                    label = stringResource(R.string.menu_preferences),
                    icon = Icons.Outlined.Tune,
                    trailingBadge = when (uiState.themeMode) {
                        ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                        ThemeMode.DARK -> stringResource(R.string.theme_dark)
                        ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                    },
                    onClick = onPreferencesClick,
                    modifier = Modifier.testTag("menu_preferences")
                )
                ProfileMenuItem(
                    label = stringResource(R.string.menu_log_out),
                    icon = Icons.Outlined.PowerSettingsNew,
                    onClick = onLogoutClick,
                    isDanger = true,
                    modifier = Modifier.testTag("menu_log_out")
                )
            }
        }
    }
}

@Composable
fun AppearanceCustomizerCard(
    currentMode: ThemeMode,
    isCurrentlyDark: Boolean,
    onSetThemeMode: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors

    val description = when (currentMode) {
        ThemeMode.LIGHT -> stringResource(R.string.theme_desc_light)
        ThemeMode.DARK -> stringResource(R.string.theme_desc_dark)
        ThemeMode.SYSTEM -> stringResource(R.string.theme_desc_system)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .background(colors.surface)
            .padding(14.dp)
    ) {
        // Card Header Row: Icon, Title & Quick Toggle Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(colors.accentContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Palette,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.theme_appearance),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.textSecondary
                )
            }

            // Quick Dark Mode Switch
            Switch(
                checked = (currentMode == ThemeMode.DARK || (currentMode == ThemeMode.SYSTEM && isCurrentlyDark)),
                onCheckedChange = { checked ->
                    onSetThemeMode(if (checked) ThemeMode.DARK else ThemeMode.LIGHT)
                },
                modifier = Modifier.testTag("theme_mode_switch"),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colors.surface,
                    checkedTrackColor = colors.accent,
                    uncheckedThumbColor = colors.surface,
                    uncheckedTrackColor = colors.borderStrong
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(thickness = 1.dp, color = colors.border)
        Spacer(modifier = Modifier.height(14.dp))

        // 3 Segmented Theme Mode Options: Light, Dark, System
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeOptionButton(
                label = stringResource(R.string.theme_light),
                icon = Icons.Outlined.LightMode,
                selected = currentMode == ThemeMode.LIGHT,
                onClick = { onSetThemeMode(ThemeMode.LIGHT) },
                testTag = "theme_option_light",
                modifier = Modifier.weight(1f)
            )

            ThemeOptionButton(
                label = stringResource(R.string.theme_dark),
                icon = Icons.Outlined.DarkMode,
                selected = currentMode == ThemeMode.DARK,
                onClick = { onSetThemeMode(ThemeMode.DARK) },
                testTag = "theme_option_dark",
                modifier = Modifier.weight(1f)
            )

            ThemeOptionButton(
                label = stringResource(R.string.theme_system),
                icon = Icons.Outlined.BrightnessAuto,
                selected = currentMode == ThemeMode.SYSTEM,
                onClick = { onSetThemeMode(ThemeMode.SYSTEM) },
                testTag = "theme_option_system",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ThemeOptionButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    val containerBg = if (selected) colors.accentContainer else colors.surface2
    val borderColor = if (selected) colors.accent else colors.border
    val contentColor = if (selected) colors.accent else colors.textSecondary
    val fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(if (selected) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(containerBg)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = fontWeight,
            color = contentColor
        )
    }
}

@Composable
fun ProfileMenuItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingBadge: String? = null,
    isDanger: Boolean = false
) {
    val colors = RailshiftTheme.colors
    val textColor = if (isDanger) colors.danger else colors.text
    val iconColor = if (isDanger) colors.danger else colors.text
    val iconBg = if (isDanger) Color.Transparent else colors.surface2

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, colors.border, RoundedCornerShape(14.dp))
            .background(colors.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier.weight(1f)
        )

        if (trailingBadge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.accentContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = trailingBadge,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.accent
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (!isDanger) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(name = "Profile Light")
@Composable
private fun ProfileLightPreview() {
    RailshiftTheme(darkTheme = false) {
        ProfileContent(
            uiState = ProfileUiState(),
            onEditProfileClick = {},
            onPaymentMethodsClick = {},
            onBookingHistoryClick = {},
            onSetThemeMode = {},
            onPreferencesClick = {},
            onLogoutClick = {},
            onOpenLanguagePicker = {}
        )
    }
}

@Preview(name = "Profile Dark")
@Composable
private fun ProfileDarkPreview() {
    RailshiftTheme(darkTheme = true) {
        ProfileContent(
            uiState = ProfileUiState(),
            onEditProfileClick = {},
            onPaymentMethodsClick = {},
            onBookingHistoryClick = {},
            onSetThemeMode = {},
            onPreferencesClick = {},
            onLogoutClick = {},
            onOpenLanguagePicker = {}
        )
    }
}
