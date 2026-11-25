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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.indianrailways.railone.data.model.Station
import com.indianrailways.railone.data.repository.MockRailwayService
import com.indianrailways.railone.data.repository.StationRepository
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.theme.RailOneActiveGreen
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneBlueDeep
import com.indianrailways.railone.ui.theme.RailOneSurface
import com.indianrailways.railone.ui.theme.TextPrimary
import com.indianrailways.railone.ui.theme.TextSecondary

/**
 * Step 1 of Unreserved Ticket Booking ("Unreserved E-Ticket") matching official RailOne video (00:12 - 00:15).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnreservedBookingScreen(
    ticketRepository: TicketRepository,
    onBack: () -> Unit,
    onProceedToJourney: (fromCode: String, toCode: String, bookingMode: String, locationMode: String) -> Unit
) {
    var selectedBookingMode by remember { mutableStateOf("Normal") }
    var selectedLocationMode by remember { mutableStateOf("Outside Station") }

    var sourceStation by remember {
        mutableStateOf(StationRepository.getStationByCode("BH") ?: StationRepository.majorStations[0])
    }
    var destStation by remember {
        mutableStateOf(StationRepository.getStationByCode("BRC") ?: StationRepository.majorStations[1])
    }

    var isPickingSource by remember { mutableStateOf(false) }
    var isPickingDest by remember { mutableStateOf(false) }
    var showUpcomingTrainsSheet by remember { mutableStateOf(false) }
    var infoDialogMessage by remember { mutableStateOf<Pair<String, String>?>(null) }

    val recentSearches by ticketRepository.recentSearches.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FB))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            // 1. Header: Centered "Unreserved E-Ticket" + circular X close button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(36.dp))

                    Text(
                        text = "Unreserved E-Ticket",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF133863)
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 2. Main White Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
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
                                .background(Color(0xFFF1F4F9))
                                .padding(4.dp)
                        ) {
                            listOf("Normal", "Season").forEach { mode ->
                                val selected = selectedBookingMode == mode
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(if (selected) Color.White else Color.Transparent)
                                        .clickable { selectedBookingMode = mode },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = mode,
                                        fontSize = 14.sp,
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (selected) RailOneBlue else TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // B. Location Mode Pill Toggle: Outside Station (i) | At Station (i)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf("Outside Station", "At Station").forEach { locMode ->
                                val selected = selectedLocationMode == locMode
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .clip(RoundedCornerShape(21.dp))
                                        .background(if (selected) RailOneBlue else Color.White)
                                        .border(
                                            width = 1.dp,
                                            color = if (selected) RailOneBlue else Color(0xFFD5DCE6),
                                            shape = RoundedCornerShape(21.dp)
                                        )
                                        .clickable { selectedLocationMode = locMode }
                                        .padding(horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = locMode,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (selected) Color.White else Color(0xFF334155)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Info",
                                            tint = if (selected) Color.White else Color(0xFF64748B),
                                            modifier = Modifier
                                                .size(15.dp)
                                                .clickable {
                                                    infoDialogMessage = if (locMode == "Outside Station") {
                                                        "Outside Station (Paperless)" to "Book a paperless mobile ticket from anywhere outside the railway station premises. Your ticket stays stored offline on this device."
                                                    } else {
                                                        "At Station Booking" to "Scan the station UTS QR code or book directly inside station premises for immediate departure."
                                                    }
                                                }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // C. From / To Station Selector with Right-side Swap Circle
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 52.dp)
                            ) {
                                // From
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
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Train,
                                            contentDescription = "From",
                                            tint = Color(0xFF64748B),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${sourceStation.code} - ${sourceStation.name}",
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1E293B)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color(0xFFE5E9F0), thickness = 1.dp)
                                Spacer(modifier = Modifier.height(10.dp))

                                // To
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
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Train,
                                            contentDescription = "To",
                                            tint = Color(0xFF64748B),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${destStation.code} - ${destStation.name}",
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1E293B)
                                        )
                                    }
                                }
                            }

                            // Swap Circle Button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEBF3FF))
                                    .border(1.dp, Color(0xFFD0E2FF), CircleShape)
                                    .clickable {
                                        val tmp = sourceStation
                                        sourceStation = destStation
                                        destStation = tmp
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = "Swap",
                                    tint = RailOneBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        // D. Proceed To Book Button (Solid Blue)
                        Button(
                            onClick = {
                                if (sourceStation.code == destStation.code) {
                                    infoDialogMessage = "Invalid Route" to "Source and Destination stations cannot be the same."
                                } else {
                                    onProceedToJourney(
                                        sourceStation.code,
                                        destStation.code,
                                        selectedBookingMode,
                                        selectedLocationMode
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RailOneBlue)
                        ) {
                            Text(
                                text = "Proceed To Book",
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // E. Check Upcoming Trains Button (Outlined Blue)
                        OutlinedButton(
                            onClick = { showUpcomingTrainsSheet = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, RailOneBlue),
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

            // 3. "Recent Searches" Section matching video (00:12 - 00:14)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Recent Searches",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF133863)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recentSearches) { (srcCode, dstCode) ->
                            val src = StationRepository.getStationByCode(srcCode)
                            val dst = StationRepository.getStationByCode(dstCode)
                            if (src != null && dst != null) {
                                Box(
                                    modifier = Modifier
                                        .width(170.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFE8EEF8))
                                        .clickable {
                                            sourceStation = src
                                            destStation = dst
                                        }
                                        .padding(10.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.White.copy(alpha = 0.72f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "${src.name}, ${src.code}",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF1E293B),
                                                textAlign = TextAlign.Center,
                                                maxLines = 1,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .padding(vertical = 4.dp)
                                                .size(26.dp)
                                                .clip(CircleShape)
                                                .background(Color.White),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.SwapVert,
                                                contentDescription = "Route",
                                                tint = RailOneBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.White.copy(alpha = 0.72f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "${dst.name}, ${dst.code}",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF1E293B),
                                                textAlign = TextAlign.Center,
                                                maxLines = 1,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Station Picker Sheet
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

        // Upcoming Trains Bottom Sheet
        if (showUpcomingTrainsSheet) {
            val trains = remember(sourceStation, destStation) {
                MockRailwayService.searchTrainsBetweenStations(sourceStation, destStation)
            }
            ModalBottomSheet(
                onDismissRequest = { showUpcomingTrainsSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 28.dp)
                ) {
                    Text(
                        text = "Upcoming Trains (${sourceStation.code} → ${destStation.code})",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = RailOneBlueDeep
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    trains.forEach { train ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clickable {
                                    showUpcomingTrainsSheet = false
                                    onProceedToJourney(
                                        sourceStation.code,
                                        destStation.code,
                                        selectedBookingMode,
                                        selectedLocationMode
                                    )
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = RailOneSurface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${train.trainNumber} ${train.trainName}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${train.departureTime} → ${train.arrivalTime} (${train.duration})",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = RailOneActiveGreen.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = train.trainType,
                                        color = RailOneActiveGreen,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Info Dialog
        infoDialogMessage?.let { (title, message) ->
            AlertDialog(
                onDismissRequest = { infoDialogMessage = null },
                title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp) },
                text = { Text(message, fontSize = 13.5.sp, color = TextSecondary) },
                confirmButton = {
                    TextButton(onClick = { infoDialogMessage = null }) {
                        Text("OK", color = RailOneBlue, fontWeight = FontWeight.Bold)
                    }
                }
            )
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
                .padding(bottom = 28.dp)
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
                placeholder = { Text("Search by name, code (e.g. BH, BRC, ADI)") },
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
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                }
            }
        }
    }
}
