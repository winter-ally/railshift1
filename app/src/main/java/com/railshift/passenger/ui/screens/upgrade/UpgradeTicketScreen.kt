package com.railshift.passenger.ui.screens.upgrade

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.data.models.TrainClass
import com.railshift.passenger.ui.components.ClassOptionCard
import com.railshift.passenger.ui.components.FareLine
import com.railshift.passenger.ui.components.FareSummary
import com.railshift.passenger.ui.components.SubScreenTopBar
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun UpgradeTicketScreen(
    viewModel: UpgradeViewModel,
    onBackClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    onUpgradeSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showTypeNoDialog by remember { mutableStateOf(false) }
    var showTrainPickerDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    UpgradeTicketContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onGalleryClick = { viewModel.onScanSuccess("73205184") },
        onTypeNoClick = { showTypeNoDialog = true },
        onSelectClass = { viewModel.selectClass(it) },
        onTrainPickerClick = { showTrainPickerDialog = true },
        onConfirmUpgradeClick = {
            viewModel.confirmUpgrade {
                showSuccessDialog = true
            }
        },
        onOpenLanguagePicker = onOpenLanguagePicker,
        modifier = modifier
    )

    if (showTypeNoDialog) {
        val colors = RailshiftTheme.colors
        var enteredNo by remember { mutableStateOf("73205184") }

        AlertDialog(
            onDismissRequest = { showTypeNoDialog = false },
            title = {
                Text(
                    text = "Enter Ticket Number",
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
            },
            text = {
                OutlinedTextField(
                    value = enteredNo,
                    onValueChange = { enteredNo = it },
                    label = { Text("Ticket number") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accent,
                        focusedLabelColor = colors.accent
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("type_ticket_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onScanSuccess(enteredNo)
                        showTypeNoDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("Submit", color = colors.onAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTypeNoDialog = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showTrainPickerDialog) {
        val colors = RailshiftTheme.colors
        val trains = listOf(
            Triple("12711", "Pinakini Express", "Today • departs 06:25"),
            Triple("12727", "Godavari Express", "Today • departs 17:15"),
            Triple("12704", "Falaknuma Express", "Today • departs 21:30")
        )

        AlertDialog(
            onDismissRequest = { showTrainPickerDialog = false },
            title = {
                Text(
                    text = "Select Train",
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    trains.forEach { (number, name, dep) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (uiState.selectedTrainNumber == number) colors.accentContainer else colors.surface2)
                                .clickable {
                                    viewModel.selectTrain(number, name, dep)
                                    showTrainPickerDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Train,
                                contentDescription = null,
                                tint = colors.accent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "$number • $name",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.text
                                )
                                Text(
                                    text = dep,
                                    fontSize = 12.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showTrainPickerDialog = false }) {
                    Text("Close", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showSuccessDialog) {
        val colors = RailshiftTheme.colors
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onUpgradeSuccess()
            },
            title = {
                Text(
                    text = "Upgrade Confirmed",
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
            },
            text = {
                Text(
                    text = "Your ticket has been upgraded to ${uiState.selectedClass.displayName} on train ${uiState.selectedTrainNumber}. Your new confirmed ticket is now available in My bookings.",
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onUpgradeSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("View in Bookings", color = colors.onAccent)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun UpgradeTicketContent(
    uiState: UpgradeUiState,
    onBackClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onTypeNoClick: () -> Unit,
    onSelectClass: (TrainClass) -> Unit,
    onTrainPickerClick: () -> Unit,
    onConfirmUpgradeClick: () -> Unit,
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
            title = stringResource(R.string.tile_upgrade),
            subtitle = stringResource(R.string.upgrade_header_sub),
            onBackClick = onBackClick,
            onLanguageClick = onOpenLanguagePicker
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp)
                .padding(bottom = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Camera Viewfinder Box
            CameraViewfinderBox(
                modifier = Modifier.testTag("camera_viewfinder")
            )

            // Caption
            Text(
                text = stringResource(R.string.upgrade_scan_caption),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            // Gallery and Type no. Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Gallery button
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, colors.borderStrong, RoundedCornerShape(12.dp))
                        .background(colors.surface)
                        .clickable(onClick = onGalleryClick)
                        .padding(vertical = 10.dp)
                        .testTag("btn_gallery"),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Photo,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.btn_gallery),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.text
                    )
                }

                // Type no. button
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, colors.borderStrong, RoundedCornerShape(12.dp))
                        .background(colors.surface)
                        .clickable(onClick = onTypeNoClick)
                        .padding(vertical = 10.dp)
                        .testTag("btn_type_no"),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Keyboard,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.btn_type_no),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.text
                    )
                }
            }

            // Ticket Scanned Status Banner
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.successContainer)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .testTag("banner_ticket_scanned"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = colors.success,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.banner_ticket_scanned),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.success
                )
            }

            // General Ticket Card
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                    .background(colors.surface)
                    .padding(12.dp)
                    .testTag("card_general_ticket")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.label_general_ticket),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(colors.accentContainer)
                            .padding(horizontal = 9.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.badge_valid),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.accent
                        )
                    }
                }

                Text(
                    text = "Ticket no. ${uiState.paperTicket.ticketNumber}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Station Line
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = uiState.paperTicket.fromCode,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.text
                        )
                        Text(
                            text = uiState.paperTicket.fromCity,
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Canvas(modifier = Modifier.weight(1f).height(1.dp)) {
                            drawLine(
                                color = colors.borderStrong,
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
                                color = colors.borderStrong,
                                start = Offset.Zero,
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = uiState.paperTicket.toCode,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.text
                        )
                        Text(
                            text = uiState.paperTicket.toCity,
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 1.dp, color = colors.border)
                Spacer(modifier = Modifier.height(10.dp))

                // Key-Value Grid: Passengers & Fare Paid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.label_passengers),
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                        Text(
                            text = uiState.paperTicket.passengersText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.text
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.label_fare_paid),
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                        Text(
                            text = "₹${uiState.paperTicket.farePaid}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.text
                        )
                    }
                }
            }

            // Choose Train and Class Section
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.section_choose_train_class),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Train picker row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, colors.borderStrong, RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .clickable(onClick = onTrainPickerClick)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .testTag("pick_train_row"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Train,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${uiState.selectedTrainNumber} • ${uiState.selectedTrainName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                    Text(
                        text = uiState.selectedTrainDeparture,
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Class Options List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.upgradeOptions.forEach { option ->
                    ClassOptionCard(
                        option = option,
                        selected = uiState.selectedClass == option.trainClass,
                        onSelect = { onSelectClass(option.trainClass) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fare Difference Summary
            FareSummary(
                lines = listOf(
                    FareLine(
                        label = "Fare difference × ${uiState.paperTicket.passengersCount}",
                        amountText = "₹${uiState.totalFareDifference}"
                    )
                ),
                totalLabel = stringResource(R.string.label_amount_to_pay),
                totalAmountText = "₹${uiState.totalFareDifference}"
            )

            // Confirm Class Button
            Button(
                onClick = onConfirmUpgradeClick,
                enabled = !uiState.isUpgrading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent,
                    contentColor = colors.onAccent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(46.dp)
                    .testTag("btn_confirm_upgrade")
            ) {
                if (uiState.isUpgrading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = colors.onAccent,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Confirm ${uiState.selectedClass.code}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Note at bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 1.dp)
                )
                Text(
                    text = stringResource(R.string.upgrade_note),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.textSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun CameraViewfinderBox(
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    val infiniteTransition = rememberInfiniteTransition(label = "scanLine")
    val scanYProgress by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanY"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface2),
        contentAlignment = Alignment.Center
    ) {
        // Corner brackets and animated scan line
        val accentColor = colors.accent
        Canvas(modifier = Modifier.fillMaxSize()) {
            val bracketSize = 26.dp.toPx()
            val bracketStroke = 3.dp.toPx()
            val margin = 16.dp.toPx()
            val cornerRadius = 8.dp.toPx()

            // Top-Left bracket
            drawLine(accentColor, Offset(margin, margin + cornerRadius), Offset(margin, margin + bracketSize), bracketStroke)
            drawLine(accentColor, Offset(margin + cornerRadius, margin), Offset(margin + bracketSize, margin), bracketStroke)
            drawArc(
                color = accentColor,
                startAngle = 180f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(margin, margin),
                size = androidx.compose.ui.geometry.Size(cornerRadius * 2, cornerRadius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(bracketStroke)
            )

            // Top-Right bracket
            val right = size.width - margin
            drawLine(accentColor, Offset(right, margin + cornerRadius), Offset(right, margin + bracketSize), bracketStroke)
            drawLine(accentColor, Offset(right - bracketSize, margin), Offset(right - cornerRadius, margin), bracketStroke)
            drawArc(
                color = accentColor,
                startAngle = 270f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(right - cornerRadius * 2, margin),
                size = androidx.compose.ui.geometry.Size(cornerRadius * 2, cornerRadius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(bracketStroke)
            )

            // Bottom-Left bracket
            val bottom = size.height - margin
            drawLine(accentColor, Offset(margin, bottom - bracketSize), Offset(margin, bottom - cornerRadius), bracketStroke)
            drawLine(accentColor, Offset(margin + cornerRadius, bottom), Offset(margin + bracketSize, bottom), bracketStroke)
            drawArc(
                color = accentColor,
                startAngle = 90f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(margin, bottom - cornerRadius * 2),
                size = androidx.compose.ui.geometry.Size(cornerRadius * 2, cornerRadius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(bracketStroke)
            )

            // Bottom-Right bracket
            drawLine(accentColor, Offset(right, bottom - bracketSize), Offset(right, bottom - cornerRadius), bracketStroke)
            drawLine(accentColor, Offset(right - bracketSize, bottom), Offset(right - cornerRadius, bottom), bracketStroke)
            drawArc(
                color = accentColor,
                startAngle = 0f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(right - cornerRadius * 2, bottom - cornerRadius * 2),
                size = androidx.compose.ui.geometry.Size(cornerRadius * 2, cornerRadius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(bracketStroke)
            )

            // Scan line
            val lineY = size.height * scanYProgress
            drawLine(
                color = accentColor.copy(alpha = 0.65f),
                start = Offset(30.dp.toPx(), lineY),
                end = Offset(size.width - 30.dp.toPx(), lineY),
                strokeWidth = 2.dp.toPx()
            )
        }

        // Center QR icon
        Icon(
            imageVector = Icons.Outlined.QrCodeScanner,
            contentDescription = null,
            tint = colors.textSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(44.dp)
        )
    }
}

@Preview(name = "Upgrade Light")
@Composable
private fun UpgradeLightPreview() {
    RailshiftTheme(darkTheme = false) {
        UpgradeTicketContent(
            uiState = UpgradeUiState(),
            onBackClick = {},
            onGalleryClick = {},
            onTypeNoClick = {},
            onSelectClass = {},
            onTrainPickerClick = {},
            onConfirmUpgradeClick = {},
            onOpenLanguagePicker = {}
        )
    }
}

@Preview(name = "Upgrade Dark")
@Composable
private fun UpgradeDarkPreview() {
    RailshiftTheme(darkTheme = true) {
        UpgradeTicketContent(
            uiState = UpgradeUiState(),
            onBackClick = {},
            onGalleryClick = {},
            onTypeNoClick = {},
            onSelectClass = {},
            onTrainPickerClick = {},
            onConfirmUpgradeClick = {},
            onOpenLanguagePicker = {}
        )
    }
}
