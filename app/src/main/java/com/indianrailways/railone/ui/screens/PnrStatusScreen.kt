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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianrailways.railone.data.repository.MockRailwayService
import com.indianrailways.railone.data.repository.PnrResult
import com.indianrailways.railone.ui.components.DashedDivider
import com.indianrailways.railone.ui.theme.RailOneActiveGreen
import com.indianrailways.railone.ui.theme.RailOneActiveGreenBg
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneBlueDeep
import com.indianrailways.railone.ui.theme.RailOneSurface
import com.indianrailways.railone.ui.theme.TextPrimary
import com.indianrailways.railone.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PnrStatusScreen(onBack: () -> Unit) {
    var pnrInput by remember { mutableStateOf("8756232802") }
    var isLoading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<PnrResult?>(MockRailwayService.checkPnrStatus("8756232802")) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RailOneSurface)
    ) {
        // Top Bar
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
                    .size(38.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "PNR Enquiry",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Enter 10-Digit PNR Number",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = pnrInput,
                            onValueChange = { if (it.length <= 10) pnrInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("e.g. 8756232802") },
                            leadingIcon = {
                                Icon(Icons.Outlined.ConfirmationNumber, "PNR", tint = RailOneBlue)
                            },
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RailOneBlue,
                                unfocusedBorderColor = Color(0xFFD0D7DE)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    delay(600)
                                    result = MockRailwayService.checkPnrStatus(pnrInput)
                                    isLoading = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(23.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RailOneBlue)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Get Status", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            if (result != null) {
                val r = result!!
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("PNR: ${r.pnr}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RailOneBlueDeep)
                                    Text("${r.trainNumber} - ${r.trainName}", fontSize = 13.sp, color = TextSecondary)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = RailOneActiveGreenBg
                                ) {
                                    Text(
                                        r.chartStatus,
                                        color = RailOneActiveGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            DashedDivider()
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("From", fontSize = 11.sp, color = TextSecondary)
                                    Text(r.fromStation.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(r.fromStation.code, fontSize = 11.sp, color = RailOneBlue)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Class", fontSize = 11.sp, color = TextSecondary)
                                    Text(r.travelClass, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("To", fontSize = 11.sp, color = TextSecondary)
                                    Text(r.toStation.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(r.toStation.code, fontSize = 11.sp, color = RailOneBlue)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Passenger Details", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))

                            r.passengers.forEach { p ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF7F9FC)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Passenger ${p.number}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            Text("${p.coach} / Berth ${p.berth} (${p.berthType})", fontSize = 12.sp, color = TextSecondary)
                                        }
                                        Text(
                                            p.currentStatus,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RailOneActiveGreen
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
}
