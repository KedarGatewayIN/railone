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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsRailway
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.indianrailways.railone.data.model.ReservedTicket
import com.indianrailways.railone.data.model.TicketStatus
import com.indianrailways.railone.data.model.UnreservedTicket
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.components.JourneyCardType
import com.indianrailways.railone.ui.components.JourneyPlannerCard
import com.indianrailways.railone.ui.components.RailOneLanguageButton
import com.indianrailways.railone.ui.components.RailOneLogo
import com.indianrailways.railone.ui.components.RailOneNotificationButton
import com.indianrailways.railone.ui.components.SocialMediaBannerCard
import com.indianrailways.railone.ui.components.UpcomingTicketShape
import com.indianrailways.railone.ui.components.WavesLogoIcon
import com.indianrailways.railone.ui.navigation.Screen
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneBlueDeep
import com.indianrailways.railone.ui.theme.ServiceAmber
import com.indianrailways.railone.ui.theme.ServiceBlue
import com.indianrailways.railone.ui.theme.ServiceGray
import com.indianrailways.railone.ui.theme.ServiceGreen
import com.indianrailways.railone.ui.theme.ServicePeach
import com.indianrailways.railone.ui.theme.ServicePink
import com.indianrailways.railone.ui.theme.ServicePurple
import com.indianrailways.railone.ui.theme.ServiceWaves
import com.indianrailways.railone.ui.theme.TextPrimary
import com.indianrailways.railone.ui.theme.TextSecondary
import kotlinx.coroutines.launch

data class ServiceOffering(
    val title: String,
    val icon: ImageVector?,
    val iconTint: Color,
    val backgroundColor: Color,
    val route: String,
    val isWaves: Boolean = false
)

data class RailwayFact(
    val title: String,
    val description: String,
    val imageRes: Int
)

@Composable
fun HomeScreen(
    ticketRepository: TicketRepository,
    onNavigate: (String) -> Unit
) {
    val unreservedTickets by ticketRepository.unreservedTickets.collectAsState()
    val reservedTickets by ticketRepository.reservedTickets.collectAsState()
    val notifications by ticketRepository.notifications.collectAsState()

    val unreadNotifCount = notifications.count { !it.isRead }
    val latestUnreserved = unreservedTickets.firstOrNull { it.status == TicketStatus.ACTIVE }
    val latestReserved = reservedTickets.firstOrNull { it.status == TicketStatus.ACTIVE }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val offerings = remember {
        listOf(
            ServiceOffering("Search\nTrains", Icons.Default.Search, Color(0xFFE91E63), ServicePink, Screen.ReservedBooking.route),
            ServiceOffering("PNR\nStatus", Icons.Default.ConfirmationNumber, Color(0xFF388E3C), ServiceGreen, Screen.PnrStatus.route),
            ServiceOffering("Coach\nPosition", Icons.Default.DirectionsRailway, Color(0xFF0277BD), ServiceBlue, Screen.CoachPosition.route),
            ServiceOffering("Track Your\nTrain", Icons.Default.Train, Color(0xFFF5A623), ServiceAmber, Screen.LiveTrainStatus.route),
            ServiceOffering("Order\nFood", Icons.Default.Fastfood, Color(0xFF3949AB), ServicePurple, Screen.OrderFood.route),
            ServiceOffering("File\nRefund", Icons.Default.ReceiptLong, Color(0xFF455A64), ServiceGray, Screen.FileRefund.route),
            ServiceOffering("Rail\nMadad", Icons.Default.Handshake, Color(0xFFE57373), ServicePeach, Screen.RailMadad.route),
            ServiceOffering("Go To\nWAVES", null, Color.White, ServiceWaves, Screen.YouProfile.route, isWaves = true)
        )
    }

    val facts = remember {
        listOf(
            RailwayFact(
                title = "Historical Milestone",
                description = "First ever passenger train was run between Bori Bandar to Thane on April 16, 1853.",
                imageRes = R.drawable.img_trivia_1853
            ),
            RailwayFact(
                title = "Engineering Marvel",
                description = "Chenab Railway Bridge in Dharot, Jammu & Kashmir is the World's highest Railway Bridge.",
                imageRes = R.drawable.img_trivia_bridge
            ),
            RailwayFact(
                title = "World Record",
                description = "Hubballi Junction in Karnataka holds the record for the world's longest railway platform at 1,507 m.",
                imageRes = R.drawable.img_trivia_1853
            ),
            RailwayFact(
                title = "Green Railways",
                description = "Indian Railways is achieving 100% broad-gauge electrification towards Net Zero Carbon Emission.",
                imageRes = R.drawable.img_trivia_bridge
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFBFC))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Official Top Bar: Language circle (A / अ) | RailOne logo | Notification bell with red badge
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RailOneLanguageButton(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("App language: English (हिंदी available)")
                            }
                        }
                    )

                    RailOneLogo(fontSizeSp = 27f)

                    RailOneNotificationButton(
                        unreadCount = unreadNotifCount,
                        onClick = { onNavigate(Screen.Notifications.route) }
                    )
                }
            }

            // 2. Greeting & Journey Planner Heading
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 12.dp, bottom = 10.dp)
                ) {
                    Text(
                        text = "Hi, Kedar Mitesh Vyas!",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Journey Planner",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF133863)
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
                        onClick = { onNavigate(Screen.ReservedBooking.route) },
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

            // 4. "More Offerings" 4x2 Grid
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                ) {
                    Text(
                        text = "More Offerings",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF133863)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        offerings.take(4).forEach { offering ->
                            OfferingGridItem(offering = offering, onNavigate = onNavigate)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 14.dp)
                ) {
                    Text(
                        text = "Upcoming Journey",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF133863)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        latestUnreserved != null -> {
                            UpcomingUnreservedCard(
                                ticket = latestUnreserved,
                                onViewDetails = {
                                    onNavigate(Screen.DigitalTicket.createRoute(latestUnreserved.utsNumber))
                                },
                                onBookAgain = {
                                    onNavigate(Screen.UnreservedBooking.route)
                                }
                            )
                        }
                        latestReserved != null -> {
                            UpcomingReservedCard(
                                ticket = latestReserved,
                                onViewDetails = { onNavigate(Screen.MyBookings.route) },
                                onBookAgain = { onNavigate(Screen.ReservedBooking.route) }
                            )
                        }
                        else -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigate(Screen.UnreservedBooking.route) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No upcoming journey booked",
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Tap to book an instant Unreserved or Reserved ticket",
                                        color = RailOneBlue,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
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
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Do You know?",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF133863),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(facts) { fact ->
                            Column(
                                modifier = Modifier.width(172.dp)
                            ) {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(112.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = fact.imageRes),
                                        contentDescription = fact.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = fact.description,
                                    fontSize = 12.sp,
                                    color = Color(0xFF334155),
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // 7. "Follow Us On Social Media Platforms" Scenic Banner
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Follow Us On Social Media Platforms",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF133863)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    SocialMediaBannerCard(
                        onPlatformClick = { platform ->
                            scope.launch {
                                snackbarHostState.showSnackbar("Official Indian Railways on $platform")
                            }
                        }
                    )
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
                .size(58.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(offering.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            if (offering.isWaves) {
                WavesLogoIcon(modifier = Modifier.size(32.dp))
            } else if (offering.icon != null) {
                Icon(
                    imageVector = offering.icon,
                    contentDescription = offering.title,
                    tint = offering.iconTint,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = offering.title,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF334155),
            textAlign = TextAlign.Center,
            lineHeight = 14.5.sp
        )
    }
}

@Composable
fun UpcomingUnreservedCard(
    ticket: UnreservedTicket,
    onViewDetails: () -> Unit,
    onBookAgain: () -> Unit
) {
    val ticketShape = UpcomingTicketShape(cornerRadius = 18.dp, notchRadius = 9.dp, notchXFraction = 0.72f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ticketShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF7D6DF0), Color(0xFF9C7CE8), Color(0xFFB78BE7))
                )
            )
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = ticket.formattedBookingDateShort,
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.8.dp)
                    .background(Color.White.copy(alpha = 0.25f))
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Route row with dashed separator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ticket.sourceStation.name,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "- - - - - - - -",
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 11.sp
                )
                Text(
                    text = ticket.destStation.name,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Unreserved",
                    color = Color.White,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF563FA6).copy(alpha = 0.45f))
                            .border(1.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                            .clickable { onBookAgain() }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Book Again",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF563FA6).copy(alpha = 0.65f))
                            .border(1.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                            .clickable { onViewDetails() }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "View Details",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UpcomingReservedCard(
    ticket: ReservedTicket,
    onViewDetails: () -> Unit,
    onBookAgain: () -> Unit
) {
    val ticketShape = UpcomingTicketShape(cornerRadius = 18.dp, notchRadius = 9.dp, notchXFraction = 0.72f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ticketShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF7D6DF0), Color(0xFF9C7CE8), Color(0xFFB78BE7))
                )
            )
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "${ticket.journeyDate} • PNR ${ticket.pnr}",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.8.dp)
                    .background(Color.White.copy(alpha = 0.25f))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(ticket.sourceStation.name, color = Color.White, fontSize = 13.sp)
                Text("- - - - - - -", color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
                Text(ticket.destStation.name, color = Color.White, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reserved (${ticket.travelClass})",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF563FA6).copy(alpha = 0.45f))
                            .border(1.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                            .clickable { onBookAgain() }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Book Again", color = Color.White, fontSize = 11.5.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF563FA6).copy(alpha = 0.65f))
                            .border(1.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                            .clickable { onViewDetails() }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("View Details", color = Color.White, fontSize = 11.5.sp)
                    }
                }
            }
        }
    }
}
