package com.indianrailways.railone.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneOrange
import com.indianrailways.railone.ui.theme.TextPrimary

@Composable
fun JourneyPlannerCard(
    title: String,
    type: JourneyCardType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                when (type) {
                    JourneyCardType.RESERVED -> ReservedIllustration()
                    JourneyCardType.UNRESERVED -> UnreservedIllustration()
                    JourneyCardType.PLATFORM -> PlatformIllustration()
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}

enum class JourneyCardType {
    RESERVED,
    UNRESERVED,
    PLATFORM
}

@Composable
private fun ReservedIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Sky & hills scenic background seen through train window
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF81D4FA), Color(0xFFE1F5FE))
            )
        )
        // Rolling green hills
        val hillPath = Path().apply {
            moveTo(0f, size.height * 0.7f)
            quadraticBezierTo(size.width * 0.35f, size.height * 0.4f, size.width * 0.7f, size.height * 0.65f)
            quadraticBezierTo(size.width * 0.85f, size.height * 0.75f, size.width, size.height * 0.6f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(hillPath, Color(0xFF81C784))

        // Train window frame & coach seats
        drawRoundRect(
            color = Color(0xFF37474F),
            topLeft = Offset(4f, 4f),
            size = Size(size.width - 8f, size.height - 8f),
            cornerRadius = CornerRadius(12f, 12f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
        )

        // Passenger silhouettes sitting by window
        drawCircle(
            color = Color(0xFF1E88E5),
            radius = size.height * 0.15f,
            center = Offset(size.width * 0.35f, size.height * 0.45f)
        )
        drawCircle(
            color = Color(0xFFE53935),
            radius = size.height * 0.12f,
            center = Offset(size.width * 0.65f, size.height * 0.48f)
        )
    }
}

@Composable
private fun UnreservedIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Warm interior of Indian Railways general unreserved compartment
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFFECB3), Color(0xFFFFCC80))
            )
        )
        // Luggage rack and fans
        drawRoundRect(
            color = Color(0xFFB0BEC5),
            topLeft = Offset(size.width * 0.1f, 10f),
            size = Size(size.width * 0.8f, 12f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Cheerful passengers commuting together
        drawCircle(
            color = Color(0xFFE64A19),
            radius = size.height * 0.16f,
            center = Offset(size.width * 0.3f, size.height * 0.52f)
        )
        drawCircle(
            color = Color(0xFF00897B),
            radius = size.height * 0.15f,
            center = Offset(size.width * 0.52f, size.height * 0.48f)
        )
        drawCircle(
            color = Color(0xFF5E35B1),
            radius = size.height * 0.14f,
            center = Offset(size.width * 0.74f, size.height * 0.54f)
        )
        // Subtle badge
        drawCircle(
            color = RailOneOrange,
            radius = 6f,
            center = Offset(size.width * 0.85f, 20f)
        )
    }
}

@Composable
private fun PlatformIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Indian Railways platform & track with modern Vande Bharat train
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFFF8E1), Color(0xFFFFE0B2))
            )
        )
        // Station platform roof & tiles
        drawRect(
            color = Color(0xFF78909C),
            topLeft = Offset(0f, size.height * 0.65f),
            size = Size(size.width, size.height * 0.35f)
        )
        // Yellow safety platform edge line
        drawRect(
            color = Color(0xFFFFD54F),
            topLeft = Offset(0f, size.height * 0.65f),
            size = Size(size.width, 6f)
        )
        // Aerodynamic Vande Bharat train front (White & Blue)
        val trainNose = Path().apply {
            moveTo(size.width * 0.15f, size.height * 0.65f)
            lineTo(size.width * 0.85f, size.height * 0.65f)
            lineTo(size.width * 0.85f, size.height * 0.28f)
            quadraticBezierTo(size.width * 0.45f, size.height * 0.26f, size.width * 0.15f, size.height * 0.52f)
            close()
        }
        drawPath(trainNose, Color.White)
        // Blue aerodynamic stripe
        drawRect(
            color = RailOneBlue,
            topLeft = Offset(size.width * 0.2f, size.height * 0.44f),
            size = Size(size.width * 0.65f, 8f)
        )
    }
}
