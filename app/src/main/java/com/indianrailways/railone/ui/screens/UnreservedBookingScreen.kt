package com.indianrailways.railone.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.indianrailways.railone.data.model.Station
import com.indianrailways.railone.data.model.TicketStatus
import com.indianrailways.railone.data.model.TicketType
import com.indianrailways.railone.data.model.TrainType
import com.indianrailways.railone.data.model.UnreservedTicket
import com.indianrailways.railone.data.repository.FareCalculator
import com.indianrailways.railone.data.repository.StationRepository
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.navigation.Screen
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneBlueDeep
import com.indianrailways.railone.ui.theme.RailOneOrange
import com.indianrailways.railone.ui.theme.RailOneSurface
import com.indianrailways.railone.ui.theme.TextMuted
import com.indianrailways.railone.ui.theme.TextPrimary
import com.indianrailways.railone.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnreservedBookingScreen(
    ticketRepository: TicketRepository,
    onBack: () -> Unit,
    onTicketBooked: (String) -> Unit
) {
    var selectedBookingMode by remember { mutableStateOf("Normal") } // Normal | Season
    var selectedLocationMode by remember { mutableStateOf("Outside Station") } // Outside Station | At Station

    // Stations
    var sourceStation by remember {
        mutableStateOf(StationRepository.getStationByCode("BH") ?: StationRepository.majorStations[0])
    }
    var destStation by remember {
        mutableStateOf(StationRepository.getStationByCode("BRC") ?: StationRepository.majorStations[1])
    }

    // Journey details
    var ticketType by remember { mutableStateOf(TicketType.JOURNEY) }
    var trainType by remember { mutableStateOf(TrainType.SUPERFAST) }
    var adultCount by remember { mutableIntStateOf(1) }
    var childCount by remember { mutableIntStateOf(0) }

    // Station Picker Sheet State
    var isPickingSource by remember { mutableStateOf(false) }
    var isPickingDest by remember { mutableStateOf(false) }

    // Payment Bypass / Generating Ticket State
    var isGeneratingTicket by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Dynamic Fare Calculation
    val distanceKm = remember(sourceStation, destStation) {
        StationRepository.getDistanceKm(sourceStation.code, destStation.code)
    }

    val calculatedFare = remember(distanceKm, trainType, ticketType, adultCount, childCount) {
        FareCalculator.calculateFare(
            distanceKm = distanceKm,
            trainType = trainType,
            ticketType = ticketType,
            adults = adultCount,
            children = childCount
        )
    }

    val recentSearches = remember {
        listOf(
            Pair("BH", "BRC"),
            Pair("BRC", "BH"),
            Pair("ST", "BH"),
            Pair("ADI", "BRC")
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(RailOneSurface),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Header: "Unreserved E-Ticket" with close button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(36.dp))

                    Text(
                        text = "Unreserved E-Ticket",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RailOneBlueDeep
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = RailOneBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 2. Main Booking Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // A. Segmented Toggle: Normal | Season
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(Color(0xFFF1F3F6))
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selectedBookingMode == "Normal") Color.White else Color.Transparent)
                                    .clickable { selectedBookingMode = "Normal" },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Normal",
                                    fontSize = 14.sp,
                                    fontWeight = if (selectedBookingMode == "Normal") FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedBookingMode == "Normal") RailOneBlue else TextSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selectedBookingMode == "Season") Color.White else Color.Transparent)
                                    .clickable { selectedBookingMode = "Season" },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Season",
                                    fontSize = 14.sp,
                                    fontWeight = if (selectedBookingMode == "Season") FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedBookingMode == "Season") RailOneBlue else TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // B. Pill Toggle: Outside Station (i) | At Station (i)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(21.dp))
                                    .background(if (selectedLocationMode == "Outside Station") RailOneBlue else Color.White)
                                    .border(
                                        1.dp,
                                        if (selectedLocationMode == "Outside Station") RailOneBlue else Color(0xFFD0D7DE),
                                        RoundedCornerShape(21.dp)
                                    )
                                    .clickable { selectedLocationMode = "Outside Station" }
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Outside Station",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (selectedLocationMode == "Outside Station") Color.White else TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "info",
                                        tint = if (selectedLocationMode == "Outside Station") Color.White else TextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(21.dp))
                                    .background(if (selectedLocationMode == "At Station") RailOneBlue else Color.White)
                                    .border(
                                        1.dp,
                                        if (selectedLocationMode == "At Station") RailOneBlue else Color(0xFFD0D7DE),
                                        RoundedCornerShape(21.dp)
                                    )
                                    .clickable { selectedLocationMode = "At Station" }
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "At Station",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (selectedLocationMode == "At Station") Color.White else TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "info",
                                        tint = if (selectedLocationMode == "At Station") Color.White else TextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // C. Interactive Station Selector with Swap Button (⇄)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 48.dp)
                            ) {
                                // FROM STATION
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { isPickingSource = true }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "From",
                                        fontSize = 12.sp,
                                        color = RailOneBlue,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Train,
                                            contentDescription = "From Train",
                                            tint = RailOneBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = sourceStation.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${sourceStation.code} - ${sourceStation.state}",
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color(0xFFE5E9F0))
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                // TO STATION
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { isPickingDest = true }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "To",
                                        fontSize = 12.sp,
                                        color = RailOneBlue,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Train,
                                            contentDescription = "To Train",
                                            tint = RailOneOrange,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = destStation.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${destStation.code} - ${destStation.state}",
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            // Circular Swap Button on Right
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEBF3FF))
                                    .border(1.dp, Color(0xFFC7DEFF), CircleShape)
                                    .clickable {
                                        val temp = sourceStation
                                        sourceStation = destStation
                                        destStation = temp
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = "Swap Stations",
                                    tint = RailOneBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // D. Journey Options (Ticket Type & Train Type)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Ticket Type (Journey vs Return)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Ticket Type",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF1F3F6))
                                        .padding(2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (ticketType == TicketType.JOURNEY) RailOneBlue else Color.Transparent)
                                            .clickable { ticketType = TicketType.JOURNEY }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Single",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (ticketType == TicketType.JOURNEY) Color.White else TextPrimary
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (ticketType == TicketType.RETURN) RailOneBlue else Color.Transparent)
                                            .clickable { ticketType = TicketType.RETURN }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Return",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (ticketType == TicketType.RETURN) Color.White else TextPrimary
                                        )
                                    }
                                }
                            }

                            // Class: Second Class (II)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Class",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF1F3F6))
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "SECOND (II)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RailOneBlueDeep
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Train Type: Ordinary / Mail-Exp / Superfast
                        Text(
                            text = "Train Type",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TrainType.values().forEach { type ->
                                val isSelected = trainType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) RailOneBlue.copy(alpha = 0.12f) else Color(0xFFF5F7FA))
                                        .border(
                                            1.dp,
                                            if (isSelected) RailOneBlue else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { trainType = type }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) RailOneBlue else TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Passenger Counters: Adult & Child Steppers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Adults
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Adults (12+ yrs)",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF5F7FA))
                                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { if (adultCount > 1) adultCount-- },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, "minus", modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        text = "$adultCount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                    IconButton(
                                        onClick = { if (adultCount < 4) adultCount++ },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Add, "add", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            // Children
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Children (5-11 yrs)",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF5F7FA))
                                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { if (childCount > 0) childCount-- },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, "minus", modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        text = "$childCount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                    IconButton(
                                        onClick = { if (childCount < 4) childCount++ },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Add, "add", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dynamic Fare Chip & Distance Summary
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF0F7FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCCE4FF))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Estimated Total Fare",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "$distanceKm km • ${ticketType.label}",
                                        fontSize = 11.sp,
                                        color = RailOneBlue,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = String.format("₹%.2f", calculatedFare),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RailOneOrange
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action 1: "Proceed To Book" (Solid Blue Button)
                        Button(
                            onClick = {
                                // Trigger instant 1s payment bypass flow
                                isGeneratingTicket = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RailOneBlue)
                        ) {
                            Text(
                                text = "Proceed To Book",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action 2: "Check Upcoming Trains" (Outlined Blue Button)
                        OutlinedButton(
                            onClick = {
                                onBack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RailOneBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RailOneBlue)
                        ) {
                            Text(
                                text = "Check Upcoming Trains",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 3. "Recent Searches" Section matching screenshot
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Recent Searches",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = RailOneBlueDeep
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(recentSearches) { (src, dst) ->
                            val srcStation = StationRepository.getStationByCode(src)
                            val dstStation = StationRepository.getStationByCode(dst)

                            if (srcStation != null && dstStation != null) {
                                Card(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            sourceStation = srcStation
                                            destStation = dstStation
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F1FD))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${srcStation.name}, ${srcStation.code}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = RailOneBlueDeep
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.SwapVert,
                                            contentDescription = "swap",
                                            tint = RailOneBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${dstStation.name}, ${dstStation.code}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = RailOneBlueDeep
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Station Picker Modal Bottom Sheet
        if (isPickingSource || isPickingDest) {
            StationPickerBottomSheet(
                title = if (isPickingSource) "Select Source Station" else "Select Destination Station",
                onDismiss = {
                    isPickingSource = false
                    isPickingDest = false
                },
                onStationSelected = { station ->
                    if (isPickingSource) {
                        sourceStation = station
                    } else {
                        destStation = station
                    }
                    isPickingSource = false
                    isPickingDest = false
                }
            )
        }

        // Generating Ticket Dialog (1s Loading Bypass)
        if (isGeneratingTicket) {
            Dialog(onDismissRequest = {}) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(28.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = RailOneBlue,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Generating Ticket...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Connecting to Indian Railways UTS Server",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            LaunchedEffect(Unit) {
                // Realistic 1-second seamless ticket generation
                delay(1000)

                val newUts = ticketRepository.generateRealisticUtsNumber()
                val newTicket = UnreservedTicket(
                    utsNumber = newUts,
                    sourceStation = sourceStation,
                    destStation = destStation,
                    via = "--------",
                    ticketType = ticketType,
                    trainType = trainType,
                    travelClass = "SECOND",
                    adultCount = adultCount,
                    childCount = childCount,
                    distanceKm = distanceKm,
                    fare = calculatedFare,
                    bookingTimestamp = System.currentTimeMillis(),
                    status = TicketStatus.ACTIVE
                )

                ticketRepository.addUnreservedTicket(newTicket)
                isGeneratingTicket = false
                onTicketBooked(newUts)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationPickerBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    onStationSelected: (Station) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    val filteredStations = remember(searchQuery) {
        StationRepository.searchStations(searchQuery)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = RailOneBlueDeep
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by name, code (e.g. ADI, BRC, NDLS)") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = RailOneBlue)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RailOneBlue,
                    unfocusedBorderColor = Color(0xFFD0D7DE)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                items(filteredStations) { station ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStationSelected(station) }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = station.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${station.state} • ${station.zone} Railway",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE8F1FD)
                        ) {
                            Text(
                                text = station.code,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RailOneBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(Color(0xFFEEEEEE))
                    )
                }
            }
        }
    }
}
