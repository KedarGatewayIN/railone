package com.indianrailways.railone.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.TextMuted
import com.indianrailways.railone.ui.theme.TextPrimary
import com.indianrailways.railone.ui.theme.TextSecondary

@Composable
fun NotificationScreen(
    ticketRepository: TicketRepository,
    onBack: () -> Unit
) {
    val notifications by ticketRepository.notifications.collectAsState()

    LaunchedEffect(Unit) {
        ticketRepository.markAllNotificationsRead()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Blue Top Header matching official video (00:47 - 00:49)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RailOneBlue)
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                Text(
                    text = "Notification",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = Color.White
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(notifications, key = { it.id }) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEF4FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Train,
                            contentDescription = null,
                            tint = RailOneBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.title,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.message,
                            fontSize = 12.5.sp,
                            color = TextSecondary,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.formattedTime,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
                HorizontalDivider(color = Color(0xFFEAECEF), thickness = 1.dp)
            }
        }
    }
}
