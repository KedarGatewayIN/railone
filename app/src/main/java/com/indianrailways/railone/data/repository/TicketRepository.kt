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
            // Seed initial realistic unreserved tickets: 1 Active, 1 Completed, 1 Cancelled
            val bh = StationRepository.getStationByCode("BH") ?: StationRepository.majorStations[0]
            val brc = StationRepository.getStationByCode("BRC") ?: StationRepository.majorStations[1]
            val st = StationRepository.getStationByCode("ST") ?: StationRepository.majorStations[3]
            val adi = StationRepository.getStationByCode("ADI") ?: StationRepository.majorStations[2]

            val activeTicket = UnreservedTicket(
                utsNumber = "X509EIA0BF",
                sourceStation = bh,
                destStation = brc,
                via = "--------",
                ticketType = TicketType.JOURNEY,
                trainType = TrainType.SUPERFAST,
                travelClass = "SECOND",
                adultCount = 1,
                childCount = 0,
                distanceKm = 71,
                fare = 60.0,
                bookingTimestamp = System.currentTimeMillis() - 1000 * 60 * 12,
                status = TicketStatus.ACTIVE
            )

            val completedTicket = UnreservedTicket(
                utsNumber = "C381KQZ7XA",
                sourceStation = st,
                destStation = bh,
                via = "--------",
                ticketType = TicketType.JOURNEY,
                trainType = TrainType.MAIL_EXPRESS,
                travelClass = "SECOND",
                adultCount = 1,
                childCount = 0,
                distanceKm = 59,
                fare = 45.0,
                bookingTimestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3, // 3 days ago
                status = TicketStatus.COMPLETED
            )

            val cancelledTicket = UnreservedTicket(
                utsNumber = "R821MOP91B",
                sourceStation = adi,
                destStation = brc,
                via = "--------",
                ticketType = TicketType.JOURNEY,
                trainType = TrainType.SUPERFAST,
                travelClass = "SECOND",
                adultCount = 1,
                childCount = 0,
                distanceKm = 100,
                fare = 65.0,
                bookingTimestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7, // 7 days ago
                status = TicketStatus.CANCELLED
            )

            mutableListOf(activeTicket, completedTicket, cancelledTicket)
        }

        val reserved: MutableList<ReservedTicket> = if (!reservedJson.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<ReservedTicket>>() {}.type
            gson.fromJson(reservedJson, type) ?: mutableListOf()
        } else {
            val jbp = StationRepository.getStationByCode("JBP") ?: StationRepository.majorStations[16]
            val bh = StationRepository.getStationByCode("BH") ?: StationRepository.majorStations[0]
            val gda = StationRepository.getStationByCode("GDA") ?: StationRepository.majorStations[17]
            val indb = StationRepository.getStationByCode("INDB") ?: StationRepository.majorStations[18]
            val knw = StationRepository.getStationByCode("KNW") ?: StationRepository.majorStations[19]
            val mmct = StationRepository.getStationByCode("MMCT") ?: StationRepository.majorStations[4]
            val ndls = StationRepository.getStationByCode("NDLS") ?: StationRepository.majorStations[6]

            mutableListOf(
                ReservedTicket(
                    pnr = "8756232802",
                    trainNumber = "20906",
                    trainName = "EKNR MAHAMANA",
                    sourceStation = jbp,
                    destStation = bh,
                    journeyDate = "Sun, 17 Jan 27",
                    travelDuration = "14h:15m",
                    status = TicketStatus.ACTIVE,
                    fare = 1420.0
                ),
                ReservedTicket(
                    pnr = "3003321393",
                    trainNumber = "19309",
                    trainName = "SHANTI EXPRESS",
                    sourceStation = gda,
                    destStation = indb,
                    journeyDate = "Wed, 13 Jan 27",
                    travelDuration = "7h:10m",
                    status = TicketStatus.ACTIVE,
                    fare = 780.0
                ),
                ReservedTicket(
                    pnr = "8256233956",
                    trainNumber = "12188",
                    trainName = "JBP GARIB RATH",
                    sourceStation = knw,
                    destStation = jbp,
                    journeyDate = "Thu, 14 Jan 27",
                    travelDuration = "6h:30m",
                    status = TicketStatus.ACTIVE,
                    fare = 910.0
                ),
                ReservedTicket(
                    pnr = "2648194012",
                    trainNumber = "12951",
                    trainName = "MUMBAI TEJAS RAJDHANI",
                    sourceStation = mmct,
                    destStation = ndls,
                    journeyDate = "Sat, 09 Jan 27",
                    travelDuration = "15h:32m",
                    status = TicketStatus.COMPLETED,
                    fare = 2850.0
                ),
                ReservedTicket(
                    pnr = "4193820194",
                    trainNumber = "12009",
                    trainName = "SHATABDI EXPRESS",
                    sourceStation = mmct,
                    destStation = bh,
                    journeyDate = "Tue, 05 Jan 27",
                    travelDuration = "4h:48m",
                    status = TicketStatus.CANCELLED,
                    fare = 1120.0
                )
            )
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

        persistUnreserved()
        persistReserved()
        persistPlatform()
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
