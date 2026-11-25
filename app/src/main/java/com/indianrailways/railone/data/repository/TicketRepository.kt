package com.indianrailways.railone.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.indianrailways.railone.data.db.RailOneDatabaseHelper
import com.indianrailways.railone.data.model.AppNotification
import com.indianrailways.railone.data.model.PlatformTicket
import com.indianrailways.railone.data.model.ReservedTicket
import com.indianrailways.railone.data.model.TicketStatus
import com.indianrailways.railone.data.model.UnreservedTicket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.SecureRandom
import java.util.Locale

class TicketRepository(context: Context) {
    private val dbHelper = RailOneDatabaseHelper(context)
    private val prefs: SharedPreferences =
        context.getSharedPreferences("railone_ticket_store", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _unreservedTickets = MutableStateFlow<List<UnreservedTicket>>(emptyList())
    val unreservedTickets: StateFlow<List<UnreservedTicket>> = _unreservedTickets.asStateFlow()

    private val _reservedTickets = MutableStateFlow<List<ReservedTicket>>(emptyList())
    val reservedTickets: StateFlow<List<ReservedTicket>> = _reservedTickets.asStateFlow()

    private val _platformTickets = MutableStateFlow<List<PlatformTicket>>(emptyList())
    val platformTickets: StateFlow<List<PlatformTicket>> = _platformTickets.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val recentSearches: StateFlow<List<Pair<String, String>>> = _recentSearches.asStateFlow()

    init {
        migrateLegacySharedPrefsIfNeeded()
        loadAllFromSqlite()
        loadRecentSearches()
    }

    private fun migrateLegacySharedPrefsIfNeeded() {
        val migrated = prefs.getBoolean("sqlite_migrated_v1", false)
        if (migrated) return

        val unreservedJson = prefs.getString("unreserved_list", null)
        if (!unreservedJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<UnreservedTicket>>() {}.type
            val list: List<UnreservedTicket> = runCatching {
                gson.fromJson<List<UnreservedTicket>>(unreservedJson, type)
            }.getOrNull().orEmpty()
            list.forEach { dbHelper.upsertUnreservedTicket(it) }
        }

        val reservedJson = prefs.getString("reserved_list", null)
        if (!reservedJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<ReservedTicket>>() {}.type
            val list: List<ReservedTicket> = runCatching {
                gson.fromJson<List<ReservedTicket>>(reservedJson, type)
            }.getOrNull().orEmpty()
            list.forEach { dbHelper.upsertReservedTicket(it) }
        }

        val platformJson = prefs.getString("platform_list", null)
        if (!platformJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<PlatformTicket>>() {}.type
            val list: List<PlatformTicket> = runCatching {
                gson.fromJson<List<PlatformTicket>>(platformJson, type)
            }.getOrNull().orEmpty()
            list.forEach { dbHelper.upsertPlatformTicket(it) }
        }

        prefs.edit().putBoolean("sqlite_migrated_v1", true).apply()
    }

    private fun loadAllFromSqlite() {
        _unreservedTickets.value = dbHelper.getAllUnreservedTickets()
        _reservedTickets.value = dbHelper.getAllReservedTickets()
        _platformTickets.value = dbHelper.getAllPlatformTickets()
        _notifications.value = dbHelper.getAllNotifications()
    }

    private fun loadRecentSearches() {
        val json = prefs.getString("recent_searches", null)
        val loaded: List<Pair<String, String>> = if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<List<String>>>() {}.type
            val raw: List<List<String>> = runCatching { gson.fromJson<List<List<String>>>(json, type) }
                .getOrNull().orEmpty()
            raw.mapNotNull { pair -> if (pair.size == 2) pair[0] to pair[1] else null }
        } else {
            emptyList()
        }
        _recentSearches.value = if (loaded.isNotEmpty()) {
            loaded
        } else {
            listOf(
                "BH" to "BRC",
                "BRC" to "BH"
            )
        }
    }

    fun recordRecentSearch(fromCode: String, toCode: String) {
        val pair = fromCode.uppercase(Locale.ENGLISH) to toCode.uppercase(Locale.ENGLISH)
        val updated = listOf(pair) + _recentSearches.value.filterNot { it == pair }
        val trimmed = updated.take(6)
        _recentSearches.value = trimmed
        prefs.edit()
            .putString("recent_searches", gson.toJson(trimmed.map { listOf(it.first, it.second) }))
            .apply()
    }

    fun addUnreservedTicket(ticket: UnreservedTicket) {
        dbHelper.upsertUnreservedTicket(ticket)
        _unreservedTickets.value = dbHelper.getAllUnreservedTickets()
        recordRecentSearch(ticket.sourceStation.code, ticket.destStation.code)

        val refId = generateNumericRefId()
        addNotification(
            title = "RailOne",
            message = "Unreserved Ticket with Reference ID $refId (UTS ${ticket.utsNumber}) is booked. Please check My Bookings to get ticket details. This message does not provide authorisation to travel.IR-CRIS"
        )
        addNotification(
            title = "RailOne",
            message = "Your ticket has been booked successfully. You can view it in the \"My Booking\" section. If the ticket is not visible please swipe down or tap the refresh icon at the top. Please do not perform a factory reset or change your handset while the ticket is still valid on your phone."
        )
    }

    fun cancelUnreservedTicket(utsNumber: String) {
        val existing = _unreservedTickets.value.find { it.utsNumber.equals(utsNumber, ignoreCase = true) }
        if (existing != null) {
            dbHelper.upsertUnreservedTicket(existing.copy(status = TicketStatus.CANCELLED))
            _unreservedTickets.value = dbHelper.getAllUnreservedTickets()
        }
    }

    fun addReservedTicket(ticket: ReservedTicket) {
        dbHelper.upsertReservedTicket(ticket)
        _reservedTickets.value = dbHelper.getAllReservedTickets()
        addNotification(
            title = "RailOne",
            message = "Reserved Ticket PNR ${ticket.pnr} (${ticket.trainNumber} ${ticket.trainName}) is confirmed in ${ticket.coach}-${ticket.berth}. Happy Journey!"
        )
    }

    fun cancelReservedTicket(pnr: String) {
        val existing = _reservedTickets.value.find { it.pnr.equals(pnr, ignoreCase = true) }
        if (existing != null) {
            dbHelper.upsertReservedTicket(existing.copy(status = TicketStatus.CANCELLED))
            _reservedTickets.value = dbHelper.getAllReservedTickets()
        }
    }

    fun addPlatformTicket(ticket: PlatformTicket) {
        dbHelper.upsertPlatformTicket(ticket)
        _platformTickets.value = dbHelper.getAllPlatformTickets()
        addNotification(
            title = "RailOne",
            message = "Platform Ticket ${ticket.id} for ${ticket.station.name} (${ticket.passengerCount} person) booked successfully. Valid for 2 hours."
        )
    }

    fun addNotification(title: String, message: String) {
        val notif = AppNotification(
            id = "notif_${System.nanoTime()}",
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        dbHelper.insertNotification(notif)
        _notifications.value = dbHelper.getAllNotifications()
    }

    fun markAllNotificationsRead() {
        dbHelper.markAllNotificationsRead()
        _notifications.value = dbHelper.getAllNotifications()
    }

    fun getUnreservedTicketByUts(uts: String): UnreservedTicket? {
        return _unreservedTickets.value.find { it.utsNumber.equals(uts, ignoreCase = true) }
    }

    fun refreshFromDatabase() {
        loadAllFromSqlite()
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

    fun generateRealisticPnr(): String {
        val random = SecureRandom()
        val first = 2 + random.nextInt(7)
        val rest = (1..9).map { random.nextInt(10) }.joinToString("")
        return "$first$rest"
    }

    private fun generateNumericRefId(): String {
        val random = SecureRandom()
        return (1..15).map { random.nextInt(10) }.joinToString("")
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
