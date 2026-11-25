package com.indianrailways.railone.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianrailways.railone.data.model.PlatformTicket
import com.indianrailways.railone.data.model.ReservedTicket
import com.indianrailways.railone.data.model.TicketStatus
import com.indianrailways.railone.data.model.UnreservedTicket
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.components.DashedDivider
import com.indianrailways.railone.ui.components.TicketCardShape
import com.indianrailways.railone.ui.theme.RailOneActiveGreen
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

@Composable
fun MyBookingsScreen(
    ticketRepository: TicketRepository,
    onBack: () -> Unit,
    onViewUnreservedTicket: (String) -> Unit,
    onBookAgainUnreserved: () -> Unit,
    onBookAgainReserved: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(BookingTab.UPCOMING) }
    var selectedFilterChip by remember { mutableStateOf("All") }
    var showFilterRow by remember { mutableStateOf(false) }

    var ticketToCancel by remember { mutableStateOf<UnreservedTicket?>(null) }
    var selectedReservedTicket by remember { mutableStateOf<ReservedTicket?>(null) }

    val unreservedTickets by ticketRepository.unreservedTickets.collectAsState()
    val reservedTickets by ticketRepository.reservedTickets.collectAsState()
    val platformTickets by ticketRepository.platformTickets.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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

    val visibleUnreserved = if (selectedFilterChip == "All" || selectedFilterChip == "Unreserved") tabFilteredUnreserved else emptyList()
    val visibleReserved = if (selectedFilterChip == "All" || selectedFilterChip == "Reserved") tabFilteredReserved else emptyList()
    val visiblePlatform = if (selectedFilterChip == "All" || selectedFilterChip == "Platform") tabFilteredPlatform else emptyList()

    val currentTabCount = visibleUnreserved.size + visibleReserved.size + visiblePlatform.size
    val isEmpty = currentTabCount == 0

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFBFC))
        ) {
            // Top Blue Header matching official video (00:29 - 00:32)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RailOneBlue)
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.22f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
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

                IconButton(onClick = { showFilterRow = !showFilterRow }) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Filter Bookings",
                        tint = Color.White
                    )
                }
            }

            // Subheader: "Upcoming (N)" in Orange + Refresh icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedTab.title} ($currentTabCount)",
                    color = RailOneOrange,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        ticketRepository.refreshFromDatabase()
                        scope.launch {
                            snackbarHostState.showSnackbar("Bookings refreshed from local SQLite store")
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color(0xFF334155),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Optional Filter Chips (toggled by top-right filter icon)
            AnimatedVisibility(visible = showFilterRow) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("All", "Unreserved", "Reserved", "Platform")) { chip ->
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
            }

            // Ticket List or Empty State
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
                                text = "Tickets you book in RailOne are saved in SQLite and appear here immediately.",
                                fontSize = 12.5.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = onBookAgainUnreserved,
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RailOneBlue)
                            ) {
                                Text("Book Unreserved Ticket", fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(visibleReserved, key = { "res_${it.pnr}" }) { reserved ->
                        ReservedTicketCard(
                            ticket = reserved,
                            onViewDetails = { selectedReservedTicket = reserved },
                            onBookAgain = onBookAgainReserved
                        )
                    }

                    items(visibleUnreserved, key = { "uts_${it.utsNumber}" }) { unreserved ->
                        UnreservedTicketCard(
                            ticket = unreserved,
                            onViewDetails = { onViewUnreservedTicket(unreserved.utsNumber) },
                            onBookAgain = onBookAgainUnreserved,
                            onCancel = { ticketToCancel = unreserved }
                        )
                    }

                    items(visiblePlatform, key = { "plt_${it.id}" }) { platform ->
                        PlatformTicketCard(
                            ticket = platform,
                            onBookAgain = onBookAgainUnreserved
                        )
                    }
                }
            }

            // Bottom Floating Pill Tabs: Upcoming | Completed | Cancelled | All
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(30.dp),
                color = Color(0xFFEAF2FC),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BookingTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        val tabColor = if (isSelected) RailOneOrange else Color(0xFF475569)

                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 12.dp, vertical = 4.dp),
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
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = tabColor
                            )
                        }
                    }
                }
            }
        }

        // Unreserved Cancel Dialog
        ticketToCancel?.let { ticket ->
            AlertDialog(
                onDismissRequest = { ticketToCancel = null },
                title = { Text("Cancel Ticket?", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = {
                    Column {
                        Text(
                            text = "Cancel UTS Ticket ${ticket.utsNumber} (${ticket.sourceStation.code} → ${ticket.destStation.code})?",
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Fare ${ticket.formattedFare} will be refunded to your R-Wallet.",
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
                                snackbarHostState.showSnackbar("Ticket ${ticket.utsNumber} moved to Cancelled.")
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

        // Reserved Ticket Detail Dialog
        selectedReservedTicket?.let { res ->
            AlertDialog(
                onDismissRequest = { selectedReservedTicket = null },
                title = { Text("PNR: ${res.pnr}", fontWeight = FontWeight.Bold, color = RailOneBlue) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${res.trainNumber} (${res.trainName})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${res.sourceStation.name} → ${res.destStation.name}", fontSize = 13.sp)
                        Text("Journey Date: ${res.journeyDate}", fontSize = 13.sp, color = TextSecondary)
                        Text("Departure: ${res.departureTime} | Arrival: ${res.arrivalTime}", fontSize = 12.5.sp, color = TextSecondary)
                        Text("Coach / Berth: ${res.coach} / ${res.berth} (${res.travelClass})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = RailOneActiveGreen)
                        Text("Status: ${res.status.label}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Fare: ${res.formattedFare}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RailOneOrange)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { selectedReservedTicket = null },
                        colors = ButtonDefaults.buttonColors(containerColor = RailOneBlue)
                    ) {
                        Text("Close")
                    }
                },
                dismissButton = {
                    if (res.status == TicketStatus.ACTIVE) {
                        TextButton(onClick = {
                            ticketRepository.cancelReservedTicket(res.pnr)
                            selectedReservedTicket = null
                            scope.launch {
                                snackbarHostState.showSnackbar("Reserved PNR ${res.pnr} cancelled.")
                            }
                        }) {
                            Text("Cancel PNR", color = RailOneWarningRed)
                        }
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
    onCancel: () -> Unit
) {
    val isCancelled = ticket.status == TicketStatus.CANCELLED
    var menuExpanded by remember { mutableStateOf(false) }
    val cardShape = TicketCardShape(notchPositionFraction = 0.72f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .border(
                1.dp,
                if (isCancelled) Color(0xFFE0E0E0) else Color(0xFFF6A623).copy(alpha = 0.55f),
                cardShape
            )
            .clickable { onViewDetails() },
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Top Row: "Unreserved" cyan pill | "UTS: X509EIA0BF" + menu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCancelled) RailOneWarningRedBg else Color(0xFFE0F7FA)
                    ) {
                        Text(
                            text = if (isCancelled) "Cancelled" else "Unreserved",
                            color = if (isCancelled) RailOneWarningRed else Color(0xFF00838F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "UTS: ${ticket.utsNumber}",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        if (ticket.status == TicketStatus.ACTIVE) {
                            Box {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = TextMuted,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { menuExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Cancel Ticket", color = RailOneWarningRed) },
                                        onClick = {
                                            menuExpanded = false
                                            onCancel()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stacked Ticket Type | Booking Date row matching Video (00:31 - 00:32)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Ticket Type", fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = ticket.ticketType.label,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Booking Date", fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = ticket.formattedBookingDateShort,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Route Row: Source — 71 km — Destination
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ticket.sourceStation.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextPrimary
                    )
                    Text(
                        text = "— ${ticket.distanceKm} km —",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = ticket.destStation.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextPrimary
                    )
                }
            }

            DashedDivider(
                color = Color(0xFFF6A623).copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            // Centered Footer Actions matching Video (00:32): Book Again | View Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Book Again",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RailOneBlue,
                    modifier = Modifier.clickable { onBookAgain() }
                )
                Spacer(modifier = Modifier.width(34.dp))
                Text(text = "|", fontSize = 14.sp, color = Color(0xFFD0D7DE))
                Spacer(modifier = Modifier.width(34.dp))
                Text(
                    text = "View Details",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RailOneBlue,
                    modifier = Modifier.clickable { onViewDetails() }
                )
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
    val isCancelled = ticket.status == TicketStatus.CANCELLED
    val cardShape = TicketCardShape(notchPositionFraction = 0.72f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .border(
                1.dp,
                if (isCancelled) Color(0xFFE0E0E0) else Color(0xFFF6A623).copy(alpha = 0.55f),
                cardShape
            )
            .clickable { onViewDetails() },
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCancelled) RailOneWarningRedBg else Color(0xFFE0F7FA)
                    ) {
                        Text(
                            text = if (isCancelled) "Cancelled" else "Reserved",
                            color = if (isCancelled) RailOneWarningRed else Color(0xFF00838F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "PNR: ${ticket.pnr}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Train No.", fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = "${ticket.trainNumber} (${ticket.trainName})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Journey Date", fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = ticket.journeyDate,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(ticket.sourceStation.name, fontSize = 13.sp, color = TextPrimary)
                    Text("— ${ticket.travelDuration} —", fontSize = 11.sp, color = TextMuted)
                    Text(ticket.destStation.name, fontSize = 13.sp, color = TextPrimary)
                }
            }

            DashedDivider(
                color = Color(0xFFF6A623).copy(alpha = 0.5f),
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
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RailOneBlue,
                    modifier = Modifier.clickable { onBookAgain() }
                )
                Spacer(modifier = Modifier.width(34.dp))
                Text("|", fontSize = 14.sp, color = Color(0xFFD0D7DE))
                Spacer(modifier = Modifier.width(34.dp))
                Text(
                    text = "View Details",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RailOneBlue,
                    modifier = Modifier.clickable { onViewDetails() }
                )
            }
        }
    }
}

@Composable
fun PlatformTicketCard(
    ticket: PlatformTicket,
    onBookAgain: () -> Unit
) {
    val cardShape = TicketCardShape(notchPositionFraction = 0.72f)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .border(1.dp, Color(0xFFF6A623).copy(alpha = 0.55f), cardShape),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE0F7FA)
                    ) {
                        Text(
                            text = "Platform",
                            color = Color(0xFF00838F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "ID: ${ticket.id}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = ticket.station.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${ticket.passengerCount} Person(s) • Valid 2 hrs • ${ticket.formattedFare}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            DashedDivider(
                color = Color(0xFFF6A623).copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Book Again",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RailOneBlue,
                    modifier = Modifier.clickable { onBookAgain() }
                )
            }
        }
    }
}
