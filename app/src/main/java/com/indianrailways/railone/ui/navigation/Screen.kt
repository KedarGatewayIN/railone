package com.indianrailways.railone.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object UnreservedBooking : Screen("unreserved_booking")
    object UnreservedJourney : Screen("unreserved_journey/{fromCode}/{toCode}/{bookingMode}/{locationMode}") {
        fun createRoute(
            fromCode: String,
            toCode: String,
            bookingMode: String = "Normal",
            locationMode: String = "Outside Station"
        ): String {
            return "unreserved_journey/${Uri.encode(fromCode)}/${Uri.encode(toCode)}/${Uri.encode(bookingMode)}/${Uri.encode(locationMode)}"
        }
    }
    object DigitalTicket : Screen("digital_ticket/{utsNumber}") {
        fun createRoute(utsNumber: String) = "digital_ticket/$utsNumber"
    }
    object MyBookings : Screen("my_bookings")
    object ReservedBooking : Screen("reserved_booking")
    object PlatformBooking : Screen("platform_booking")
    object PnrStatus : Screen("pnr_status")
    object LiveTrainStatus : Screen("live_train_status")
    object CoachPosition : Screen("coach_position")
    object OrderFood : Screen("order_food")
    object RailMadad : Screen("rail_madad")
    object FileRefund : Screen("file_refund")
    object YouProfile : Screen("you_profile")
    object Menu : Screen("menu")
    object Notifications : Screen("notifications")
}
