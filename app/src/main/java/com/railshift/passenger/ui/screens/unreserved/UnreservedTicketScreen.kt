package com.railshift.passenger.ui.screens.unreserved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.History
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
import com.railshift.passenger.data.models.RecentUnreservedTicket
import com.railshift.passenger.data.models.Station
import com.railshift.passenger.ui.components.*
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun UnreservedTicketScreen(
    viewModel: UnreservedViewModel,
    onBackClick: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    onBookingSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }

    UnreservedTicketContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onSwapStations = { viewModel.swapStations() },
        onAdultsMinus = { viewModel.decrementAdults() },
        onAdultsPlus = { viewModel.incrementAdults() },
        onChildrenMinus = { viewModel.decrementChildren() },
        onChildrenPlus = { viewModel.incrementChildren() },
        onRecentTicketClick = { viewModel.selectRecentTicket(it) },
        onBookTicketClick = {
            viewModel.bookTicket {
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
                    text = "Paperless Ticket Booked",
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
            },
            text = {
                Text(
                    text = "General ticket booked from ${uiState.fromStation.name} to ${uiState.toStation.name} for ${uiState.adults} adult(s) and ${uiState.children} child(ren). Total fare: ₹${uiState.totalFare}. Valid for travel today.",
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
                    Text("View in Bookings", color = colors.onAccent)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun UnreservedTicketContent(
    uiState: UnreservedUiState,
    onBackClick: () -> Unit,
    onSwapStations: () -> Unit,
    onAdultsMinus: () -> Unit,
    onAdultsPlus: () -> Unit,
    onChildrenMinus: () -> Unit,
    onChildrenPlus: () -> Unit,
    onRecentTicketClick: (RecentUnreservedTicket) -> Unit,
    onBookTicketClick: () -> Unit,
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
            title = stringResource(R.string.tile_unreserved),
            subtitle = stringResource(R.string.tile_unreserved_sub),
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

            // Stations card
            StationCard(
                fromStation = uiState.fromStation,
                toStation = uiState.toStation,
                onSwapClick = onSwapStations
            )

            // Passengers section
            Text(
                text = stringResource(R.string.label_passengers),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary,
                modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
            )

            // Steppers container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .padding(horizontal = 12.dp)
            ) {
                // Adults row
                StepperRow(
                    title = stringResource(R.string.passenger_adults),
                    subtitle = stringResource(R.string.passenger_adults_sub),
                    count = uiState.adults,
                    minCount = 1,
                    onMinusClick = onAdultsMinus,
                    onPlusClick = onAdultsPlus,
                    testTagPrefix = "stepper_adults"
                )

                HorizontalDivider(thickness = 1.dp, color = colors.border)

                // Children row
                StepperRow(
                    title = stringResource(R.string.passenger_children),
                    subtitle = stringResource(R.string.passenger_children_sub),
                    count = uiState.children,
                    minCount = 0,
                    onMinusClick = onChildrenMinus,
                    onPlusClick = onChildrenPlus,
                    testTagPrefix = "stepper_children"
                )
            }

            // Fare breakdown summary
            val fareLines = buildList {
                add(
                    FareLine(
                        label = "${uiState.adults} adult${if (uiState.adults > 1) "s" else ""}",
                        amountText = "₹${uiState.totalAdultFare}"
                    )
                )
                if (uiState.children > 0) {
                    add(
                        FareLine(
                            label = "${uiState.children} child${if (uiState.children > 1) "ren" else ""}",
                            amountText = "₹${uiState.totalChildFare}"
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            FareSummary(
                lines = fareLines,
                totalLabel = stringResource(R.string.label_total_fare),
                totalAmountText = "₹${uiState.totalFare}"
            )

            // Book ticket button
            Button(
                onClick = onBookTicketClick,
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
                    .testTag("btn_book_unreserved_ticket")
            ) {
                if (uiState.isBooking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = colors.onAccent,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.btn_book_ticket),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Recent tickets section
            Text(
                text = stringResource(R.string.section_recent_tickets),
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
                uiState.recentTickets.forEachIndexed { index, ticket ->
                    if (index > 0) {
                        HorizontalDivider(thickness = 1.dp, color = colors.border)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRecentTicketClick(ticket) }
                            .padding(vertical = 11.dp)
                            .testTag("recent_ticket_$index"),
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
                                text = "${ticket.fromCode} to ${ticket.toCode}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.text
                            )
                            Text(
                                text = "${ticket.passengersText} • ${ticket.dateText}",
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

@Preview(name = "Unreserved Light")
@Composable
private fun UnreservedLightPreview() {
    RailshiftTheme(darkTheme = false) {
        UnreservedTicketContent(
            uiState = UnreservedUiState(
                fromStation = Station("BZA", "Vijayawada Jn", "Vijayawada"),
                toStation = Station("GNT", "Guntur Jn", "Guntur"),
                recentTickets = listOf(
                    RecentUnreservedTicket("BZA", "GNT", "2 adults", "28 Sep"),
                    RecentUnreservedTicket("BZA", "RJY", "1 adult", "14 Sep")
                )
            ),
            onBackClick = {},
            onSwapStations = {},
            onAdultsMinus = {},
            onAdultsPlus = {},
            onChildrenMinus = {},
            onChildrenPlus = {},
            onRecentTicketClick = {},
            onBookTicketClick = {},
            onOpenLanguagePicker = {}
        )
    }
}

@Preview(name = "Unreserved Dark")
@Composable
private fun UnreservedDarkPreview() {
    RailshiftTheme(darkTheme = true) {
        UnreservedTicketContent(
            uiState = UnreservedUiState(
                fromStation = Station("BZA", "Vijayawada Jn", "Vijayawada"),
                toStation = Station("GNT", "Guntur Jn", "Guntur"),
                recentTickets = listOf(
                    RecentUnreservedTicket("BZA", "GNT", "2 adults", "28 Sep"),
                    RecentUnreservedTicket("BZA", "RJY", "1 adult", "14 Sep")
                )
            ),
            onBackClick = {},
            onSwapStations = {},
            onAdultsMinus = {},
            onAdultsPlus = {},
            onChildrenMinus = {},
            onChildrenPlus = {},
            onRecentTicketClick = {},
            onBookTicketClick = {},
            onOpenLanguagePicker = {}
        )
    }
}
