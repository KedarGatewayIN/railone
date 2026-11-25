package com.indianrailways.railone.data.repository

import com.indianrailways.railone.data.model.TicketType
import com.indianrailways.railone.data.model.TrainType
import kotlin.math.ceil
import kotlin.math.max

object FareCalculator {

    fun calculateFare(
        distanceKm: Int,
        trainType: TrainType,
        ticketType: TicketType,
        adults: Int,
        children: Int
    ): Double {
        if (adults <= 0 && children <= 0) return 0.0

        // Base 2nd Class Ordinary slab (calibrated so 71km BH-BRC matches official ₹45 Mail/Exp & ₹60 SF)
        val baseOrdinaryFare: Double = when {
            distanceKm <= 20 -> 10.0
            distanceKm <= 45 -> 15.0
            distanceKm <= 60 -> 25.0
            distanceKm <= 80 -> 30.0
            distanceKm <= 110 -> 35.0
            distanceKm <= 160 -> 45.0
            distanceKm <= 220 -> 60.0
            distanceKm <= 320 -> 85.0
            distanceKm <= 500 -> 125.0
            else -> 125.0 + (distanceKm - 500) * 0.20
        }

        // Apply train class multiplier / surcharges
        val singleAdultFare: Double = when (trainType) {
            TrainType.ORDINARY -> baseOrdinaryFare
            TrainType.MAIL_EXPRESS -> {
                val raw = max(30.0, baseOrdinaryFare * 1.5)
                roundToNearestFive(raw)
            }
            TrainType.SUPERFAST -> {
                val mailExp = max(30.0, baseOrdinaryFare * 1.5)
                roundToNearestFive(mailExp) + 15.0
            }
        }

        val singleChildFare: Double = if (children > 0) {
            roundToNearestFive(singleAdultFare * 0.5)
        } else 0.0

        val totalOneWay = (singleAdultFare * adults) + (singleChildFare * children)

        val totalFare = when (ticketType) {
            TicketType.JOURNEY -> totalOneWay
            TicketType.RETURN -> totalOneWay * 2.0
        }

        return totalFare
    }

    private fun roundToNearestFive(amount: Double): Double {
        return (ceil(amount / 5.0) * 5.0)
    }
}
