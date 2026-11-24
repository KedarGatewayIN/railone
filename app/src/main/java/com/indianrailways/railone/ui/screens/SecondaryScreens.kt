package com.indianrailways.railone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianrailways.railone.ui.components.DashedDivider
import com.indianrailways.railone.ui.theme.RailOneActiveGreen
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneBlueDeep
import com.indianrailways.railone.ui.theme.RailOneOrange
import com.indianrailways.railone.ui.theme.RailOneSurface
import com.indianrailways.railone.ui.theme.TextMuted
import com.indianrailways.railone.ui.theme.TextPrimary
import com.indianrailways.railone.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun SimpleTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RailOneBlue)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(38.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// 1. COACH POSITION SCREEN
@Composable
fun CoachPositionScreen(onBack: () -> Unit) {
    val coaches = listOf(
        "LOCO", "GEN", "GEN", "S1", "S2", "S3", "S4", "S5", "PC", "B1", "B2", "B3", "B4", "A1", "A2", "H1", "GEN", "EOG"
    )

    Column(modifier = Modifier.fillMaxSize().background(RailOneSurface)) {
        SimpleTopBar(title = "Coach Position", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("20906 EKNR MAHAMANA EX", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RailOneBlueDeep)
                        Text("From: Jabalpur (JBP) → Bharuch Jn (BH)", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Platform Train Rake Composition (Engine to Guard):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Horizontal Coach Composition Scroll
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            coaches.forEachIndexed { index, coach ->
                                val isEngine = coach == "LOCO"
                                val isHighlighted = coach == "B3"
                                Surface(
                                    modifier = Modifier
                                        .width(62.dp)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = when {
                                        isHighlighted -> RailOneOrange
                                        isEngine -> Color(0xFFD32F2F)
                                        coach.startsWith("B") || coach.startsWith("A") -> Color(0xFF1976D2)
                                        else -> Color(0xFF546E7A)
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(coach, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${index + 1}", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).background(RailOneOrange, RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Your Coach (B3)", fontSize = 11.sp, color = TextSecondary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).background(Color(0xFF1976D2), RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AC Coaches", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 2. ORDER FOOD (FOOD ON TRACK)
@Composable
fun OrderFoodScreen(onBack: () -> Unit) {
    val meals = listOf(
        Triple("Special Veg Maharaja Thali", "Paneer butter masala, Dal makhani, 3 Rotis, Jeera rice, Gulab jamun", "₹140"),
        Triple("Hyderabadi Dum Biryani", "Fragrant basmati rice served with raita and mirchi ka salan", "₹160"),
        Triple("South Indian Breakfast Combo", "2 Idlis, 1 Crispy Vada with sambar & fresh coconut chutney", "₹70"),
        Triple("Rail Neer Packaged Drinking Water (1L)", "Chilled, sealed Indian Railways packaged drinking water", "₹15"),
        Triple("Kulhad Masala Chai", "Fresh hot brewed tea with cardamom & ginger in terracotta cup", "₹20")
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(RailOneSurface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            SimpleTopBar(title = "Food on Track (IRCTC)", onBack = onBack)

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Delivered directly to your train seat", fontSize = 13.sp, color = TextSecondary)
                }
                items(meals) { (name, desc, price) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(desc, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(price, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RailOneOrange)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Added $name to cart for seat B3-32")
                                    }
                                },
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RailOneBlue)
                            ) {
                                Text("Add", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

// 3. RAIL MADAD SCREEN
@Composable
fun RailMadadScreen(onBack: () -> Unit) {
    var selectedCategory by remember { mutableStateOf("Coach Cleanliness") }
    var tokenGenerated by remember { mutableStateOf(false) }

    val categories = listOf(
        Pair("Medical Assistance", Icons.Default.MedicalServices),
        Pair("Coach Cleanliness", Icons.Default.CleaningServices),
        Pair("Water Availability", Icons.Default.WaterDrop),
        Pair("Electrical Equipment", Icons.Default.ElectricBolt),
        Pair("Security Assistance", Icons.Default.Security)
    )

    Column(modifier = Modifier.fillMaxSize().background(RailOneSurface)) {
        SimpleTopBar(title = "Rail Madad (Grievance)", onBack = onBack)

        Column(modifier = Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Complaint Category", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    categories.forEach { (cat, icon) ->
                        val isSelected = selectedCategory == cat
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) RailOneBlue.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable { selectedCategory = cat }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon, cat, tint = if (isSelected) RailOneBlue else TextSecondary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(cat, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    if (!tokenGenerated) {
                        Button(
                            onClick = { tokenGenerated = true },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(23.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RailOneOrange)
                        ) {
                            Text("Submit Quick Grievance")
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Grievance Registered: #RM-2026-9812", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Text("Assigned to Train Superintendent & Onboard staff.", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 4. FILE REFUND SCREEN
@Composable
fun FileRefundScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(RailOneSurface)) {
        SimpleTopBar(title = "File Refund & TDR", onBack = onBack)
        Column(modifier = Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ticket Deposit Receipt (TDR)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "File TDR for cancelled trains, train delayed > 3 hours, or AC failure as per Railway refund rules.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF5F7FA),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "No pending refund requests found for your account.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// 5. YOU (PROFILE) SCREEN
@Composable
fun YouProfileScreen(onBack: () -> Unit) {
    var offlineWalletEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().background(RailOneSurface)) {
        SimpleTopBar(title = "My Profile & Wallet", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(RailOneBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("KMV", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Kedar Mitesh Vyas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("+91 98799 88201", fontSize = 12.sp, color = TextSecondary)
                            Text("IRCTC ID: kedar_vyas99 (Verified)", fontSize = 12.sp, color = RailOneBlue, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Rail Wallet Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBalanceWallet, "Wallet", tint = RailOneOrange)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("R-Wallet Balance", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Text("₹450.00", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = RailOneActiveGreen)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("3% cashback bonus active on R-Wallet top-ups for UTS unreserved tickets.", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }

            // Preferences
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Security & Preferences", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Offline UTS Ticket Storage", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Store scannable digital tickets locally without internet", fontSize = 11.sp, color = TextSecondary)
                            }
                            Switch(
                                checked = offlineWalletEnabled,
                                onCheckedChange = { offlineWalletEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RailOneBlue)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Biometric Authentication", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Require Fingerprint to open active ticket barcode", fontSize = 11.sp, color = TextSecondary)
                            }
                            Switch(
                                checked = biometricEnabled,
                                onCheckedChange = { biometricEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RailOneBlue)
                            )
                        }
                    }
                }
            }
        }
    }
}

// 6. MENU SCREEN
@Composable
fun MenuScreen(
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RailOneSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RailOneBlue)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RailOne Super App Services",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val menuItems = listOf(
                Pair("UTS Unreserved Ticketing", "Book paperless local & express tickets"),
                Pair("IRCTC Train Reservation", "Reserve sleeper, 3AC, 2AC, and Vande Bharat"),
                Pair("Platform Ticket Booking", "Instant platform entry token"),
                Pair("National Train Enquiry System (NTES)", "Live train delay and platform updates"),
                Pair("Integrated Railway Helpline (139)", "24x7 all-India passenger assistance"),
                Pair("Rail Madad App", "Grievance, security and medical emergency")
            )

            items(menuItems) { (title, subtitle) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(CircleShape).background(Color(0xFFEEF4FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Train, "train", tint = RailOneBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}
