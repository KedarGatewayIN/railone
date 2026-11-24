package com.indianrailways.railone.data.repository

import com.indianrailways.railone.data.model.Station

data class PnrResult(
    val pnr: String,
    val trainNumber: String,
    val trainName: String,
    val journeyDate: String,
    val fromStation: Station,
    val toStation: Station,
    val boardingStation: Station,
    val travelClass: String,
    val chartStatus: String,
    val passengers: List<PassengerStatus>
)

data class PassengerStatus(
    val number: Int,
    val bookingStatus: String,
    val currentStatus: String,
    val coach: String,
    val berth: String,
    val berthType: String
)

data class TrainLiveStatus(
    val trainNumber: String,
    val trainName: String,
    val runningStatus: String,
    val delayMinutes: Int,
    val currentStation: String,
    val lastUpdated: String,
    val stations: List<StationHalt>
)

data class StationHalt(
    val station: Station,
    val scheduledArrival: String,
    val scheduledDeparture: String,
    val actualArrival: String,
    val actualDeparture: String,
    val delayText: String,
    val platform: String,
    val isDeparted: Boolean
)

object MockRailwayService {

    fun checkPnrStatus(pnr: String): PnrResult {
        val cleanPnr = if (pnr.length >= 10) pnr.take(10) else "8756232802"
        val lastDigit = cleanPnr.last().digitToIntOrNull() ?: 2

        val (trainNo, trainName, src, dst) = when (lastDigit % 3) {
            0 -> Quad("20906", "EKNR MAHAMANA EX", StationRepository.getStationByCode("JBP")!!, StationRepository.getStationByCode("BH")!!)
            1 -> Quad("12952", "MUMBAI TEJAS RAJ", StationRepository.getStationByCode("NDLS")!!, StationRepository.getStationByCode("MMCT")!!)
            else -> Quad("19309", "SHANTI EXPRESS", StationRepository.getStationByCode("GDA")!!, StationRepository.getStationByCode("INDB")!!)
        }

        val passengers = listOf(
            PassengerStatus(
                number = 1,
                bookingStatus = "CNF / B3 / 32 / MB",
                currentStatus = "CNF (Confirmed)",
                coach = "B3",
                berth = "32",
                berthType = "Middle Berth"
            ),
            PassengerStatus(
                number = 2,
                bookingStatus = "CNF / B3 / 33 / LB",
                currentStatus = "CNF (Confirmed)",
                coach = "B3",
                berth = "33",
                berthType = "Lower Berth"
            )
        )

        return PnrResult(
            pnr = cleanPnr,
            trainNumber = trainNo,
            trainName = trainName,
            journeyDate = "27 Nov 2026",
            fromStation = src,
            toStation = dst,
            boardingStation = src,
            travelClass = "3rd AC (3A)",
            chartStatus = "CHART PREPARED",
            passengers = passengers
        )
    }

    fun getLiveTrainStatus(trainNumber: String): TrainLiveStatus {
        val cleanTrain = if (trainNumber.isNotBlank()) trainNumber.trim() else "20906"
        val halts = listOf(
            StationHalt(StationRepository.getStationByCode("JBP")!!, "08:15", "08:20", "08:15", "08:20", "Right Time", "PF 1", true),
            StationHalt(StationRepository.getStationByCode("KNW")!!, "14:45", "14:50", "14:52", "14:57", "Late 7m", "PF 2", true),
            StationHalt(StationRepository.getStationByCode("BSL")!!, "17:10", "17:15", "17:14", "17:19", "Late 4m", "PF 3", true),
            StationHalt(StationRepository.getStationByCode("ST")!!, "21:30", "21:35", "21:30", "21:35", "On Time", "PF 1", false),
            StationHalt(StationRepository.getStationByCode("BH")!!, "22:30", "22:32", "22:30", "22:32", "On Time", "PF 2", false),
            StationHalt(StationRepository.getStationByCode("BRC")!!, "23:45", "Destination", "23:45", "Destination", "Expected On Time", "PF 4", false)
        )

        return TrainLiveStatus(
            trainNumber = cleanTrain,
            trainName = "EKNR MAHAMANA SUPERFAST",
            runningStatus = "Running on time near Surat (ST)",
            delayMinutes = 0,
            currentStation = "Surat (ST) - Platform 1",
            lastUpdated = "Updated 2 mins ago",
            stations = halts
        )
    }

    private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
