package com.railshift.passenger.ui.screens.reserved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.data.models.RecentSearch
import com.railshift.passenger.data.models.Station
import com.railshift.passenger.data.models.TrainClass
import com.railshift.passenger.ui.components.StationCard
import com.railshift.passenger.ui.components.SubScreenTopBar
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun ReservedTicketScreen(
    viewModel: ReservedViewModel,
    onBackClick: () -> Unit,
    onSearchTrains: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    ReservedTicketContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onSwapStations = { viewModel.swapStations() },
        onSelectDateChip = { viewModel.selectDateChip(it) },
        onSelectClass = { viewModel.selectClass(it) },
        onToggleAvailableOnly = { viewModel.toggleShowAvailableOnly(it) },
        onToggleFlexibleDates = { viewModel.toggleFlexibleDates(it) },
        onRecentSearchClick = {
            viewModel.selectRecentSearch(it)
            viewModel.searchTrains()
            onSearchTrains()
        },
        onSearchClick = {
            viewModel.searchTrains()
            onSearchTrains()
        },
        onOpenLanguagePicker = onOpenLanguagePicker,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservedTicketContent(
    uiState: ReservedUiState,
    onBackClick: () -> Unit,
    onSwapStations: () -> Unit,
    onSelectDateChip: (Int) -> Unit,
    onSelectClass: (TrainClass) -> Unit,
    onToggleAvailableOnly: (Boolean) -> Unit,
    onToggleFlexibleDates: (Boolean) -> Unit,
    onRecentSearchClick: (RecentSearch) -> Unit,
    onSearchClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    val scrollState = rememberScrollState()
    var showClassDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        SubScreenTopBar(
            title = stringResource(R.string.tile_reserved),
            subtitle = stringResource(R.string.tile_reserved_sub),
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

            // From / To Station Card
            StationCard(
                fromStation = uiState.fromStation,
                toStation = uiState.toStation,
                onSwapClick = onSwapStations
            )

            // Journey Date Section
            Text(
                text = stringResource(R.string.label_journey_date),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary,
                modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
            )

            // Date Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, colors.borderStrong, RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .clickable { /* Date picker dialog or chips */ }
                    .padding(horizontal = 12.dp, vertical = 11.dp)
                    .testTag("journey_date_field"),
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
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val chips = listOf(
                    stringResource(R.string.chip_today),
                    stringResource(R.string.chip_tomorrow),
                    "Mon, 12 Oct"
                )
                chips.forEachIndexed { index, text ->
                    val isSelected = uiState.selectedDateChipIndex == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .then(
                                if (isSelected) Modifier.background(colors.accentContainer)
                                else Modifier
                                    .border(1.dp, colors.borderStrong, RoundedCornerShape(16.dp))
                                    .background(colors.surface)
                            )
                            .clickable { onSelectDateChip(index) }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                            .testTag("date_chip_$index")
                    ) {
                        Text(
                            text = text,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) colors.accent else colors.text
                        )
                    }
                }
            }

            // Class Section
            Text(
                text = stringResource(R.string.label_class),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary,
                modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
            )

            // Class Field with Dropdown
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, colors.borderStrong, RoundedCornerShape(12.dp))
                        .background(colors.surface)
                        .clickable { showClassDropdown = true }
                        .padding(horizontal = 12.dp, vertical = 11.dp)
                        .testTag("class_dropdown_field"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AirlineSeatReclineExtra,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = uiState.selectedClass.displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.text,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = showClassDropdown,
                    onDismissRequest = { showClassDropdown = false },
                    modifier = Modifier.background(colors.surface)
                ) {
                    TrainClass.values().forEach { trainClass ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = trainClass.displayName,
                                    color = if (trainClass == uiState.selectedClass) colors.accent else colors.text,
                                    fontWeight = if (trainClass == uiState.selectedClass) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSelectClass(trainClass)
                                showClassDropdown = false
                            }
                        )
                    }
                }
            }

            // Checkboxes Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
            ) {
                HorizontalDivider(thickness = 1.dp, color = colors.border)

                // Checkbox 1: Show only available berths
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleAvailableOnly(!uiState.showAvailableOnly) }
                        .padding(vertical = 12.dp)
                        .testTag("chk_available_only"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (uiState.showAvailableOnly) Icons.Outlined.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
                        contentDescription = null,
                        tint = if (uiState.showAvailableOnly) colors.accent else colors.textSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.chk_available_only),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.text
                    )
                }

                HorizontalDivider(thickness = 1.dp, color = colors.border)

                // Checkbox 2: Flexible with dates
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleFlexibleDates(!uiState.isFlexibleWithDates) }
                        .padding(vertical = 12.dp)
                        .testTag("chk_flexible_dates"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (uiState.isFlexibleWithDates) Icons.Outlined.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
                        contentDescription = null,
                        tint = if (uiState.isFlexibleWithDates) colors.accent else colors.textSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.chk_flexible_dates),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.text
                    )
                }
            }

            // Search Trains Button
            Button(
                onClick = onSearchClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent,
                    contentColor = colors.onAccent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .height(46.dp)
                    .testTag("btn_search_trains")
            ) {
                Text(
                    text = stringResource(R.string.btn_search_trains),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Recent Searches Section
            Text(
                text = stringResource(R.string.section_recent_searches),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .padding(horizontal = 12.dp)
            ) {
                uiState.recentSearches.forEachIndexed { index, search ->
                    if (index > 0) {
                        HorizontalDivider(thickness = 1.dp, color = colors.border)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRecentSearchClick(search) }
                            .padding(vertical = 11.dp)
                            .testTag("recent_search_$index"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${search.fromCode} to ${search.toCode}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.text
                            )
                            Text(
                                text = "${search.dateText} • ${search.classCode}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                color = colors.textSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Reserved Screen Light")
@Composable
private fun ReservedScreenLightPreview() {
    RailshiftTheme(darkTheme = false) {
        ReservedTicketContent(
            uiState = ReservedUiState(
                fromStation = Station("BZA", "Vijayawada Jn", "Vijayawada"),
                toStation = Station("MAS", "Chennai Central", "Chennai"),
                recentSearches = listOf(
                    RecentSearch("BZA", "HYB", "Sat, 17 Oct", "3A"),
                    RecentSearch("BZA", "VSKP", "Fri, 23 Oct", "SL")
                )
            ),
            onBackClick = {},
            onSwapStations = {},
            onSelectDateChip = {},
            onSelectClass = {},
            onToggleAvailableOnly = {},
            onToggleFlexibleDates = {},
            onRecentSearchClick = {},
            onSearchClick = {},
            onOpenLanguagePicker = {}
        )
    }
}

@Preview(name = "Reserved Screen Dark")
@Composable
private fun ReservedScreenDarkPreview() {
    RailshiftTheme(darkTheme = true) {
        ReservedTicketContent(
            uiState = ReservedUiState(
                fromStation = Station("BZA", "Vijayawada Jn", "Vijayawada"),
                toStation = Station("MAS", "Chennai Central", "Chennai"),
                recentSearches = listOf(
                    RecentSearch("BZA", "HYB", "Sat, 17 Oct", "3A"),
                    RecentSearch("BZA", "VSKP", "Fri, 23 Oct", "SL")
                )
            ),
            onBackClick = {},
            onSwapStations = {},
            onSelectDateChip = {},
            onSelectClass = {},
            onToggleAvailableOnly = {},
            onToggleFlexibleDates = {},
            onRecentSearchClick = {},
            onSearchClick = {},
            onOpenLanguagePicker = {}
        )
    }
}
