package com.indianrailways.railone.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.indianrailways.railone.data.model.ReservedTicket
import com.indianrailways.railone.data.model.TicketStatus
import com.indianrailways.railone.data.repository.MockRailwayService
import com.indianrailways.railone.data.repository.SearchTrainItem
import com.indianrailways.railone.data.repository.StationRepository
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.theme.RailOneActiveGreen
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneBlueDeep
import com.indianrailways.railone.ui.theme.RailOneOrange
import com.indianrailways.railone.ui.theme.RailOneSurface
import com.indianrailways.railone.ui.theme.TextPrimary
import com.indianrailways.railone.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * Reserved Train Search & Booking Screen so tapping "Reserved" on Home books real Reserved tickets
 * stored in SQLite (no pre-populated fake reserved tickets in My Bookings).
 */
@Composable
fun ReservedBookingScreen(
    ticketRepository: TicketRepository,
    onBack: () -> Unit,
    onBooked: () -> Unit
) {
    var sourceStation by remember {
        mutableStateOf(StationRepository.getStationByCode("BH") ?: StationRepository.majorStations[0])
    }
    var destStation by remember {
        mutableStateOf(StationRepository.getStationByCode("BRC") ?: StationRepository.majorStations[1])
    }
    var isPickingSource by remember { mutableStateOf(false) }
    var isPickingDest by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("Wed, 26 Nov 26") }
    var pendingBooking by remember { mutableStateOf<Pair<SearchTrainItem, Pair<String, Double>>?>(null) }

    val trains = remember(sourceStation, destStation) {
        MockRailwayService.searchTrainsBetweenStations(sourceStation, destStation)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RailOneSurface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RailOneBlue)
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                Column {
                    Text(
                        text = "Reserved E-Ticket",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "IRCTC Train Search & Booking",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Route Selector Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { isPickingSource = true }
                                ) {
                                    Text("From", fontSize = 11.sp, color = TextSecondary)
                                    Text(sourceStation.code, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = RailOneBlueDeep)
                                    Text(sourceStation.name, fontSize = 11.5.sp, color = TextSecondary)
                                }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEEF4FF))
                                        .clickable {
                                            val tmp = sourceStation
                                            sourceStation = destStation
                                            destStation = tmp
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.SwapVert, contentDescription = "Swap", tint = RailOneBlue)
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { isPickingDest = true },
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text("To", fontSize = 11.sp, color = TextSecondary)
                                    Text(destStation.code, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = RailOneBlueDeep)
                                    Text(destStation.name, fontSize = 11.5.sp, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFEDF1F5))
                            Spacer(modifier = Modifier.height(10.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val dates = listOf("Wed, 26 Nov 26", "Thu, 27 Nov 26", "Fri, 28 Nov 26", "Sat, 29 Nov 26")
                                items(dates) { date ->
                                    val selected = selectedDate == date
                                    Surface(
                                        modifier = Modifier.clickable { selectedDate = date },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (selected) RailOneBlue else Color(0xFFF5F7FA),
                                        border = BorderStroke(1.dp, if (selected) RailOneBlue else Color(0xFFE2E8F0))
                                    ) {
                                        Text(
                                            text = date,
                                            fontSize = 12.sp,
                                            color = if (selected) Color.White else TextPrimary,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Available Trains (${trains.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RailOneBlueDeep
                    )
                }

                items(trains) { train ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Train, contentDescription = null, tint = RailOneBlue, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${train.trainNumber} • ${train.trainName}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = train.runDays,
                                    fontSize = 11.sp,
                                    color = RailOneActiveGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(train.departureTime, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(sourceStation.code, fontSize = 11.sp, color = TextSecondary)
                                }
                                Text("— ${train.duration} —", fontSize = 11.5.sp, color = TextSecondary)
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(train.arrivalTime, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(destStation.code, fontSize = 11.sp, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(train.classes) { classFare ->
                                    Surface(
                                        modifier = Modifier.clickable {
                                            pendingBooking = train to classFare
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFF0F7FF),
                                        border = BorderStroke(1.dp, RailOneBlue.copy(alpha = 0.45f))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(classFare.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RailOneBlueDeep)
                                                Text("₹${classFare.second.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RailOneOrange)
                                            }
                                            Text(train.availability, fontSize = 10.sp, color = RailOneActiveGreen, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isPickingSource || isPickingDest) {
            StationPickerBottomSheet(
                title = if (isPickingSource) "Select Source Station" else "Select Destination Station",
                onDismiss = {
                    isPickingSource = false
                    isPickingDest = false
                },
                onStationSelected = { station ->
                    if (isPickingSource) sourceStation = station else destStation = station
                    isPickingSource = false
                    isPickingDest = false
                }
            )
        }

        pendingBooking?.let { (train, classFare) ->
            Dialog(onDismissRequest = {}) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(26.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = RailOneBlue)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Confirming IRCTC Booking...", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${train.trainNumber} • ${classFare.first} • ₹${classFare.second.toInt()}", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }

            LaunchedEffect(Unit) {
                delay(1000)
                val pnr = ticketRepository.generateRealisticPnr()
                ticketRepository.addReservedTicket(
                    ReservedTicket(
                        pnr = pnr,
                        trainNumber = train.trainNumber,
                        trainName = train.trainName,
                        sourceStation = sourceStation,
                        destStation = destStation,
                        journeyDate = selectedDate,
                        travelDuration = train.duration,
                        departureTime = train.departureTime,
                        arrivalTime = train.arrivalTime,
                        coach = if (classFare.first == "2S") "D2" else if (classFare.first == "CC") "C4" else "B3",
                        berth = "32 (WS)",
                        travelClass = classFare.first,
                        status = TicketStatus.ACTIVE,
                        fare = classFare.second
                    )
                )
                pendingBooking = null
                onBooked()
            }
        }
    }
}
