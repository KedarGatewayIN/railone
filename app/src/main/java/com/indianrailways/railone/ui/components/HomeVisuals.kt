package com.indianrailways.railone.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianrailways.railone.R
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
                    .background(Color(0xFFE8F1FC))
            ) {
                val imageRes = when (type) {
                    JourneyCardType.RESERVED -> R.drawable.img_journey_reserved
                    JourneyCardType.UNRESERVED -> R.drawable.img_journey_unreserved
                    JourneyCardType.PLATFORM -> R.drawable.img_journey_platform
                }
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
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

/**
 * Authentic RailOne Logo with signature orange dot over the letter 'i'.
 */
@Composable
fun RailOneLogo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Ra",
            fontSize = 25.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF263238),
            letterSpacing = (-0.5).sp
        )
        // Custom 'i' with signature orange dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 0.5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(RailOneOrange)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .width(3.2.dp)
                    .height(13.5.dp)
                    .background(Color(0xFF263238), RoundedCornerShape(1.dp))
            )
        }
        Text(
            text = "l",
            fontSize = 25.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF263238),
            letterSpacing = (-0.5).sp
        )
        Text(
            text = "One",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1D20),
            letterSpacing = (-0.5).sp
        )
    }
}
