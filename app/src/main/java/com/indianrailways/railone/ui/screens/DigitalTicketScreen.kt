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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.components.DashedDivider
import com.indianrailways.railone.ui.components.QrCodeView
import com.indianrailways.railone.ui.theme.RailOneActiveGreen
import com.indianrailways.railone.ui.theme.RailOneActiveGreenBg
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneBlueDeep
import com.indianrailways.railone.ui.theme.RailOneOrange
import com.indianrailways.railone.ui.theme.RailOneSurface
import com.indianrailways.railone.ui.theme.RailOneWarningRed
import com.indianrailways.railone.ui.theme.RailOneWarningRedBg
import com.indianrailways.railone.ui.theme.TextMuted
import com.indianrailways.railone.ui.theme.TextPrimary
import com.indianrailways.railone.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun DigitalTicketScreen(
    utsNumber: String,
    ticketRepository: TicketRepository,
    onBack: () -> Unit,
    onGoToMyBookings: () -> Unit
) {
    val unreservedTickets by ticketRepository.unreservedTickets.collectAsState()
    val ticket = unreservedTickets.find { it.utsNumber.equals(utsNumber, ignoreCase = true) }
        ?: unreservedTickets.firstOrNull()

    // Elevate screen brightness to 100% for authentic UTS ticket validation & TTE inspection
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context as? Activity
        val window = activity?.window
        val originalBrightness = window?.attributes?.screenBrightness
            ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE

        window?.let { w ->
            val layoutParams = w.attributes
            layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL // 100% brightness (1.0f)
            w.attributes = layoutParams
        }

        onDispose {
            // Restore previous screen brightness when user leaves digital ticket view
            window?.let { w ->
                val layoutParams = w.attributes
                layoutParams.screenBrightness = originalBrightness
                w.attributes = layoutParams
            }
        }
    }

    // Dynamic countdown timer (starts from 5:00 mins as in official RailOne app and ticks down each second)
    var countdownSeconds by remember { mutableIntStateOf(300) } // 5m 00s (300 seconds)

    LaunchedEffect(Unit) {
        while (countdownSeconds > 0) {
            delay(1000)
            countdownSeconds--
        }
    }

    val minutes = countdownSeconds / 60
    val seconds = countdownSeconds % 60
    val formattedTimer = String.format("%02d:%02d", minutes, seconds)

    if (ticket == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RailOneSurface),
            contentAlignment = Alignment.Center
        ) {
            Text("Ticket not found", color = TextSecondary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RailOneSurface),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Blue Header Bar matching screenshot:
        // Left back button | Title: "Booking Details" | Subtitle: "Thank You, Kedar Mitesh Vyas, Happy Journey!"
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RailOneBlue)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                    Column {
                        Text(
                            text = "Booking Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Thank You, ${ticket.passengerName}, Happy Journey!",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // 2. Black Animated Dynamic Preview Card matching screenshot
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2229)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // "INDIAN RAILWAYS / भारतीय रेल"
                    Text(
                        text = "INDIAN RAILWAYS",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "भारतीय रेल",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    DashedDivider(color = Color(0xFF424750))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Dynamic Countdown Timer
                    Text(
                        text = "Dynamic preview will close in",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formattedTimer,
                        color = Color(0xFFFF5252),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Ticket Booking Date & Time: ${ticket.formattedBookingDateTime}",
                        color = Color(0xFFFFCC80),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Ticket is Non-Transferable",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 3. Ticket Details Main Body Card matching screenshot
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Header Row: "Journey Ticket" on left | "● ACTIVE" pill on right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (ticket.ticketType.name == "RETURN") "Return Ticket" else "Journey Ticket",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RailOneActiveGreenBg
                        ) {
                            Text(
                                text = "● ACTIVE",
                                color = RailOneActiveGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // UTS Number in large bold clean typography
                    Text(
                        text = ticket.utsNumber,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RailOneBlueDeep,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    DashedDivider(color = Color(0xFFE5E9F0))
                    Spacer(modifier = Modifier.height(14.dp))

                    // 2-Column Grid matching screenshot
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Left Column
                        Column(modifier = Modifier.weight(1f)) {
                            TicketDataField(label = "Source", value = ticket.sourceStation.name, isBold = true)
                            Spacer(modifier = Modifier.height(10.dp))
                            TicketDataField(label = "Distance", value = "${ticket.distanceKm} km")
                            Spacer(modifier = Modifier.height(10.dp))
                            TicketDataField(label = "Ticket Type", value = ticket.ticketType.name)
                            Spacer(modifier = Modifier.height(10.dp))
                            TicketDataField(label = "Class", value = ticket.travelClass)
                        }

                        // Right Column
                        Column(modifier = Modifier.weight(1f)) {
                            TicketDataField(label = "Destination", value = ticket.destStation.name, isBold = true)
                            Spacer(modifier = Modifier.height(10.dp))
                            TicketDataField(label = "Passenger", value = ticket.passengerSummary)
                            Spacer(modifier = Modifier.height(10.dp))
                            TicketDataField(label = "Train Types", value = ticket.trainType.name)
                            Spacer(modifier = Modifier.height(10.dp))
                            TicketDataField(label = "Fare", value = ticket.formattedFare, isBold = true, isFare = true)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    DashedDivider(color = Color(0xFFE5E9F0))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Via & IR Registration Code
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Via: ${ticket.via}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = ticket.irRegistrationCode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Validity Notice
                    Text(
                        text = "*Valid for start of journey within 3 hour or until departure of the first train",
                        fontSize = 11.sp,
                        color = RailOneBlue,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Red Warning Callout Box matching screenshot
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = RailOneWarningRedBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2))
                    ) {
                        Text(
                            text = "Note: This ticket is non refundable. Ticket is stored locally on the device. Please do not change your handset or perform factory reset.",
                            color = RailOneWarningRed,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // "Book Connecting Journey" Pill Button matching screenshot
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(23.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7DEFF)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = RailOneBlue
                        )
                    ) {
                        Text(
                            text = "Book Connecting Journey",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Authentic Scannable QR Code
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        QrCodeView(
                            content = ticket.qrPayload,
                            modifier = Modifier.size(220.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Scan at UTS validator / for TTE verification",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // "Do you know?" info box matching screenshot
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF7F9FC))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Do you know?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "IR recovers only 57% of cost of travel on an average",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "This ticket is booked on a personal user ID. It's sale/purchase is an offence u/s 143 of the Railways Act, 1989",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "For enquiry and integrated railway helpline, please dial 139.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = RailOneBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Button: "Go to My Bookings"
                    Button(
                        onClick = onGoToMyBookings,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RailOneBlue)
                    ) {
                        Text(
                            text = "Go to My Bookings",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TicketDataField(
    label: String,
    value: String,
    isBold: Boolean = false,
    isFare: Boolean = false
) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = if (isFare) 16.sp else 13.sp,
            fontWeight = if (isBold || isFare) FontWeight.Bold else FontWeight.Medium,
            color = if (isFare) RailOneOrange else TextPrimary
        )
    }
}
