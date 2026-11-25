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

data class SearchTrainItem(
    val trainNumber: String,
    val trainName: String,
    val trainType: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val runDays: String,
    val classes: List<Pair<String, Double>>, // Class code to Fare
    val availability: String
)

object MockRailwayService {

    fun searchTrainsBetweenStations(from: Station, to: Station): List<SearchTrainItem> {
        val distance = StationRepository.getDistanceKm(from.code, to.code)
        val slFare = (140 + (distance * 0.55).toInt() / 10 * 10).toDouble()
        val ac3Fare = (490 + (distance * 1.45).toInt() / 10 * 10).toDouble()
        val ac2Fare = (740 + (distance * 2.15).toInt() / 10 * 10).toDouble()
        val ccFare = (320 + (distance * 1.25).toInt() / 10 * 10).toDouble()

        return listOf(
            SearchTrainItem(
                trainNumber = "20906",
                trainName = "EKNR MAHAMANA SF",
                trainType = "SUPERFAST",
                departureTime = "06:15",
                arrivalTime = "07:22",
                duration = "01h 07m",
                runDays = "Daily",
                classes = listOf("2S" to 75.0, "SL" to slFare, "3A" to ac3Fare, "2A" to ac2Fare),
                availability = "AVL 42"
            ),
            SearchTrainItem(
                trainNumber = "12933",
                trainName = "KARNAVATI EXPRESS",
                trainType = "SUPERFAST",
                departureTime = "09:42",
                arrivalTime = "10:40",
                duration = "00h 58m",
                runDays = "Daily",
                classes = listOf("2S" to 75.0, "CC" to ccFare, "EC" to ac2Fare),
                availability = "AVL 18"
            ),
            SearchTrainItem(
                trainNumber = "19015",
                trainName = "SAURASHTRA EXP",
                trainType = "MAIL/EXP",
                departureTime = "14:20",
                arrivalTime = "15:48",
                duration = "01h 28m",
                runDays = "Daily",
                classes = listOf("SL" to slFare, "3A" to ac3Fare, "2A" to ac2Fare),
                availability = "AVL 64"
            ),
            SearchTrainItem(
                trainNumber = "20902",
                trainName = "VANDE BHARAT EXP",
                trainType = "VANDE BHARAT",
                departureTime = "17:55",
                arrivalTime = "18:38",
                duration = "00h 43m",
                runDays = "Except Wed",
                classes = listOf("CC" to ccFare + 120, "EC" to ac2Fare + 280),
                availability = "AVL 11"
            )
        )
    }

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
