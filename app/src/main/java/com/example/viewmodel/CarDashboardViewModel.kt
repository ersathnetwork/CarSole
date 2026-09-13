package com.example.viewmodel

import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GeminiRepairService
import com.example.model.CarDashboardState
import com.example.model.IssueSeverity
import com.example.model.RepairDiagnosis
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

enum class ActiveTab {
    DRIVE_MODE,
    AI_REPAIR
}

enum class ActiveScreen {
    MAIN_DASHBOARD,
    SETTINGS,
    ACTIVE_DRIVING_HUD
}

class CarDashboardViewModel(
    private val repairService: GeminiRepairService = GeminiRepairService()
) : ViewModel() {

    private val _carState = MutableStateFlow(CarDashboardState())
    val carState: StateFlow<CarDashboardState> = _carState.asStateFlow()

    private val _activeTab = MutableStateFlow(ActiveTab.DRIVE_MODE)
    val activeTab: StateFlow<ActiveTab> = _activeTab.asStateFlow()

    private val _activeScreen = MutableStateFlow(ActiveScreen.MAIN_DASHBOARD)
    val activeScreen: StateFlow<ActiveScreen> = _activeScreen.asStateFlow()

    private val _showGoProSheet = MutableStateFlow(false)
    val showGoProSheet: StateFlow<Boolean> = _showGoProSheet.asStateFlow()

    private val _showExitDialog = MutableStateFlow(false)
    val showExitDialog: StateFlow<Boolean> = _showExitDialog.asStateFlow()

    private val _showHistoryDialog = MutableStateFlow(false)
    val showHistoryDialog: StateFlow<Boolean> = _showHistoryDialog.asStateFlow()

    // AI Repair State
    private val _problemDescription = MutableStateFlow("")
    val problemDescription: StateFlow<String> = _problemDescription.asStateFlow()

    private val _selectedSymptom = MutableStateFlow<String?>(null)
    val selectedSymptom: StateFlow<String?> = _selectedSymptom.asStateFlow()

    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _currentDiagnosis = MutableStateFlow<RepairDiagnosis?>(null)
    val currentDiagnosis: StateFlow<RepairDiagnosis?> = _currentDiagnosis.asStateFlow()

    private val _diagnosisHistory = MutableStateFlow<List<RepairDiagnosis>>(
        listOf(
            RepairDiagnosis(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis() - 86400000L * 2,
                symptom = "Brake Noise",
                userDescription = "Squealing noise from front right brake when slowing down.",
                issueTitle = "Glazed Brake Pads & Minor Rotor Scoring",
                severity = IssueSeverity.MEDIUM,
                estimatedSavings = "$285",
                estimatedRepairTime = "1.5 Hours",
                rootCause = "Overheated brake pad surface causing friction glaze.",
                stepByStepGuide = listOf("Inspect pads", "Replace with ceramic", "Bed-in rotors"),
                toolsNeeded = listOf("Jack", "14mm wrench", "C-clamp"),
                safetyWarning = "Do not touch hot brake rotors directly."
            )
        )
    )
    val diagnosisHistory: StateFlow<List<RepairDiagnosis>> = _diagnosisHistory.asStateFlow()

    // GoPro options
    private val _selectedPlan = MutableStateFlow("Weekly") // "Weekly" or "Yearly"
    val selectedPlan: StateFlow<String> = _selectedPlan.asStateFlow()

    private val _isProUnlocked = MutableStateFlow(false)
    val isProUnlocked: StateFlow<Boolean> = _isProUnlocked.asStateFlow()

    // Driving HUD simulation state
    private var drivingSimulationJob: Job? = null
    private val _hudSpeed = MutableStateFlow(0)
    val hudSpeed: StateFlow<Int> = _hudSpeed.asStateFlow()

    private val _hudRpm = MutableStateFlow(900)
    val hudRpm: StateFlow<Int> = _hudRpm.asStateFlow()

    private val _hudGear = MutableStateFlow("D")
    val hudGear: StateFlow<String> = _hudGear.asStateFlow()

    private val _tripDistance = MutableStateFlow(0.0f)
    val tripDistance: StateFlow<Float> = _tripDistance.asStateFlow()

    // True once real GPS fixes are flowing in via onGpsLocation(). Until then
    // (no location permission, indoors, cold GPS fix, etc.) we fall back to
    // the scripted simulation so the HUD never just sits frozen at 0.
    private var isReceivingRealGps = false
    private var lastGpsLocation: Location? = null

    fun switchTab(tab: ActiveTab) {
        _activeTab.value = tab
    }

    fun openSettings() {
        _activeScreen.value = ActiveScreen.SETTINGS
    }

    fun closeSettings() {
        _activeScreen.value = ActiveScreen.MAIN_DASHBOARD
    }

    fun openGoPro() {
        _showGoProSheet.value = true
    }

    fun closeGoPro() {
        _showGoProSheet.value = false
    }

    fun openExitDialog() {
        _showExitDialog.value = true
    }

    fun closeExitDialog() {
        _showExitDialog.value = false
    }

    fun openHistoryDialog() {
        _showHistoryDialog.value = true
    }

    fun closeHistoryDialog() {
        _showHistoryDialog.value = false
    }

    fun selectPlan(plan: String) {
        _selectedPlan.value = plan
    }

    fun activatePro() {
        _isProUnlocked.value = true
        _showGoProSheet.value = false
    }

    // AI Repair actions
    fun setProblemDescription(text: String) {
        if (text.length <= 500) {
            _problemDescription.value = text
        }
    }

    fun selectSymptom(symptom: String) {
        if (_selectedSymptom.value == symptom) {
            _selectedSymptom.value = null
        } else {
            _selectedSymptom.value = symptom
            if (_problemDescription.value.isBlank()) {
                _problemDescription.value = when (symptom) {
                    "Brake Noise" -> "When braking above 40 mph, I hear a metallic noise from the rear left wheel."
                    "Engine Light" -> "Check engine light came on solid after highway driving, slight engine shudder."
                    "Battery" -> "Vehicle cranks slowly in the morning, battery voltage indicator drops."
                    "Tire Wear" -> "Uneven feathering wear on outside edge of front tires, steering pulls left."
                    "Overheating" -> "Radiator fan running at max speed, coolant temperature gauge spikes at red lights."
                    else -> "$symptom issue observed while driving."
                }
            }
        }
    }

    fun setPhoto(bitmap: Bitmap?) {
        _selectedBitmap.value = bitmap
    }

    fun clearPhoto() {
        _selectedBitmap.value = null
    }

    fun clearProblemDescription() {
        _problemDescription.value = ""
        _selectedSymptom.value = null
    }

    fun startAiAnalysis() {
        if (_isAnalyzing.value) return
        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                val diagnosis = repairService.diagnoseVehicleIssue(
                    description = _problemDescription.value.ifBlank { "Vehicle inspection diagnosis request" },
                    category = _selectedSymptom.value ?: "General Diagnosis",
                    bitmap = _selectedBitmap.value
                )
                _currentDiagnosis.value = diagnosis
                _diagnosisHistory.update { listOf(diagnosis) + it }
            } catch (e: Exception) {
                // Handled gracefully in service
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun dismissDiagnosis() {
        _currentDiagnosis.value = null
    }

    // Music actions
    fun selectMusicApp(appName: String) {
        _carState.update {
            it.copy(
                activeMusicSource = appName,
                isPlayingMusic = true
            )
        }
    }

    fun toggleMusicPlayback() {
        _carState.update { it.copy(isPlayingMusic = !it.isPlayingMusic) }
    }

    // Driving HUD actions
    fun startDriving() {
        _activeScreen.value = ActiveScreen.ACTIVE_DRIVING_HUD
        _hudSpeed.value = 0
        _hudGear.value = "D"
        _tripDistance.value = 0.0f
        startDrivingSimulation()
    }

    fun stopDriving() {
        drivingSimulationJob?.cancel()
        drivingSimulationJob = null
        _hudSpeed.value = 0
        _hudGear.value = "P"
        _activeScreen.value = ActiveScreen.MAIN_DASHBOARD
    }

    /**
     * Feed a real GPS fix into the HUD. Call this from MainActivity's
     * FusedLocationProviderClient/LocationManager callback once location
     * permission is granted. Speed comes straight from the GPS chip
     * (location.speed, m/s) and trip distance accumulates from the actual
     * distance between fixes — this is what makes the HUD reflect the real
     * car instead of a scripted animation.
     */
    fun onGpsLocation(location: Location) {
        isReceivingRealGps = true
        val speedKmh = (location.speed * 3.6f).toInt().coerceIn(0, 260)
        _hudSpeed.value = speedKmh
        // No OBD-II/vehicle-bus connection on most aftermarket head units, so
        // RPM can't be read for real — approximate it from road speed in top
        // gear just to keep the tachometer moving in step with the car.
        _hudRpm.value = (900 + speedKmh * 28).coerceIn(800, 6200)
        _hudGear.value = if (speedKmh == 0) "P" else "D"

        lastGpsLocation?.let { previous ->
            val meters = previous.distanceTo(location)
            _tripDistance.update { it + meters / 1000f }
        }
        lastGpsLocation = location
    }

    /** Call when GPS is unavailable/permission denied so the fallback simulation kicks in. */
    fun onGpsUnavailable() {
        isReceivingRealGps = false
    }

    private fun startDrivingSimulation() {
        drivingSimulationJob?.cancel()
        lastGpsLocation = null
        drivingSimulationJob = viewModelScope.launch {
            var currentSpeed = 25
            var targetSpeed = 75
            while (isActive) {
                delay(400)
                // Real GPS speed is being fed in via onGpsLocation() — don't
                // fight it with the scripted fallback numbers.
                if (isReceivingRealGps) continue
                if (currentSpeed < targetSpeed) {
                    currentSpeed += (2..4).random()
                } else if (currentSpeed > targetSpeed) {
                    currentSpeed -= (1..3).random()
                } else {
                    targetSpeed = (55..105).random()
                }
                _hudSpeed.value = currentSpeed
                _hudRpm.value = 1400 + (currentSpeed * 28)
                _tripDistance.update { it + (currentSpeed.toFloat() / 3600f * 0.4f) }
            }
        }
    }

    fun accelerate() {
        _hudSpeed.update { (it + 8).coerceAtMost(160) }
        _hudRpm.update { (it + 450).coerceAtMost(6200) }
    }

    fun brake() {
        _hudSpeed.update { (it - 10).coerceAtLeast(0) }
        _hudRpm.update { (it - 350).coerceAtLeast(800) }
    }

    fun setGear(gear: String) {
        _hudGear.value = gear
        if (gear == "P" || gear == "N") {
            _hudSpeed.value = 0
        }
    }
}
