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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Train
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
import com.indianrailways.railone.data.repository.TrainLiveStatus
import com.indianrailways.railone.ui.theme.RailOneActiveGreen
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneBlueDeep
import com.indianrailways.railone.ui.theme.RailOneSurface
import com.indianrailways.railone.ui.theme.TextMuted
import com.indianrailways.railone.ui.theme.TextPrimary
import com.indianrailways.railone.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LiveTrainStatusScreen(onBack: () -> Unit) {
    var trainNumberInput by remember { mutableStateOf("20906") }
    var isLoading by remember { mutableStateOf(false) }
    var statusResult by remember { mutableStateOf<TrainLiveStatus?>(MockRailwayService.getLiveTrainStatus("20906")) }
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
                text = "Live Train Status",
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
                            text = "Enter 5-Digit Train Number / Name",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = trainNumberInput,
                            onValueChange = { trainNumberInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("e.g. 20906 or EKNR MAHAMANA") },
                            leadingIcon = {
                                Icon(Icons.Default.Train, "Train", tint = RailOneBlue)
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
                                    delay(500)
                                    statusResult = MockRailwayService.getLiveTrainStatus(trainNumberInput)
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
                                Text("Track Train", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            if (statusResult != null) {
                val st = statusResult!!
                item {
                    // Running Summary Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F4FD))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${st.trainNumber} - ${st.trainName}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = RailOneBlueDeep
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "● ${st.runningStatus}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RailOneActiveGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = st.lastUpdated,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Route & Halts",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = RailOneBlueDeep
                    )
                }

                items(st.stations) { halt ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (halt.isDeparted) Color(0xFFF9FBFC) else Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (halt.isDeparted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "status",
                                tint = if (halt.isDeparted) RailOneActiveGreen else RailOneBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = halt.station.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${halt.station.code} • ${halt.platform}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = halt.actualArrival,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = halt.delayText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (halt.delayText.contains("Late")) Color(0xFFE65100) else RailOneActiveGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
