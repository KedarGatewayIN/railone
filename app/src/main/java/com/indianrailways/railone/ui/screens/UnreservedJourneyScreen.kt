package com.indianrailways.railone.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.indianrailways.railone.data.model.TicketStatus
import com.indianrailways.railone.data.model.TicketType
import com.indianrailways.railone.data.model.TrainType
import com.indianrailways.railone.data.model.UnreservedTicket
import com.indianrailways.railone.data.repository.FareCalculator
import com.indianrailways.railone.data.repository.StationRepository
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneBlueDeep
import com.indianrailways.railone.ui.theme.RailOneOrange
import com.indianrailways.railone.ui.theme.TextMuted
import com.indianrailways.railone.ui.theme.TextPrimary
import com.indianrailways.railone.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * Step 2 of Unreserved Booking ("Unreserved Journey" / "E-Ticket")
 * matching the official RailOne video (00:16 - 00:25).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnreservedJourneyScreen(
    fromCode: String,
    toCode: String,
    bookingMode: String,
    locationMode: String,
    ticketRepository: TicketRepository,
    onBack: () -> Unit,
    onTicketBooked: (String) -> Unit
) {
    val sourceStation = remember(fromCode) {
        StationRepository.getStationByCode(fromCode) ?: StationRepository.majorStations[0]
    }
    val destStation = remember(toCode) {
        StationRepository.getStationByCode(toCode) ?: StationRepository.majorStations[1]
    }

    var trainType by remember { mutableStateOf(TrainType.SUPERFAST) }
    var otherTrainLabel by remember { mutableStateOf("OTHERS") }
    var ticketType by remember { mutableStateOf(TicketType.JOURNEY) }
    var adultCount by remember { mutableIntStateOf(1) }
    var childCount by remember { mutableIntStateOf(0) }
    var availConcession by remember { mutableStateOf(false) }

    var showOtherTrainSheet by remember { mutableStateOf(false) }
    var mahamanaAlertTrain by remember { mutableStateOf<String?>(null) }
    var showFareBreakup by remember { mutableStateOf(false) }
    var isGeneratingTicket by remember { mutableStateOf(false) }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FB))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Blue Header: Back circle | "Unreserved Journey" + "E-Ticket"
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
                        text = "Unreserved Journey",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "E-Ticket",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.88f)
                    )
                }
            }

            // 2. White Route Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sourceStation.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = sourceStation.code,
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "To",
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(18.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = destStation.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = destStation.code,
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE5E9F0), thickness = 1.dp)

            // 3. Scrollable Form Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Train Type
                Text(
                    text = "Train Type",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    JourneyPillChip(
                        label = "MAIL/EXP",
                        selected = trainType == TrainType.MAIL_EXPRESS,
                        onClick = {
                            trainType = TrainType.MAIL_EXPRESS
                            otherTrainLabel = "OTHERS"
                        }
                    )
                    JourneyPillChip(
                        label = "SUPERFAST",
                        selected = trainType == TrainType.SUPERFAST,
                        onClick = {
                            trainType = TrainType.SUPERFAST
                            otherTrainLabel = "OTHERS"
                        }
                    )
                    // OTHERS dropdown chip matching video (00:18 - 00:21)
                    Surface(
                        modifier = Modifier.clickable { showOtherTrainSheet = true },
                        shape = RoundedCornerShape(10.dp),
                        color = if (trainType == TrainType.ORDINARY) RailOneBlue else Color.White,
                        border = BorderStroke(
                            1.dp,
                            if (trainType == TrainType.ORDINARY) RailOneBlue else Color(0xFFD5DCE6)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = otherTrainLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (trainType == TrainType.ORDINARY) Color.White else Color(0xFF475569)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "More Train Types",
                                tint = if (trainType == TrainType.ORDINARY) Color.White else Color(0xFF475569),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ticket Type
                Text(
                    text = "Ticket Type",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    JourneyPillChip(
                        label = "JOURNEY",
                        selected = ticketType == TicketType.JOURNEY,
                        onClick = { ticketType = TicketType.JOURNEY }
                    )
                    JourneyPillChip(
                        label = "RETURN",
                        selected = ticketType == TicketType.RETURN,
                        onClick = { ticketType = TicketType.RETURN }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Adult & Child Stepper Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE5E9F0))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        PassengerStepperRow(
                            label = "Adult",
                            count = adultCount,
                            min = 1,
                            max = 4,
                            onDecrement = { if (adultCount > 1) adultCount-- },
                            onIncrement = { if (adultCount < 4) adultCount++ }
                        )
                        HorizontalDivider(color = Color(0xFFEDF1F5), thickness = 1.dp)
                        PassengerStepperRow(
                            label = "Child",
                            count = childCount,
                            min = 0,
                            max = 4,
                            onDecrement = { if (childCount > 0) childCount-- },
                            onIncrement = { if (childCount < 4) childCount++ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Aged between 5 and 12 years on the day of Travel",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Class
                Text(
                    text = "Class",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(8.dp))
                JourneyPillChip(
                    label = "SECOND",
                    selected = true,
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Avail Concession row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { availConcession = !availConcession }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = availConcession,
                        onClick = { availConcession = !availConcession },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = RailOneBlue,
                            unselectedColor = Color(0xFF94A3B8)
                        )
                    )
                    Text(
                        text = "Avail Concession",
                        fontSize = 13.5.sp,
                        color = Color(0xFF334155)
                    )
                }
            }

            // 4. Pinned Bottom Fare & Book Now Bar (matching video 00:16 - 00:25)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ConfirmationNumber,
                                contentDescription = "Fare",
                                tint = RailOneBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Fare",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF334155)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = String.format("₹ %.0f", calculatedFare),
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Surface(
                                modifier = Modifier.clickable { showFareBreakup = true },
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Text(
                                    text = "Fare Breakup",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = buildAnnotatedString {
                            append("By proceeding further you agree to our ")
                            withStyle(SpanStyle(color = RailOneBlue, fontWeight = FontWeight.Medium)) {
                                append("Terms & Conditions")
                            }
                        },
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { isGeneratingTicket = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RailOneBlue)
                    ) {
                        Text(
                            text = "Book Now",
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Train Types Bottom Sheet ("ORDINARY", "MAHAMANA SF", "MAHAMANA EXP") matching video 00:20
        if (showOtherTrainSheet) {
            ModalBottomSheet(
                onDismissRequest = { showOtherTrainSheet = false },
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
                        text = "Train Types",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF133863)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("ORDINARY", "MAHAMANA SF", "MAHAMANA EXP").forEach { itemLabel ->
                            val isSelected = trainType == TrainType.ORDINARY && itemLabel == "ORDINARY"
                            Surface(
                                modifier = Modifier.clickable {
                                    showOtherTrainSheet = false
                                    if (itemLabel == "ORDINARY") {
                                        trainType = TrainType.ORDINARY
                                        otherTrainLabel = "ORDINARY"
                                    } else {
                                        mahamanaAlertTrain = itemLabel
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) RailOneBlue else Color.White,
                                border = BorderStroke(1.dp, if (isSelected) RailOneBlue else Color(0xFFD5DCE6))
                            ) {
                                Text(
                                    text = itemLabel,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Authentic Mahamana Warning Dialog matching video (00:22 - 00:24)
        mahamanaAlertTrain?.let {
            Dialog(onDismissRequest = { mahamanaAlertTrain = null }) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = Color(0xFFE51C23),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "There is no MAHAMANA train running today from ${sourceStation.name} to ${destStation.name}.",
                            fontSize = 14.5.sp,
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { mahamanaAlertTrain = null },
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(42.dp),
                            shape = RoundedCornerShape(21.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RailOneBlue)
                        ) {
                            Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Fare Breakup Dialog
        if (showFareBreakup) {
            val oneWayAdult = FareCalculator.calculateFare(distanceKm, trainType, TicketType.JOURNEY, 1, 0)
            val oneWayChild = if (childCount > 0) FareCalculator.calculateFare(distanceKm, trainType, TicketType.JOURNEY, 0, 1) else 0.0
            AlertDialog(
                onDismissRequest = { showFareBreakup = false },
                title = { Text("Fare Breakup", fontWeight = FontWeight.Bold, color = RailOneBlueDeep) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Route: ${sourceStation.code} → ${destStation.code} ($distanceKm km)", fontSize = 13.sp)
                        Text("Train Type: ${trainType.label}", fontSize = 13.sp)
                        Text("Ticket Type: ${ticketType.label}", fontSize = 13.sp)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text("Adult ($adultCount × ₹${oneWayAdult.toInt()}): ₹${(oneWayAdult * adultCount).toInt()}", fontSize = 13.sp)
                        if (childCount > 0) {
                            Text("Child ($childCount × ₹${oneWayChild.toInt()}): ₹${(oneWayChild * childCount).toInt()}", fontSize = 13.sp)
                        }
                        if (ticketType == TicketType.RETURN) {
                            Text("Return Multiplier: × 2", fontSize = 13.sp)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "Total Payable: ₹${calculatedFare.toInt()}.00",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = RailOneOrange
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showFareBreakup = false },
                        colors = ButtonDefaults.buttonColors(containerColor = RailOneBlue)
                    ) {
                        Text("Close")
                    }
                }
            )
        }

        // Instant 1s Payment Bypass & Ticket Generation Dialog
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
                            modifier = Modifier.size(46.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Generating Ticket...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Issuing Paperless UTS Ticket",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            LaunchedEffect(Unit) {
                delay(1000)
                val newUts = ticketRepository.generateRealisticUtsNumber()
                val newTicket = UnreservedTicket(
                    utsNumber = newUts,
                    sourceStation = sourceStation,
                    destStation = destStation,
                    via = "------",
                    ticketType = ticketType,
                    trainType = trainType,
                    travelClass = "SECOND",
                    adultCount = adultCount,
                    childCount = childCount,
                    distanceKm = distanceKm,
                    fare = calculatedFare,
                    bookingTimestamp = System.currentTimeMillis(),
                    status = TicketStatus.ACTIVE,
                    bookingMode = bookingMode,
                    locationMode = locationMode
                )
                ticketRepository.addUnreservedTicket(newTicket)
                isGeneratingTicket = false
                onTicketBooked(newUts)
            }
        }
    }
}

@Composable
private fun JourneyPillChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (selected) RailOneBlue else Color.White,
        border = BorderStroke(1.dp, if (selected) RailOneBlue else Color(0xFFD5DCE6))
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else Color(0xFF475569),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun PassengerStepperRow(
    label: String,
    count: Int,
    min: Int,
    max: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF334155)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .border(
                        1.2.dp,
                        if (count > min) RailOneBlue else Color(0xFFCBD5E1),
                        CircleShape
                    )
                    .clickable(enabled = count > min) { onDecrement() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease $label",
                    tint = if (count > min) RailOneBlue else Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = count.toString(),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .border(
                        1.2.dp,
                        if (count < max) RailOneBlue else Color(0xFFCBD5E1),
                        CircleShape
                    )
                    .clickable(enabled = count < max) { onIncrement() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase $label",
                    tint = if (count < max) RailOneBlue else Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
