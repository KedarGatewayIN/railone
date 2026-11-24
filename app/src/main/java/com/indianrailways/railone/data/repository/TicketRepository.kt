package com.indianrailways.railone.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.indianrailways.railone.data.model.PlatformTicket
import com.indianrailways.railone.data.model.ReservedTicket
import com.indianrailways.railone.data.model.TicketStatus
import com.indianrailways.railone.data.model.TicketType
import com.indianrailways.railone.data.model.TrainType
import com.indianrailways.railone.data.model.UnreservedTicket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.SecureRandom
import java.util.Locale

class TicketRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("railone_ticket_store", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _unreservedTickets = MutableStateFlow<List<UnreservedTicket>>(emptyList())
    val unreservedTickets: StateFlow<List<UnreservedTicket>> = _unreservedTickets.asStateFlow()

    private val _reservedTickets = MutableStateFlow<List<ReservedTicket>>(emptyList())
    val reservedTickets: StateFlow<List<ReservedTicket>> = _reservedTickets.asStateFlow()

    private val _platformTickets = MutableStateFlow<List<PlatformTicket>>(emptyList())
    val platformTickets: StateFlow<List<PlatformTicket>> = _platformTickets.asStateFlow()

    init {
        loadAllTickets()
    }

    private fun loadAllTickets() {
        val unreservedJson = prefs.getString("unreserved_list", null)
        val reservedJson = prefs.getString("reserved_list", null)
        val platformJson = prefs.getString("platform_list", null)

        val unreserved: MutableList<UnreservedTicket> = if (!unreservedJson.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<UnreservedTicket>>() {}.type
            gson.fromJson(unreservedJson, type) ?: mutableListOf()
        } else {
            mutableListOf()
        }

        val reserved: MutableList<ReservedTicket> = if (!reservedJson.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<ReservedTicket>>() {}.type
            gson.fromJson(reservedJson, type) ?: mutableListOf()
        } else {
            mutableListOf()
        }

        val platform: MutableList<PlatformTicket> = if (!platformJson.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<PlatformTicket>>() {}.type
            gson.fromJson(platformJson, type) ?: mutableListOf()
        } else {
            mutableListOf()
        }

        _unreservedTickets.value = unreserved
        _reservedTickets.value = reserved
        _platformTickets.value = platform
    }

    private fun persistUnreserved() {
        prefs.edit().putString("unreserved_list", gson.toJson(_unreservedTickets.value)).apply()
    }

    private fun persistReserved() {
        prefs.edit().putString("reserved_list", gson.toJson(_reservedTickets.value)).apply()
    }

    private fun persistPlatform() {
        prefs.edit().putString("platform_list", gson.toJson(_platformTickets.value)).apply()
    }

    fun addUnreservedTicket(ticket: UnreservedTicket) {
        val current = _unreservedTickets.value.toMutableList()
        current.add(0, ticket) // Add newly booked ticket right to the top!
        _unreservedTickets.value = current
        persistUnreserved()
    }

    fun cancelUnreservedTicket(utsNumber: String) {
        val current = _unreservedTickets.value.map { ticket ->
            if (ticket.utsNumber.equals(utsNumber, ignoreCase = true)) {
                ticket.copy(status = TicketStatus.CANCELLED)
            } else {
                ticket
            }
        }
        _unreservedTickets.value = current
        persistUnreserved()
    }

    fun addPlatformTicket(ticket: PlatformTicket) {
        val current = _platformTickets.value.toMutableList()
        current.add(0, ticket)
        _platformTickets.value = current
        persistPlatform()
    }

    fun getUnreservedTicketByUts(uts: String): UnreservedTicket? {
        return _unreservedTickets.value.find { it.utsNumber.equals(uts, ignoreCase = true) }
    }

    fun generateRealisticUtsNumber(): String {
        val chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val random = SecureRandom()
        val prefix = "X" + (500 + random.nextInt(499)).toString()
        val suffix = (1..6)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
        return (prefix + suffix).take(10).uppercase(Locale.ENGLISH)
    }

    companion object {
        @Volatile
        private var INSTANCE: TicketRepository? = null

        fun getInstance(context: Context): TicketRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TicketRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
