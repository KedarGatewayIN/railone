package com.indianrailways.railone.ui.screens

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianrailways.railone.data.model.TicketStatus
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.components.QrCodeView
import com.indianrailways.railone.ui.theme.RailOneActiveGreen
import com.indianrailways.railone.ui.theme.RailOneActiveGreenBg
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneWarningRed
import com.indianrailways.railone.ui.theme.RailOneWarningRedBg
import com.indianrailways.railone.ui.theme.TextMuted
import com.indianrailways.railone.ui.theme.TextPrimary
import com.indianrailways.railone.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * Pixel-perfect Portrait "Booking Details" Digital Unreserved Ticket Screen
 * matching the official RailOne app photo and screen recording (00:33 - 00:38).
 */
@Composable
fun DigitalTicketScreen(
    utsNumber: String,
    ticketRepository: TicketRepository,
    onBack: () -> Unit,
    onBookConnectingJourney: () -> Unit
) {
    val unreservedTickets by ticketRepository.unreservedTickets.collectAsState()
    val ticket = unreservedTickets.find { it.utsNumber.equals(utsNumber, ignoreCase = true) }
        ?: unreservedTickets.firstOrNull()

    val context = LocalContext.current

    // Boost screen brightness to 100% while displaying the ticket QR/dynamic preview
    DisposableEffect(Unit) {
        val activity = context as? Activity
        val window = activity?.window
        val originalBrightness = window?.attributes?.screenBrightness
            ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE

        window?.let { w ->
            val layoutParams = w.attributes
            layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
            w.attributes = layoutParams
        }

        onDispose {
            window?.let { w ->
                val layoutParams = w.attributes
                layoutParams.screenBrightness = originalBrightness
                w.attributes = layoutParams
            }
        }
    }

    // 5-minute dynamic preview countdown timer (05:00 -> 00:00)
    var countdownSeconds by remember(utsNumber) { mutableIntStateOf(300) }
    LaunchedEffect(utsNumber) {
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
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text("Ticket not found", color = TextSecondary)
        }
        return
    }

    val isCancelled = ticket.status == TicketStatus.CANCELLED

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ═══════════════════════════════════════════════════════
        // 1. BLUE TOP HEADER: Back Arrow | "Booking Details" + "Mobile: ..."
        // ═══════════════════════════════════════════════════════
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
                    .background(Color.White.copy(alpha = 0.20f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Booking Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = "Mobile: ${ticket.passengerMobile}",
                    fontSize = 12.5.sp,
                    color = Color.White.copy(alpha = 0.92f),
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // ═══════════════════════════════════════════════════════
        // 2. THANK YOU BANNER DIV
        // ═══════════════════════════════════════════════════════
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF7F8FA))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Thank You ${ticket.passengerName}, Happy Journey !",
                fontSize = 12.5.sp,
                color = Color(0xFF707882),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
        HorizontalDivider(color = Color(0xFFE5E9F0), thickness = 1.dp)

        // ═══════════════════════════════════════════════════════
        // 3. VERTICAL SCROLLABLE TICKET BODY
        // ═══════════════════════════════════════════════════════
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Combined Ticket Card (Dark Dynamic Preview on Top + White Details below)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                // ──────────────────────────────────────────────────
                // A. DARK DYNAMIC PREVIEW WINDOW
                // ──────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(215.dp)
                        .drawBehind {
                            // Subtle diamond checkerboard mosaic matching official screenshot
                            val diamondW = 46.dp.toPx()
                            val diamondH = 30.dp.toPx()
                            val halfW = diamondW / 2f
                            val halfH = diamondH / 2f

                            val darkA = Color(0xFF0B0E14)
                            val darkB = Color(0xFF1A1F2A)

                            val cols = (size.width / diamondW).toInt() + 3
                            val rows = (size.height / halfH).toInt() + 4

                            for (r in -1..rows) {
                                val y = r * halfH
                                val rowOffset = if (r % 2 != 0) halfW else 0f
                                for (c in -1..cols) {
                                    val x = c * diamondW + rowOffset
                                    val isEven = ((r + c) % 2 == 0)
                                    val path = Path().apply {
                                        moveTo(x, y - halfH)
                                        lineTo(x + halfW, y)
                                        lineTo(x, y + halfH)
                                        lineTo(x - halfW, y)
                                        close()
                                    }
                                    drawPath(path, if (isEven) darkB else darkA)
                                }
                            }
                        }
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Left rotated "INDIAN RAILWAYS" strip + vertical dashed border
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(34.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "INDIAN RAILWAYS",
                                color = Color.White.copy(alpha = 0.92f),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier
                                    .verticalRotate(-90f)
                            )
                        }

                        VerticalDashedLine(
                            color = Color.White.copy(alpha = 0.55f),
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 8.dp)
                        )

                        // Center Dynamic Preview Countdown & Timestamp
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(horizontal = 8.dp, vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Dynamic preview will close in",
                                color = Color.White.copy(alpha = 0.92f),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = formattedTimer,
                                color = Color(0xFFE51C23),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Ticket Booking Date & Time",
                                color = Color.White.copy(alpha = 0.92f),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = ticket.formattedBookingDateTime,
                                color = Color(0xFFFF9800),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = ticket.rCode,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Ticket is Non-Transferable",
                                color = Color.White.copy(alpha = 0.92f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        VerticalDashedLine(
                            color = Color.White.copy(alpha = 0.55f),
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 8.dp)
                        )

                        // Right rotated "भारतीय रेल" strip
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(34.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "भारतीय रेल",
                                color = Color.White.copy(alpha = 0.92f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier
                                    .verticalRotate(90f)
                            )
                        }
                    }
                }

                // ──────────────────────────────────────────────────
                // B. WHITE TICKET DETAILS SECTION
                // ──────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    // Header Row: Journey Ticket + UTS Number | ACTIVE badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (ticket.ticketType.name == "RETURN") "Return Ticket" else "Journey Ticket",
                                fontSize = 11.5.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ticket.utsNumber,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E293B)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCancelled) RailOneWarningRedBg else RailOneActiveGreenBg
                        ) {
                            Text(
                                text = if (isCancelled) "● CANCELLED" else "● ACTIVE",
                                color = if (isCancelled) RailOneWarningRed else RailOneActiveGreen,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFE5E9F0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // 2-Column Data Grid (Left column left-aligned, Right column RIGHT-aligned)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TicketDetailField(label = "Source", value = ticket.sourceStation.name, alignEnd = false)
                            TicketDetailField(label = "Distance", value = "${ticket.distanceKm} km", alignEnd = false)
                            TicketDetailField(label = "Ticket Type", value = ticket.ticketType.label, alignEnd = false)
                            TicketDetailField(label = "Class", value = ticket.travelClass, alignEnd = false)
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TicketDetailField(label = "Destination", value = ticket.destStation.name, alignEnd = true)
                            TicketDetailField(label = "Passenger", value = ticket.passengerSummary, alignEnd = true)
                            TicketDetailField(label = "Train Types", value = ticket.trainType.label, alignEnd = true)
                            TicketDetailField(label = "Fare", value = ticket.formattedFare, alignEnd = true, isFare = true)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFE5E9F0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Via & IR Registration Code
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Via",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Via: ${ticket.via}",
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = ticket.irRegistrationCode,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569),
                        fontFamily = FontFamily.SansSerif
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cyan Validity Callout
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEBF9FB),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "*Valid for start of journey within 3 hour or until departure of the first train",
                            color = Color(0xFF475569),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Red Warning Callout
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFDECEC),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Note: This ticket is non refundable. Ticket is stored locally on the device. Please do not change your handset or perform factory reset.",
                            color = Color(0xFFC62828),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // "Book Connecting Journey" Outlined Pill Button
                    OutlinedButton(
                        onClick = onBookConnectingJourney,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(23.dp),
                        border = BorderStroke(1.dp, RailOneBlue)
                    ) {
                        Text(
                            text = "Book Connecting Journey",
                            color = RailOneBlue,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ═══════════════════════════════════════════════════════
            // C. LARGE SCANNABLE QR CODE (matching video 00:35 - 00:36)
            // ═══════════════════════════════════════════════════════
            QrCodeView(
                content = ticket.qrPayload,
                modifier = Modifier.size(220.dp),
                qrColor = Color.Black,
                backgroundColor = Color.White
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ═══════════════════════════════════════════════════════
            // D. "Do you know?" & LEGAL FOOTNOTE (matching video 00:36)
            // ═══════════════════════════════════════════════════════
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "Do you know?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "IR recovers only 57% of cost of travel on an average.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFE5E9F0), thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "This ticket is booked on a personal user ID. It's sale/purchase is an offence u/s 143 of the Railways Act, 1989.",
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFE5E9F0), thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "For enquiry and integrated railway helpline, please dial 139.",
                    fontSize = 11.5.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TicketDetailField(
    label: String,
    value: String,
    alignEnd: Boolean,
    isFare: Boolean = false
) {
    Column(
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = value,
            fontSize = if (isFare) 14.sp else 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start
        )
    }
}

@Composable
private fun VerticalDashedLine(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.width(1.5.dp)) {
        drawLine(
            color = color,
            start = Offset(size.width / 2f, 0f),
            end = Offset(size.width / 2f, size.height),
            strokeWidth = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
        )
    }
}

/**
 * Rotates a single-line Composable cleanly by 90 or -90 degrees and swaps its measured bounds
 * so it never clips inside narrow vertical side strips.
 */
private fun Modifier.verticalRotate(degrees: Float): Modifier = this
    .layout { measurable, _ ->
        val placeable = measurable.measure(Constraints())
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width - placeable.height) / 2,
                y = (placeable.width - placeable.height) / 2
            )
        }
    }
    .rotate(degrees)
