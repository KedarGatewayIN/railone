package com.indianrailways.railone.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianrailways.railone.data.model.ReservedTicket
import com.indianrailways.railone.data.model.TicketStatus
import com.indianrailways.railone.data.model.UnreservedTicket
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.components.DashedDivider
import com.indianrailways.railone.ui.components.TicketCardShape
import com.indianrailways.railone.ui.theme.RailOneActiveGreen
import com.indianrailways.railone.ui.theme.RailOneActiveGreenBg
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneOrange
import com.indianrailways.railone.ui.theme.RailOneSurface
import com.indianrailways.railone.ui.theme.RailOneWarningRed
import com.indianrailways.railone.ui.theme.RailOneWarningRedBg
import com.indianrailways.railone.ui.theme.TextMuted
import com.indianrailways.railone.ui.theme.TextPrimary
import com.indianrailways.railone.ui.theme.TextSecondary
import kotlinx.coroutines.launch

enum class BookingTab(val title: String) {
    UPCOMING("Upcoming"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    ALL("All")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    ticketRepository: TicketRepository,
    onBack: () -> Unit,
    onViewUnreservedTicket: (String) -> Unit,
    onBookAgain: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(BookingTab.UPCOMING) }
    var selectedFilterChip by remember { mutableStateOf("All") }
    var ticketToCancel by remember { mutableStateOf<UnreservedTicket?>(null) }

    val unreservedTickets by ticketRepository.unreservedTickets.collectAsState()
    val reservedTickets by ticketRepository.reservedTickets.collectAsState()
    val platformTickets by ticketRepository.platformTickets.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // DYNAMIC FILTERING BY TAB
    val tabFilteredUnreserved = remember(unreservedTickets, selectedTab) {
        when (selectedTab) {
            BookingTab.UPCOMING -> unreservedTickets.filter { it.status == TicketStatus.ACTIVE }
            BookingTab.COMPLETED -> unreservedTickets.filter { it.status == TicketStatus.COMPLETED }
            BookingTab.CANCELLED -> unreservedTickets.filter { it.status == TicketStatus.CANCELLED }
            BookingTab.ALL -> unreservedTickets
        }
    }

    val tabFilteredReserved = remember(reservedTickets, selectedTab) {
        when (selectedTab) {
            BookingTab.UPCOMING -> reservedTickets.filter { it.status == TicketStatus.ACTIVE }
            BookingTab.COMPLETED -> reservedTickets.filter { it.status == TicketStatus.COMPLETED }
            BookingTab.CANCELLED -> reservedTickets.filter { it.status == TicketStatus.CANCELLED }
            BookingTab.ALL -> reservedTickets
        }
    }

    val tabFilteredPlatform = remember(platformTickets, selectedTab) {
        when (selectedTab) {
            BookingTab.UPCOMING -> platformTickets.filter { it.status == TicketStatus.ACTIVE }
            BookingTab.COMPLETED -> platformTickets.filter { it.status == TicketStatus.COMPLETED }
            BookingTab.CANCELLED -> platformTickets.filter { it.status == TicketStatus.CANCELLED }
            BookingTab.ALL -> platformTickets
        }
    }

    // DYNAMIC FILTERING BY CHIP
    val displayUnreserved = (selectedFilterChip == "All" || selectedFilterChip == "Unreserved")
    val displayReserved = (selectedFilterChip == "All" || selectedFilterChip == "Reserved")
    val displayPlatform = (selectedFilterChip == "All" || selectedFilterChip == "Platform")

    val visibleUnreserved = if (displayUnreserved) tabFilteredUnreserved else emptyList()
    val visibleReserved = if (displayReserved) tabFilteredReserved else emptyList()
    val visiblePlatform = if (displayPlatform) tabFilteredPlatform else emptyList()

    val currentTabCount = visibleUnreserved.size + visibleReserved.size + visiblePlatform.size
    val isEmpty = currentTabCount == 0

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RailOneSurface)
        ) {
            // Top Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RailOneBlue)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "My Bookings",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Sorted by latest departure date")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = Color.White
                    )
                }
            }

            // Subheader with count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedTab.title} ($currentTabCount)",
                    color = RailOneOrange,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Synchronized with IRCTC & UTS Servers")
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val chips = listOf("All", "Unreserved", "Reserved", "Platform")
                items(chips) { chip ->
                    val isSelected = selectedFilterChip == chip
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilterChip = chip },
                        label = {
                            Text(
                                text = chip,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RailOneBlue.copy(alpha = 0.12f),
                            selectedLabelColor = RailOneBlue,
                            containerColor = Color(0xFFF1F3F6),
                            labelColor = TextPrimary
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) RailOneBlue else Color.Transparent
                        )
                    )
                }
            }

            // Ticket List (or Empty State)
            if (isEmpty) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEF4FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Train,
                                    contentDescription = "Empty",
                                    tint = RailOneBlue,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No ${selectedTab.title} Bookings",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tickets booked for ${selectedTab.title.lowercase()} journeys will appear here automatically.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = onBookAgain,
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RailOneBlue)
                            ) {
                                Text("Book Ticket Now", fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Unreserved Tickets (only user-booked ones)
                    items(visibleUnreserved, key = { it.utsNumber }) { unreserved ->
                        UnreservedTicketCard(
                            ticket = unreserved,
                            onViewDetails = { onViewUnreservedTicket(unreserved.utsNumber) },
                            onBookAgain = onBookAgain,
                            onCancel = { ticketToCancel = unreserved }
                        )
                    }

                    // Reserved Tickets (only user-booked ones)
                    items(visibleReserved, key = { it.pnr }) { reserved ->
                        ReservedTicketCard(
                            ticket = reserved,
                            onViewDetails = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("PNR ${reserved.pnr} Status: Confirmed in ${reserved.coach}-${reserved.berth}")
                                }
                            },
                            onBookAgain = onBookAgain
                        )
                    }
                }
            }

            // Bottom Pill Tabs: Upcoming | Completed | Cancelled | All
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFFE8F1FC),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BookingTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        val tabColor = if (isSelected) RailOneOrange else TextSecondary

                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = when (tab) {
                                    BookingTab.UPCOMING -> Icons.Outlined.ConfirmationNumber
                                    BookingTab.COMPLETED -> Icons.Default.CheckCircle
                                    BookingTab.CANCELLED -> Icons.Default.Cancel
                                    BookingTab.ALL -> Icons.Default.ConfirmationNumber
                                },
                                contentDescription = tab.title,
                                tint = tabColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = tabColor
                            )
                        }
                    }
                }
            }
        }

        // Cancellation Dialog
        ticketToCancel?.let { ticket ->
            AlertDialog(
                onDismissRequest = { ticketToCancel = null },
                title = {
                    Text(
                        text = "Cancel Ticket?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Are you sure you want to cancel UTS Ticket ${ticket.utsNumber} (${ticket.sourceStation.code} → ${ticket.destStation.code})?",
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "As per Indian Railways UTS refund guidelines, full refund of ${ticket.formattedFare} will be credited back to your Rail Wallet.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            ticketRepository.cancelUnreservedTicket(ticket.utsNumber)
                            ticketToCancel = null
                            scope.launch {
                                snackbarHostState.showSnackbar("Ticket ${ticket.utsNumber} cancelled. Refund credited to Rail Wallet.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RailOneWarningRed)
                    ) {
                        Text("Confirm Cancel")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { ticketToCancel = null }) {
                        Text("Keep Ticket")
                    }
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}

@Composable
fun UnreservedTicketCard(
    ticket: UnreservedTicket,
    onViewDetails: () -> Unit,
    onBookAgain: () -> Unit,
    onCancel: () -> Unit = {}
) {
    val isCancelled = ticket.status == TicketStatus.CANCELLED
    val isCompleted = ticket.status == TicketStatus.COMPLETED

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TicketCardShape(notchPositionFraction = 0.72f))
            .border(
                1.dp,
                if (isCancelled) Color(0xFFE0E0E0) else Color(0xFFF9A825).copy(alpha = 0.6f),
                TicketCardShape(notchPositionFraction = 0.72f)
            )
            .clickable { onViewDetails() },
        shape = TicketCardShape(notchPositionFraction = 0.72f),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Top Row: Status Badge | UTS Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            isCancelled -> RailOneWarningRedBg
                            isCompleted -> Color(0xFFF1F3F6)
                            else -> RailOneActiveGreenBg
                        }
                    ) {
                        Text(
                            text = when {
                                isCancelled -> "● CANCELLED"
                                isCompleted -> "COMPLETED"
                                else -> "● ACTIVE"
                            },
                            color = when {
                                isCancelled -> RailOneWarningRed
                                isCompleted -> TextSecondary
                                else -> RailOneActiveGreen
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = "UTS: ${ticket.utsNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Train & Journey Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Class: ${ticket.travelClass} (${ticket.trainType.label})",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Booked: ${ticket.formattedBookingDateTime}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Route: FROM → TO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = ticket.sourceStation.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = ticket.sourceStation.code,
                            fontSize = 11.sp,
                            color = RailOneBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "— ${ticket.distanceKm} km —",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Text(
                            text = ticket.passengerSummary,
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = ticket.destStation.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = ticket.destStation.code,
                            fontSize = 11.sp,
                            color = RailOneBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Dashed Divider
            DashedDivider(
                color = Color(0xFFF9A825).copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            // Bottom Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fare: ${ticket.formattedFare}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = RailOneOrange
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (ticket.status == TicketStatus.ACTIVE) {
                        Text(
                            text = "Cancel",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = RailOneWarningRed,
                            modifier = Modifier.clickable { onCancel() }
                        )
                        Text(text = "|", fontSize = 12.sp, color = Color(0xFFD0D7DE))
                    }

                    Text(
                        text = "Book Again",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RailOneBlue,
                        modifier = Modifier.clickable { onBookAgain() }
                    )
                    Text(text = "|", fontSize = 13.sp, color = Color(0xFFD0D7DE))
                    Text(
                        text = "View Details",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RailOneBlue,
                        modifier = Modifier.clickable { onViewDetails() }
                    )
                }
            }
        }
    }
}

@Composable
fun ReservedTicketCard(
    ticket: ReservedTicket,
    onViewDetails: () -> Unit,
    onBookAgain: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TicketCardShape(notchPositionFraction = 0.72f))
            .border(1.dp, Color(0xFFF9A825).copy(alpha = 0.6f), TicketCardShape(notchPositionFraction = 0.72f))
            .clickable { onViewDetails() },
        shape = TicketCardShape(notchPositionFraction = 0.72f),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE0F7FA)
                    ) {
                        Text(
                            text = "Reserved",
                            color = Color(0xFF00838F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = "PNR: ${ticket.pnr}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Train No.\n${ticket.trainNumber} (${ticket.trainName})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = "Journey Date\n${ticket.journeyDate}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        textAlign = TextAlign.End,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ticket.sourceStation.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "— ${ticket.travelDuration} —",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = ticket.destStation.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            DashedDivider(
                color = Color(0xFFF9A825).copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Book Again",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RailOneBlue,
                    modifier = Modifier.clickable { onBookAgain() }
                )
                Spacer(modifier = Modifier.width(28.dp))
                Text(
                    text = "|",
                    fontSize = 13.sp,
                    color = Color(0xFFD0D7DE)
                )
                Spacer(modifier = Modifier.width(28.dp))
                Text(
                    text = "View Details",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = RailOneBlue,
                    modifier = Modifier.clickable { onViewDetails() }
                )
            }
        }
    }
}
