package com.indianrailways.railone.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianrailways.railone.R
import com.indianrailways.railone.data.model.TicketStatus
import com.indianrailways.railone.data.model.UnreservedTicket
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.components.JourneyCardType
import com.indianrailways.railone.ui.components.JourneyPlannerCard
import com.indianrailways.railone.ui.components.RailOneLogo
import com.indianrailways.railone.ui.navigation.Screen
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneBlueDeep
import com.indianrailways.railone.ui.theme.RailOneOrange
import com.indianrailways.railone.ui.theme.RailOneSurface
import com.indianrailways.railone.ui.theme.ServiceAmber
import com.indianrailways.railone.ui.theme.ServiceBlue
import com.indianrailways.railone.ui.theme.ServiceGray
import com.indianrailways.railone.ui.theme.ServiceGreen
import com.indianrailways.railone.ui.theme.ServicePeach
import com.indianrailways.railone.ui.theme.ServicePink
import com.indianrailways.railone.ui.theme.ServicePurple
import com.indianrailways.railone.ui.theme.ServiceWaves
import com.indianrailways.railone.ui.theme.TextMuted
import com.indianrailways.railone.ui.theme.TextPrimary
import com.indianrailways.railone.ui.theme.TextSecondary
import kotlinx.coroutines.launch

data class ServiceOffering(
    val title: String,
    val icon: ImageVector,
    val iconTint: Color,
    val backgroundColor: Color,
    val route: String
)

data class RailwayFact(
    val title: String,
    val description: String,
    val badge: String,
    val imageRes: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    ticketRepository: TicketRepository,
    onNavigate: (String) -> Unit
) {
    val unreservedTickets by ticketRepository.unreservedTickets.collectAsState()
    val latestTicket = unreservedTickets.find { it.status == TicketStatus.ACTIVE } ?: unreservedTickets.firstOrNull()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val offerings = remember {
        listOf(
            ServiceOffering("Search\nTrains", Icons.Default.Search, Color(0xFFE91E63), ServicePink, Screen.LiveTrainStatus.route),
            ServiceOffering("PNR\nStatus", Icons.Outlined.ConfirmationNumber, Color(0xFF2E7D32), ServiceGreen, Screen.PnrStatus.route),
            ServiceOffering("Coach\nPosition", Icons.Default.Train, Color(0xFF0288D1), ServiceBlue, Screen.CoachPosition.route),
            ServiceOffering("Track Your\nTrain", Icons.Default.Train, Color(0xFFF57C00), ServiceAmber, Screen.LiveTrainStatus.route),
            ServiceOffering("Order\nFood", Icons.Default.Fastfood, Color(0xFF7B1FA2), ServicePurple, Screen.OrderFood.route),
            ServiceOffering("File\nRefund", Icons.Default.ReceiptLong, Color(0xFF455A64), ServiceGray, Screen.FileRefund.route),
            ServiceOffering("Rail\nMadad", Icons.Default.Handshake, Color(0xFFD32F2F), ServicePeach, Screen.RailMadad.route),
            ServiceOffering("Go To\nWAVES", Icons.Default.Share, Color.White, ServiceWaves, Screen.YouProfile.route)
        )
    }

    val facts = remember {
        listOf(
            RailwayFact(
                title = "Historical Milestone",
                description = "First ever passenger train was run between Bori Bandar to Thane on April 16, 1853.",
                badge = "1853",
                imageRes = R.drawable.img_trivia_1853
            ),
            RailwayFact(
                title = "Engineering Marvel",
                description = "Chenab Railway Bridge in Dharot, Jammu & Kashmir is the World's highest Railway Bridge.",
                badge = "Record",
                imageRes = R.drawable.img_trivia_bridge
            ),
            RailwayFact(
                title = "World Record",
                description = "World's longest railway platform is at Hubballi Jn, Karnataka, stretching 1,507 meters.",
                badge = "Platform",
                imageRes = R.drawable.img_trivia_bridge
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(RailOneSurface),
            contentPadding = PaddingValues(bottom = 84.dp)
        ) {
            // 1. Top Bar: Language icon | RailOne logo | Notification bell with badge "2"
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Language/Calendar Switcher Circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEF4FF))
                            .clickable {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Language set to English / Hindi")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "A",
                                color = RailOneBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 12.sp
                            )
                            Text(
                                text = "३१",
                                color = RailOneBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 11.sp
                            )
                        }
                    }

                    // Center: Authentic RailOne Logo
                    RailOneLogo()

                    // Right: Notification Bell with Badge "2"
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = Color(0xFFE53935),
                                contentColor = Color.White
                            ) {
                                Text("2", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    ) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("2 updates: UTS Paperless service active & PNR alert")
                                }
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = TextPrimary
                            )
                        }
                    }
                }
            }

            // 2. User Greeting: "Hi, Kedar Mitesh Vyas!"
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Hi, Kedar Mitesh Vyas!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = RailOneBlueDeep
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Journey Planner",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RailOneBlueDeep
                    )
                }
            }

            // 3. Journey Planner 3 Cards: Reserved | Unreserved | Platform
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    JourneyPlannerCard(
                        title = "Reserved",
                        type = JourneyCardType.RESERVED,
                        onClick = { onNavigate(Screen.MyBookings.route) },
                        modifier = Modifier.weight(1f)
                    )
                    JourneyPlannerCard(
                        title = "Unreserved",
                        type = JourneyCardType.UNRESERVED,
                        onClick = { onNavigate(Screen.UnreservedBooking.route) },
                        modifier = Modifier.weight(1f)
                    )
                    JourneyPlannerCard(
                        title = "Platform",
                        type = JourneyCardType.PLATFORM,
                        onClick = { onNavigate(Screen.PlatformBooking.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 4. "More Offerings" Title & Grid (4x2 cards)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "More Offerings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RailOneBlueDeep
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Row 1 (4 items)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        offerings.take(4).forEach { offering ->
                            OfferingGridItem(offering = offering, onNavigate = onNavigate)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 2 (4 items)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        offerings.drop(4).take(4).forEach { offering ->
                            OfferingGridItem(offering = offering, onNavigate = onNavigate)
                        }
                    }
                }
            }

            // 5. "Upcoming Journey" Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Upcoming Journey",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RailOneBlueDeep
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (latestTicket != null) {
                        UpcomingTicketCard(
                            ticket = latestTicket,
                            onViewDetails = {
                                onNavigate(Screen.DigitalTicket.createRoute(latestTicket.utsNumber))
                            },
                            onBookAgain = {
                                onNavigate(Screen.UnreservedBooking.route)
                            }
                        )
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(Screen.UnreservedBooking.route) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No active journey ticket",
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tap to book an instant unreserved ticket",
                                    color = RailOneBlue,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // 6. "Do You know?" Railway Trivia Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "Do You know?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RailOneBlueDeep,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(facts) { fact ->
                            Column(
                                modifier = Modifier.width(180.dp)
                            ) {
                                if (fact.imageRes != null) {
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(115.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = fact.imageRes),
                                            contentDescription = fact.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = fact.description,
                                    fontSize = 12.sp,
                                    color = RailOneBlueDeep,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // 7. "Follow Us On Social Media Platforms" Banner
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Follow Us On Social Media Platforms",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = RailOneBlueDeep
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFF283593), Color(0xFFAD1457)),
                                        start = Offset(0f, 0f),
                                        end = Offset(800f, 400f)
                                    )
                                )
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SocialMediaCircle(text = "𝕏", bg = Color.Black)
                                SocialMediaCircle(text = "f", bg = Color(0xFF1877F2))
                                SocialMediaCircle(text = "IG", bg = Color(0xFFE1306C))
                                SocialMediaCircle(text = "▶", bg = Color(0xFFFF0000))
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
fun OfferingGridItem(
    offering: ServiceOffering,
    onNavigate: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable { onNavigate(offering.route) }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(offering.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = offering.icon,
                contentDescription = offering.title,
                tint = offering.iconTint,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = offering.title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun UpcomingTicketCard(
    ticket: UnreservedTicket,
    onViewDetails: () -> Unit,
    onBookAgain: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF512DA8), Color(0xFF7E57C2))
                    )
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top row: Journey Date (formatted like "Sun, 22 Nov 26")
                Text(
                    text = ticket.formattedBookingDateShort,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Route: Stations
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ticket.sourceStation.name,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "----------------",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = ticket.destStation.name,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom row: Tag + Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Unreserved",
                        color = Color(0xFF80D8FF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                                .clickable { onBookAgain() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Book Again",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.22f))
                                .clickable { onViewDetails() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "View Details",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SocialMediaCircle(text: String, bg: Color) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}
