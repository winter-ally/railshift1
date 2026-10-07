package com.railshift.passenger.ui.screens.platform

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.data.models.Station
import com.railshift.passenger.ui.components.SectionHeader
import com.railshift.passenger.ui.components.SubScreenTopBar
import com.railshift.passenger.ui.theme.HeroGradient
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun PlatformTicketScreen(
    viewModel: PlatformViewModel,
    onBackClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    onBookingSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var currentTicketNumber by remember { mutableStateOf("") }

    PlatformTicketContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onSelectStation = {
            viewModel.selectStation(it)
            viewModel.bookPlatformTicket { ticketId ->
                currentTicketNumber = ticketId
                showSuccessDialog = true
            }
        },
        onSearchPlatformClick = {
            viewModel.bookPlatformTicket { ticketId ->
                currentTicketNumber = ticketId
                showSuccessDialog = true
            }
        },
        onOpenLanguagePicker = onOpenLanguagePicker,
        modifier = modifier
    )

    if (showSuccessDialog) {
        val colors = RailshiftTheme.colors
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onBookingSuccess()
            },
            title = {
                Text(
                    text = "Platform Ticket Confirmed",
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
            },
            text = {
                Text(
                    text = "Platform entry ticket #$currentTicketNumber is issued for travel today at ${uiState.selectedStation?.name ?: "Vijayawada Jn (BZA)"}. Valid for 2 hours.",
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onBookingSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("Done", color = colors.onAccent)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun PlatformTicketContent(
    uiState: PlatformUiState,
    onBackClick: () -> Unit,
    onSelectStation: (Station) -> Unit,
    onSearchPlatformClick: () -> Unit,
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
            title = stringResource(R.string.platform_hero_title),
            subtitle = stringResource(R.string.platform_hero_sub),
            onBackClick = onBackClick,
            onLanguageClick = onOpenLanguagePicker
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp)
                .padding(bottom = 16.dp)
        ) {
            // Gradient Hero Banner with Platform illustration
            PlatformHeroBanner(modifier = Modifier.testTag("platform_hero_banner"))

            // Date of journey
            Text(
                text = stringResource(R.string.label_date_of_journey),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary,
                modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
            )

            // Journey Date field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, colors.borderStrong, RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .padding(horizontal = 12.dp, vertical = 11.dp)
                    .testTag("platform_date_field"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = uiState.journeyDateText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.text,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Search platform ticket button
            Button(
                onClick = onSearchPlatformClick,
                enabled = !uiState.isBooking,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent,
                    contentColor = colors.onAccent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(46.dp)
                    .testTag("btn_search_platform")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = colors.onAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.btn_search_platform),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Recent stations 2x2 grid
            SectionHeader(
                title = stringResource(R.string.section_recent_stations),
                actionText = stringResource(R.string.action_view_all),
                onActionClick = {}
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2x2 Grid
            val rows = uiState.recentStations.chunked(2)
            rows.forEach { rowStations ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowStations.forEach { station ->
                        RecentStationCard(
                            station = station,
                            onClick = { onSelectStation(station) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("recent_station_${station.code}")
                        )
                    }
                    if (rowStations.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom info note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.platform_info),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.textSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun PlatformHeroBanner(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(HeroGradient)
    ) {
        // Platform Illustration Canvas
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxHeight()
                .width(180.dp)
        ) {
            val w = size.width
            val h = size.height

            // Platform indicator badge "1"
            drawRoundRect(
                color = Color(0xFF0A2A6B),
                topLeft = Offset(w - 52.dp.toPx(), 12.dp.toPx()),
                size = Size(26.dp.toPx(), 26.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                style = Fill
            )

            // Platform Base
            drawRect(
                color = Color(0xFF0A2A6B).copy(alpha = 0.55f),
                topLeft = Offset(20.dp.toPx(), h - 32.dp.toPx()),
                size = Size(w, 32.dp.toPx())
            )

            // Train Coach
            val coachLeft = w - 120.dp.toPx()
            val coachTop = h - 102.dp.toPx()
            drawRoundRect(
                color = Color(0xFFE8F1FF),
                topLeft = Offset(coachLeft, coachTop),
                size = Size(86.dp.toPx(), 70.dp.toPx()),
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
            )
            // Coach roof stripe
            drawRoundRect(
                color = Color(0xFF1B6EF3),
                topLeft = Offset(coachLeft, coachTop),
                size = Size(86.dp.toPx(), 14.dp.toPx()),
                cornerRadius = CornerRadius(7.dp.toPx(), 7.dp.toPx())
            )
            // Coach windows
            drawRoundRect(
                color = Color(0xFF0A2A6B),
                topLeft = Offset(coachLeft + 10.dp.toPx(), coachTop + 20.dp.toPx()),
                size = Size(30.dp.toPx(), 24.dp.toPx()),
                cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF0A2A6B),
                topLeft = Offset(coachLeft + 46.dp.toPx(), coachTop + 20.dp.toPx()),
                size = Size(30.dp.toPx(), 24.dp.toPx()),
                cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
            )
            // Coach wheels
            drawCircle(
                color = Color(0xFFFFE29A),
                radius = 5.dp.toPx(),
                center = Offset(coachLeft + 16.dp.toPx(), coachTop + 56.dp.toPx())
            )
            drawCircle(
                color = Color(0xFFFFE29A),
                radius = 5.dp.toPx(),
                center = Offset(coachLeft + 70.dp.toPx(), coachTop + 56.dp.toPx())
            )
            // Platform edge line
            drawLine(
                color = Color(0xFF9FD0FF),
                start = Offset(10.dp.toPx(), h - 24.dp.toPx()),
                end = Offset(w, h - 24.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
        }

        // Text Overlay on Left
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(0.6f)
                .padding(start = 16.dp, top = 18.dp)
        ) {
            Text(
                text = stringResource(R.string.platform_hero_title),
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 22.sp
            )
            Text(
                text = stringResource(R.string.platform_hero_sub),
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9FD0FF)
            )
            Text(
                text = stringResource(R.string.platform_hero_desc),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFD4E4FF),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun RecentStationCard(
    station: Station,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .background(colors.surface)
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(colors.accentContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = station.code,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text
            )
            Text(
                text = station.city,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = colors.textSecondary,
                maxLines = 1
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Preview(name = "Platform Light")
@Composable
private fun PlatformLightPreview() {
    RailshiftTheme(darkTheme = false) {
        PlatformTicketContent(
            uiState = PlatformUiState(),
            onBackClick = {},
            onSelectStation = {},
            onSearchPlatformClick = {},
            onOpenLanguagePicker = {}
        )
    }
}

@Preview(name = "Platform Dark")
@Composable
private fun PlatformDarkPreview() {
    RailshiftTheme(darkTheme = true) {
        PlatformTicketContent(
            uiState = PlatformUiState(),
            onBackClick = {},
            onSelectStation = {},
            onSearchPlatformClick = {},
            onOpenLanguagePicker = {}
        )
    }
}
