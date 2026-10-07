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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.data.models.ThemeMode
import com.railshift.passenger.ui.components.SubScreenTopBar
import com.railshift.passenger.ui.theme.AvatarGradient
import com.railshift.passenger.ui.theme.RailshiftTheme
import com.railshift.passenger.ui.theme.WalletGradient
import java.text.NumberFormat
import java.util.*

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditFieldDialog by remember { mutableStateOf<String?>(null) }
    var showAddMoneyDialog by remember { mutableStateOf(false) }

    EditProfileContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onEditNameClick = { showEditFieldDialog = "name" },
        onEditEmailClick = { showEditFieldDialog = "email" },
        onEditPasswordClick = { showEditFieldDialog = "password" },
        onToggleThemeClick = { viewModel.toggleTheme() },
        onAddMoneyClick = { showAddMoneyDialog = true },
        onLogoutClick = { viewModel.logout() },
        onOpenLanguagePicker = onOpenLanguagePicker,
        modifier = modifier
    )

    if (showEditFieldDialog != null) {
        val colors = RailshiftTheme.colors
        val field = showEditFieldDialog.orEmpty()
        var textValue by remember {
            mutableStateOf(
                if (field == "name") uiState.userProfile.name
                else if (field == "email") uiState.userProfile.email
                else "secret123"
            )
        }

        AlertDialog(
            onDismissRequest = { showEditFieldDialog = null },
            title = {
                Text(
                    text = "Edit ${field.replaceFirstChar { it.uppercase() }}",
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
            },
            text = {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accent,
                        focusedLabelColor = colors.accent
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("edit_field_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (field == "name") {
                            viewModel.updateProfile(textValue, uiState.userProfile.email)
                        } else if (field == "email") {
                            viewModel.updateProfile(uiState.userProfile.name, textValue)
                        }
                        showEditFieldDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("Save", color = colors.onAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditFieldDialog = null }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showAddMoneyDialog) {
        val colors = RailshiftTheme.colors
        var addAmountText by remember { mutableStateOf("500") }

        AlertDialog(
            onDismissRequest = { showAddMoneyDialog = false },
            title = {
                Text(
                    text = "Add Money to rWallet",
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter amount in ₹ to add instantly via UPI or Net Banking:",
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = addAmountText,
                        onValueChange = { addAmountText = it },
                        singleLine = true,
                        prefix = { Text("₹ ") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.accent,
                            focusedLabelColor = colors.accent
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("add_money_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = addAmountText.toDoubleOrNull() ?: 500.0
                        viewModel.addMoney(amount)
                        showAddMoneyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("Add Money", color = colors.onAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMoneyDialog = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun EditProfileContent(
    uiState: ProfileUiState,
    onBackClick: () -> Unit,
    onEditNameClick: () -> Unit,
    onEditEmailClick: () -> Unit,
    onEditPasswordClick: () -> Unit,
    onToggleThemeClick: () -> Unit,
    onAddMoneyClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        SubScreenTopBar(
            title = stringResource(R.string.menu_edit_profile),
            onBackClick = onBackClick,
            onLanguageClick = onOpenLanguagePicker
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Avatar 88dp with Edit badge
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .testTag("avatar_with_badge"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(AvatarGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.userProfile.initials,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Edit badge (28dp circle)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .border(1.dp, colors.borderStrong, CircleShape)
                        .background(colors.surface)
                        .clickable(onClick = onEditNameClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.cd_change_photo),
                        tint = colors.accent,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fields List
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Name Field
                EditableProfileRow(
                    label = "Name",
                    value = uiState.userProfile.name,
                    icon = Icons.Outlined.Edit,
                    iconCd = stringResource(R.string.cd_edit_name),
                    onClick = onEditNameClick,
                    modifier = Modifier.testTag("field_edit_name")
                )

                // Email Field
                EditableProfileRow(
                    label = stringResource(R.string.label_email_address),
                    value = uiState.userProfile.email,
                    icon = Icons.Outlined.Edit,
                    iconCd = stringResource(R.string.cd_edit_email),
                    onClick = onEditEmailClick,
                    modifier = Modifier.testTag("field_edit_email")
                )

                // Password Field
                EditableProfileRow(
                    label = stringResource(R.string.label_password),
                    value = stringResource(R.string.masked_password),
                    icon = Icons.Outlined.Lock,
                    iconCd = stringResource(R.string.cd_change_password),
                    onClick = onEditPasswordClick,
                    modifier = Modifier.testTag("field_edit_password")
                )

                // Theme Mode Field
                EditableProfileRow(
                    label = stringResource(R.string.theme_appearance),
                    value = when (uiState.themeMode) {
                        ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                        ThemeMode.DARK -> stringResource(R.string.theme_dark)
                        ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                    },
                    icon = Icons.Outlined.Palette,
                    iconCd = stringResource(R.string.theme_appearance),
                    onClick = onToggleThemeClick,
                    modifier = Modifier.testTag("field_theme_mode")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // rWallet Gradient Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(WalletGradient)
                    .padding(16.dp)
                    .testTag("rwallet_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.wallet_title),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFD4E4FF)
                        )
                        Icon(
                            imageVector = Icons.Outlined.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    val formattedBalance = "₹" + NumberFormat.getNumberInstance(Locale.US).format(uiState.userProfile.walletBalance)
                    Text(
                        text = formattedBalance,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Add money pill button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .clickable(onClick = onAddMoneyClick)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("btn_add_money"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            tint = Color(0xFF0A2A6B),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.wallet_add_money),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0A2A6B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Log out button
            ProfileMenuItem(
                label = stringResource(R.string.menu_log_out),
                icon = Icons.Outlined.PowerSettingsNew,
                onClick = onLogoutClick,
                isDanger = true,
                modifier = Modifier.testTag("btn_logout_edit_profile")
            )
        }
    }
}

@Composable
private fun EditableProfileRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconCd: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
            .border(
                width = 0.dp,
                color = Color.Transparent
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = colors.textSecondary
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.text
            )
        }
        Icon(
            imageVector = icon,
            contentDescription = iconCd,
            tint = colors.accent,
            modifier = Modifier.size(18.dp)
        )
    }
    HorizontalDivider(thickness = 1.dp, color = colors.border)
}

@Preview(name = "Edit Profile Light")
@Composable
private fun EditProfileLightPreview() {
    RailshiftTheme(darkTheme = false) {
        EditProfileContent(
            uiState = ProfileUiState(),
            onBackClick = {},
            onEditNameClick = {},
            onEditEmailClick = {},
            onEditPasswordClick = {},
            onToggleThemeClick = {},
            onAddMoneyClick = {},
            onLogoutClick = {},
            onOpenLanguagePicker = {}
        )
    }
}

@Preview(name = "Edit Profile Dark")
@Composable
private fun EditProfileDarkPreview() {
    RailshiftTheme(darkTheme = true) {
        EditProfileContent(
            uiState = ProfileUiState(),
            onBackClick = {},
            onEditNameClick = {},
            onEditEmailClick = {},
            onEditPasswordClick = {},
            onToggleThemeClick = {},
            onAddMoneyClick = {},
            onLogoutClick = {},
            onOpenLanguagePicker = {}
        )
    }
}
