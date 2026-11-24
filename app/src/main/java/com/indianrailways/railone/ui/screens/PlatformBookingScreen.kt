package com.indianrailways.railone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.indianrailways.railone.data.model.PlatformTicket
import com.indianrailways.railone.data.model.TicketStatus
import com.indianrailways.railone.data.repository.StationRepository
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneBlueDeep
import com.indianrailways.railone.ui.theme.RailOneOrange
import com.indianrailways.railone.ui.theme.RailOneSurface
import com.indianrailways.railone.ui.theme.TextPrimary
import com.indianrailways.railone.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun PlatformBookingScreen(
    ticketRepository: TicketRepository,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var station by remember {
        mutableStateOf(StationRepository.getStationByCode("BH") ?: StationRepository.majorStations[0])
    }
    var passengerCount by remember { mutableIntStateOf(1) }
    var isPickerOpen by remember { mutableStateOf(false) }
    var isBooking by remember { mutableStateOf(false) }
    var bookedSuccess by remember { mutableStateOf(false) }

    val fare = passengerCount * 10.0

    Box(modifier = Modifier.fillMaxSize().background(RailOneSurface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RailOneBlue)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Platform Ticket Booking",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Station",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF5F7FA))
                                .clickable { isPickerOpen = true }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Train, "Station", tint = RailOneBlue)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(station.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("${station.code} - ${station.state}", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                            Text("Change", color = RailOneBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Passenger Count
                        Text(
                            text = "Number of Passengers",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF5F7FA))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { if (passengerCount > 1) passengerCount-- }) {
                                Icon(Icons.Default.Remove, "minus", tint = RailOneBlue)
                            }
                            Text(
                                text = "$passengerCount Person${if (passengerCount > 1) "s" else ""}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            IconButton(onClick = { if (passengerCount < 6) passengerCount++ }) {
                                Icon(Icons.Default.Add, "add", tint = RailOneBlue)
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF0F7FF)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Validity: 2 Hours", fontSize = 12.sp, color = RailOneBlueDeep, fontWeight = FontWeight.SemiBold)
                                    Text("Platform entry only", fontSize = 11.sp, color = TextSecondary)
                                }
                                Text("₹$fare", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RailOneOrange)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { isBooking = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RailOneBlue)
                        ) {
                            Text("Book Platform Ticket", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        if (isPickerOpen) {
            StationPickerBottomSheet(
                title = "Select Station for Platform Ticket",
                onDismiss = { isPickerOpen = false },
                onStationSelected = {
                    station = it
                    isPickerOpen = false
                }
            )
        }

        if (isBooking) {
            Dialog(onDismissRequest = {}) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = RailOneBlue)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Issuing Platform Ticket...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            LaunchedEffect(Unit) {
                delay(1000)
                ticketRepository.addPlatformTicket(
                    PlatformTicket(
                        id = "PLT" + System.currentTimeMillis().toString().takeLast(6),
                        station = station,
                        passengerCount = passengerCount,
                        fare = fare
                    )
                )
                isBooking = false
                bookedSuccess = true
            }
        }

        if (bookedSuccess) {
            Dialog(onDismissRequest = onSuccess) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, "success", tint = Color(0xFF1E8E3E), modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Platform Ticket Confirmed!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Valid for 2 hours at ${station.name}", fontSize = 13.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = onSuccess,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = RailOneBlue)
                        ) {
                            Text("View in My Bookings")
                        }
                    }
                }
            }
        }
    }
}
