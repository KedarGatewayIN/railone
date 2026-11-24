package com.indianrailways.railone.data.repository

import com.indianrailways.railone.data.model.Station
import kotlin.math.abs

object StationRepository {
    val majorStations = listOf(
        Station("BH", "BHARUCH JN.", "Gujarat", "WR"),
        Station("BRC", "VADODARA JN.", "Gujarat", "WR"),
        Station("ADI", "AHMEDABAD JN.", "Gujarat", "WR"),
        Station("ST", "SURAT", "Gujarat", "WR"),
        Station("MMCT", "MUMBAI CENTRAL", "Maharashtra", "WR"),
        Station("CSMT", "MUMBAI CSMT", "Maharashtra", "CR"),
        Station("NDLS", "NEW DELHI", "Delhi", "NR"),
        Station("DLI", "OLD DELHI", "Delhi", "NR"),
        Station("NZM", "HZ NIZAMUDDIN", "Delhi", "NR"),
        Station("ANVT", "ANAND VIHAR TRM", "Delhi", "NR"),
        Station("JP", "JAIPUR JN.", "Rajasthan", "NWR"),
        Station("PUNE", "PUNE JN.", "Maharashtra", "CR"),
        Station("SBC", "KSR BENGALURU", "Karnataka", "SWR"),
        Station("HWH", "HOWRAH JN.", "West Bengal", "ER"),
        Station("SDAH", "SEALDAH", "West Bengal", "ER"),
        Station("MAS", "MGR CHENNAI CENTRAL", "Tamil Nadu", "SR"),
        Station("JBP", "JABALPUR", "Madhya Pradesh", "WCR"),
        Station("GDA", "GODHRA JN.", "Gujarat", "WR"),
        Station("INDB", "INDORE JN.", "Madhya Pradesh", "WR"),
        Station("KNW", "KHANDWA", "Madhya Pradesh", "CR"),
        Station("BPL", "BHOPAL JN.", "Madhya Pradesh", "WCR"),
        Station("RKMP", "RANI KAMALAPATI", "Madhya Pradesh", "WCR"),
        Station("GWL", "GWALIOR JN.", "Madhya Pradesh", "NCR"),
        Station("AGC", "AGRA CANTT", "Uttar Pradesh", "NCR"),
        Station("JHS", "V LAKSHMIBAI JHANSI", "Uttar Pradesh", "NCR"),
        Station("CNB", "KANPUR CENTRAL", "Uttar Pradesh", "NCR"),
        Station("LKO", "LUCKNOW CHARBAGH", "Uttar Pradesh", "NR"),
        Station("BSB", "VARANASI JN.", "Uttar Pradesh", "NR"),
        Station("GKP", "GORAKHPUR JN.", "Uttar Pradesh", "NER"),
        Station("PNBE", "PATNA JN.", "Bihar", "ECR"),
        Station("KOTA", "KOTA JN.", "Rajasthan", "WCR"),
        Station("RTM", "RATLAM JN.", "Madhya Pradesh", "WR"),
        Station("UJN", "UJJAIN JN.", "Madhya Pradesh", "WR"),
        Station("BSL", "BHUSAWAL JN.", "Maharashtra", "CR"),
        Station("NGP", "NAGPUR JN.", "Maharashtra", "CR"),
        Station("HYB", "HYDERABAD DECCAN", "Telangana", "SCR"),
        Station("SC", "SECUNDERABAD JN.", "Telangana", "SCR"),
        Station("CDG", "CHANDIGARH", "Punjab", "NR"),
        Station("SVDK", "SMVD KATRA", "Jammu & Kashmir", "NR"),
        Station("GHY", "GUWAHATI", "Assam", "NFR")
    )

    // Curated real-world route distances for maximum demo realism
    private val distanceMap = mapOf(
        ("BH" to "BRC") to 71,
        ("BRC" to "BH") to 71,
        ("BH" to "ST") to 59,
        ("ST" to "BH") to 59,
        ("BRC" to "ADI") to 100,
        ("ADI" to "BRC") to 100,
        ("ADI" to "ST") to 230,
        ("ST" to "ADI") to 230,
        ("BH" to "ADI") to 171,
        ("ADI" to "BH") to 171,
        ("MMCT" to "ST") to 263,
        ("ST" to "MMCT") to 263,
        ("MMCT" to "BRC") to 392,
        ("BRC" to "MMCT") to 392,
        ("MMCT" to "ADI") to 492,
        ("ADI" to "MMCT") to 492,
        ("NDLS" to "JP") to 308,
        ("JP" to "NDLS") to 308,
        ("NDLS" to "CNB") to 435,
        ("CNB" to "NDLS") to 435,
        ("NDLS" to "AGC") to 195,
        ("AGC" to "NDLS") to 195,
        ("GDA" to "INDB") to 315,
        ("INDB" to "GDA") to 315,
        ("JBP" to "BH") to 890,
        ("BH" to "JBP") to 890,
        ("KNW" to "JBP") to 434,
        ("JBP" to "KNW") to 434,
        ("CSMT" to "PUNE") to 192,
        ("PUNE" to "CSMT") to 192,
        ("SBC" to "MAS") to 359,
        ("MAS" to "SBC") to 359
    )

    fun getStationByCode(code: String): Station? {
        return majorStations.find { it.code.equals(code, ignoreCase = true) }
    }

    fun searchStations(query: String): List<Station> {
        if (query.isBlank()) return majorStations
        val cleanQuery = query.trim().lowercase()
        return majorStations.filter {
            it.code.lowercase().contains(cleanQuery) ||
                    it.name.lowercase().contains(cleanQuery) ||
                    it.state.lowercase().contains(cleanQuery)
        }
    }

    fun getDistanceKm(sourceCode: String, destCode: String): Int {
        if (sourceCode.equals(destCode, ignoreCase = true)) return 10
        val direct = distanceMap[sourceCode.uppercase() to destCode.uppercase()]
        if (direct != null) return direct

        // Realistic deterministic distance estimation if pair not explicitly in table
        val hash = abs((sourceCode.hashCode() * 31) xor destCode.hashCode())
        return 45 + (hash % 280) // 45km - 325km realistic intercity unreserved range
    }
}
