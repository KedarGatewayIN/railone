package com.indianrailways.railone.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TicketType(val label: String) {
    JOURNEY("JOURNEY"),
    RETURN("RETURN")
}

enum class TrainType(val label: String) {
    ORDINARY("ORDINARY"),
    MAIL_EXPRESS("MAIL/EXPRESS"),
    SUPERFAST("SUPERFAST")
}

enum class TicketStatus(val label: String) {
    ACTIVE("ACTIVE"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED")
}

data class UnreservedTicket(
    val utsNumber: String,
    val sourceStation: Station,
    val destStation: Station,
    val via: String = "--------",
    val ticketType: TicketType = TicketType.JOURNEY,
    val trainType: TrainType = TrainType.SUPERFAST,
    val travelClass: String = "SECOND",
    val adultCount: Int = 1,
    val childCount: Int = 0,
    val distanceKm: Int,
    val fare: Double,
    val bookingTimestamp: Long = System.currentTimeMillis(),
    val validityHours: Int = 3,
    val status: TicketStatus = TicketStatus.ACTIVE,
    val irRegistrationCode: String = "IR:24AAAGM0289C1ZP",
    val passengerName: String = "Kedar Mitesh Vyas",
    val passengerMobile: String = "7227981091",
    val isPaperless: Boolean = true
) {
    val formattedBookingDateTime: String
        get() {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)
            return sdf.format(Date(bookingTimestamp))
        }

    val formattedBookingDateShort: String
        get() {
            val sdf = SimpleDateFormat("EEE, dd MMM yy", Locale.ENGLISH)
            return sdf.format(Date(bookingTimestamp))
        }

    val formattedFare: String
        get() = String.format(Locale.ENGLISH, "₹%.2f", fare)

    val passengerSummary: String
        get() = "$adultCount Adult, $childCount Child"

    /**
     * Authentic QR payload representing the encoded digital security token
     */
    val qrPayload: String
        get() = "UTS:$utsNumber|SRC:${sourceStation.code}|DST:${destStation.code}|KM:$distanceKm|FARE:$fare|DT:$bookingTimestamp|IR:$irRegistrationCode|PAX:$adultCount+$childCount|CLS:$travelClass|TRN:$trainType"
}

data class ReservedTicket(
    val pnr: String,
    val trainNumber: String,
    val trainName: String,
    val sourceStation: Station,
    val destStation: Station,
    val journeyDate: String,
    val travelDuration: String,
    val departureTime: String = "08:15",
    val arrivalTime: String = "22:30",
    val coach: String = "B3",
    val berth: String = "32 (MB)",
    val travelClass: String = "3A",
    val status: TicketStatus = TicketStatus.ACTIVE,
    val fare: Double = 1240.0,
    val passengerName: String = "Kedar Mitesh Vyas"
) {
    val formattedFare: String
        get() = String.format(Locale.ENGLISH, "₹%.2f", fare)
}

data class PlatformTicket(
    val id: String,
    val station: Station,
    val bookingTimestamp: Long = System.currentTimeMillis(),
    val passengerCount: Int = 1,
    val fare: Double = 10.0,
    val validityHours: Int = 2,
    val status: TicketStatus = TicketStatus.ACTIVE,
    val passengerName: String = "Kedar Mitesh Vyas"
) {
    val formattedBookingDateTime: String
        get() {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)
            return sdf.format(Date(bookingTimestamp))
        }

    val formattedFare: String
        get() = String.format(Locale.ENGLISH, "₹%.2f", fare)
}
