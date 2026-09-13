package com.example.model

data class CarDashboardState(
    val isEngineOn: Boolean = true,
    val speedKmh: Int = 0,
    val gear: String = "P",
    val batteryPercent: Int = 84,
    val fuelRangeKm: Int = 432,
    val totalRadars: Int = 0,
    val nearbyRadars: Int = 0,
    val locationName: String = "Al Hawtah",
    val locationArabic: String = "جابر بن حيان",
    val subDistrict: String = "Hautat Sudair",
    val temperatureC: Int = 34,
    val weatherCondition: String = "Sunny",
    val weatherDate: String = "Saturday • Sep 12",
    val weatherLocation: String = "Tumayr\nSA",
    val activeMusicSource: String = "Spotify",
    val currentSongTitle: String = "Blinding Lights",
    val currentSongArtist: String = "The Weeknd",
    val isPlayingMusic: Boolean = true
)

data class RepairDiagnosis(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val symptom: String,
    val userDescription: String,
    val imageUri: String? = null,
    val issueTitle: String,
    val severity: IssueSeverity,
    val estimatedSavings: String,
    val estimatedRepairTime: String,
    val rootCause: String,
    val stepByStepGuide: List<String>,
    val toolsNeeded: List<String>,
    val safetyWarning: String
)

enum class IssueSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

data class MusicAppItem(
    val id: String,
    val name: String,
    val iconColor: Long,
    val symbol: String
)
