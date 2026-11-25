package com.indianrailways.railone

import com.indianrailways.railone.data.model.TicketType
import com.indianrailways.railone.data.model.TrainType
import com.indianrailways.railone.data.repository.FareCalculator
import com.indianrailways.railone.data.repository.MockRailwayService
import com.indianrailways.railone.data.repository.StationRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RailOneUnitTest {

    @Test
    fun testFareCalculation_BharuchToVadodaraSuperfast() {
        // Distance between Bharuch (BH) and Vadodara (BRC) is 71 km
        val distance = StationRepository.getDistanceKm("BH", "BRC")
        assertEquals(71, distance)

        // 1 adult, 0 child, Mail/Exp single journey should be ₹45.00
        val mailExpFare = FareCalculator.calculateFare(
            distanceKm = distance,
            trainType = TrainType.MAIL_EXPRESS,
            ticketType = TicketType.JOURNEY,
            adults = 1,
            children = 0
        )
        assertEquals(45.0, mailExpFare, 0.01)

        // 1 adult, 0 child, Superfast single journey should be ₹60.00
        val fare = FareCalculator.calculateFare(
            distanceKm = distance,
            trainType = TrainType.SUPERFAST,
            ticketType = TicketType.JOURNEY,
            adults = 1,
            children = 0
        )
        assertEquals(60.0, fare, 0.01)
    }

    @Test
    fun testStationSearch() {
        val results = StationRepository.searchStations("Ahmedabad")
        assertTrue(results.isNotEmpty())
        assertEquals("ADI", results.first().code)

        val codeResult = StationRepository.getStationByCode("NDLS")
        assertNotNull(codeResult)
        assertEquals("NEW DELHI", codeResult?.name)
    }

    @Test
    fun testPnrStatusLookup() {
        val result = MockRailwayService.checkPnrStatus("8756232802")
        assertEquals("8756232802", result.pnr)
        assertEquals("19309", result.trainNumber)
        assertEquals("CHART PREPARED", result.chartStatus)
        assertTrue(result.passengers.isNotEmpty())
    }

    @Test
    fun testLiveTrainStatus() {
        val status = MockRailwayService.getLiveTrainStatus("20906")
        assertEquals("20906", status.trainNumber)
        assertTrue(status.stations.isNotEmpty())
    }

    @Test
    fun testFareCalculation_ReturnAndMailExp() {
        val distance = StationRepository.getDistanceKm("BH", "BRC")
        val returnOrdinary = FareCalculator.calculateFare(
            distanceKm = distance,
            trainType = TrainType.ORDINARY,
            ticketType = TicketType.RETURN,
            adults = 2,
            children = 1
        )
        // Base ordinary for 71km is 30.0; child is 15.0; one-way = 30*2 + 15 = 75; return = 150.0
        assertEquals(150.0, returnOrdinary, 0.01)
    }

    @Test
    fun testReservedTrainSearch() {
        val bh = StationRepository.getStationByCode("BH")!!
        val brc = StationRepository.getStationByCode("BRC")!!
        val trains = MockRailwayService.searchTrainsBetweenStations(bh, brc)
        assertTrue(trains.isNotEmpty())
        assertTrue(trains.all { it.classes.isNotEmpty() })
    }
}
