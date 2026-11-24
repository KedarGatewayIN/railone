package com.indianrailways.railone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.RailOneApp
import com.indianrailways.railone.ui.theme.RailOneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val ticketRepository = TicketRepository.getInstance(applicationContext)

        setContent {
            RailOneTheme {
                RailOneApp(ticketRepository = ticketRepository)
            }
        }
    }
}
