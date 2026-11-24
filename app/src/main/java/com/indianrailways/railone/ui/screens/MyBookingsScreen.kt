package com.indianrailways.railone.ui.screens

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.indianrailways.railone.ui.navigation.Screen
import com.indianrailways.railone.ui.theme.RailOneActiveGreen
import com.indianrailways.railone.ui.theme.RailOneActiveGreenBg
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneOrange
import com.indianrailways.railone.ui.theme.RailOneSurface
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
    var selectedFilterChip by remember { mutableStateOf("All") } // All | Unreserved | Reserved | Platform

    val unreservedTickets by ticketRepository.unreservedTickets.collectAsState()
    val reservedTickets by ticketRepository.reservedTickets.collectAsState()
    val platformTickets by ticketRepository.platformTickets.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val totalUpcomingCount = unreservedTickets.size + reservedTickets.size

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RailOneSurface)
        ) {
            // 1. Blue Top Bar matching screenshot:
            // Back arrow | "My Bookings" | Sort/Filter icon
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

            // 2. Subheader matching screenshot: "Upcoming (4)" in orange + refresh icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedTab.title} ($totalUpcomingCount)",
                    color = RailOneOrange,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Bookings refreshed with IRCTC Sync")
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

            // 3. Filter Chips: All | Unreserved | Reserved
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
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) RailOneBlue else Color.Transparent
                        )
                    )
                }
            }

            // 4. Ticket List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Newly booked unreserved tickets appear at top
                if (selectedFilterChip == "All" || selectedFilterChip == "Unreserved") {
                    items(unreservedTickets) { unreserved ->
                        UnreservedTicketCard(
                            ticket = unreserved,
                            onViewDetails = { onViewUnreservedTicket(unreserved.utsNumber) },
                            onBookAgain = onBookAgain
                        )
                    }
                }

                // Realistic pre-populated reserved tickets from screenshot
                if (selectedFilterChip == "All" || selectedFilterChip == "Reserved") {
                    items(reservedTickets) { reserved ->
                        ReservedTicketCard(
                            ticket = reserved,
                            onViewDetails = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("PNR ${reserved.pnr} confirmed in Coach ${reserved.coach}, Berth ${reserved.berth}")
                                }
                            },
                            onBookAgain = onBookAgain
                        )
                    }
                }
            }

            // 5. Floating / Bottom Pill Filter Tabs matching screenshot:
            // Upcoming | Completed | Cancelled | All
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
            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Top Row: Unreserved Badge | UTS Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RailOneActiveGreenBg
                    ) {
                        Text(
                            text = "● ACTIVE",
                            color = RailOneActiveGreen,
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
                        text = "Class: ${ticket.travelClass} (${ticket.trainType.name})",
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

                // Route: FROM ---------- duration / distance ----------> TO
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

            // Dashed Divider separating notches
            DashedDivider(
                color = Color(0xFFF9A825).copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            // Bottom Action Bar: Book Again | View Details
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

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Book Again",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RailOneBlue,
                        modifier = Modifier.clickable { onBookAgain() }
                    )
                    Text(
                        text = "|",
                        fontSize = 13.sp,
                        color = Color(0xFFD0D7DE)
                    )
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
            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Top Row: "Reserved" cyan badge | PNR in bold
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

                // Train Number & Name | Journey Date
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

                // Station route: JABALPUR ---------- 14h:15m ----------> BHARUCH JN.
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

            // Dashed Divider separating notches
            DashedDivider(
                color = Color(0xFFF9A825).copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            // Bottom Action Bar matching screenshot:
            // Book Again | View Details
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
