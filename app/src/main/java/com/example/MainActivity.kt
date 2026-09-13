package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.AiRepairScreen
import com.example.ui.screens.DiagnosisResultDialog
import com.example.ui.screens.DriveModeScreen
import com.example.ui.screens.DrivingCockpitScreen
import com.example.ui.screens.ExitDialog
import com.example.ui.screens.GoProSheet
import com.example.ui.screens.HistoryDialog
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.ActiveTab
import com.example.viewmodel.CarDashboardViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: CarDashboardViewModel by viewModels()
    private var locationManager: LocationManager? = null
    private val gpsListener = LocationListener { location -> viewModel.onGpsLocation(location) }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startGpsUpdates() else viewModel.onGpsUnavailable()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // This is a HOME app now (see the manifest) — the screen must never
        // sleep or dim mid-drive, and the dashboard has no "desktop" to
        // return to, so keep it always on.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        locationManager = getSystemService(LocationManager::class.java)

        // Start/stop real GPS speed tracking as the driver enters/leaves the
        // Active Driving HUD, instead of running it (and draining battery)
        // the whole time the launcher is on screen.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.activeScreen.collect { screen ->
                    if (screen == ActiveScreen.ACTIVE_DRIVING_HUD) {
                        ensureLocationPermissionThenStart()
                    } else {
                        stopGpsUpdates()
                    }
                }
            }
        }

        setContent {
            MyApplicationTheme {
                CarSoleApp(
                    viewModel = viewModel,
                    // As the Home app, "Exit" should send the user to the
                    // Android app drawer/recents instead of finishing this
                    // activity — finishing it would leave nothing on screen.
                    onExitApp = { moveTaskToBack(true) }
                )
            }
        }
    }

    private fun ensureLocationPermissionThenStart() {
        val hasPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            startGpsUpdates()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun startGpsUpdates() {
        val manager = locationManager ?: return
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.onGpsUnavailable()
            return
        }
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS radio is off on the head unit — fall back to the simulation
            // rather than sitting frozen at 0 km/h.
            viewModel.onGpsUnavailable()
            return
        }
        try {
            manager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                /* minTimeMs = */ 1000L,
                /* minDistanceM = */ 0f,
                gpsListener
            )
        } catch (e: SecurityException) {
            viewModel.onGpsUnavailable()
        }
    }

    private fun stopGpsUpdates() {
        locationManager?.removeUpdates(gpsListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopGpsUpdates()
    }
}

@Composable
fun CarSoleApp(
    viewModel: CarDashboardViewModel,
    onExitApp: () -> Unit
) {
    val carState by viewModel.carState.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val activeScreen by viewModel.activeScreen.collectAsState()

    val showGoProSheet by viewModel.showGoProSheet.collectAsState()
    val showExitDialog by viewModel.showExitDialog.collectAsState()
    val showHistoryDialog by viewModel.showHistoryDialog.collectAsState()

    val problemDescription by viewModel.problemDescription.collectAsState()
    val selectedSymptom by viewModel.selectedSymptom.collectAsState()
    val selectedBitmap by viewModel.selectedBitmap.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val currentDiagnosis by viewModel.currentDiagnosis.collectAsState()
    val diagnosisHistory by viewModel.diagnosisHistory.collectAsState()

    val selectedPlan by viewModel.selectedPlan.collectAsState()
    val isProUnlocked by viewModel.isProUnlocked.collectAsState()

    val hudSpeed by viewModel.hudSpeed.collectAsState()
    val hudRpm by viewModel.hudRpm.collectAsState()
    val hudGear by viewModel.hudGear.collectAsState()
    val tripDistance by viewModel.tripDistance.collectAsState()

    // Handle system back button
    BackHandler {
        when {
            showGoProSheet -> viewModel.closeGoPro()
            showHistoryDialog -> viewModel.closeHistoryDialog()
            currentDiagnosis != null -> viewModel.dismissDiagnosis()
            activeScreen == ActiveScreen.ACTIVE_DRIVING_HUD -> viewModel.stopDriving()
            activeScreen == ActiveScreen.SETTINGS -> viewModel.closeSettings()
            else -> viewModel.openExitDialog()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09080F))
    ) {
        when (activeScreen) {
            ActiveScreen.ACTIVE_DRIVING_HUD -> {
                DrivingCockpitScreen(
                    carState = carState,
                    speed = hudSpeed,
                    rpm = hudRpm,
                    gear = hudGear,
                    tripDistance = tripDistance,
                    onAccelerate = { viewModel.accelerate() },
                    onBrake = { viewModel.brake() },
                    onGearSelect = { viewModel.setGear(it) },
                    onToggleMusic = { viewModel.toggleMusicPlayback() },
                    onEndDrive = { viewModel.stopDriving() }
                )
            }

            ActiveScreen.SETTINGS -> {
                SettingsScreen(
                    onBackClick = { viewModel.closeSettings() },
                    onUpgradeClick = { viewModel.openGoPro() }
                )
            }

            ActiveScreen.MAIN_DASHBOARD -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (activeTab) {
                        ActiveTab.DRIVE_MODE -> {
                            DriveModeScreen(
                                carState = carState,
                                isProUnlocked = isProUnlocked,
                                onGoProClick = { viewModel.openGoPro() },
                                onSettingsClick = { viewModel.openSettings() },
                                onStartDriving = { viewModel.startDriving() },
                                onMusicAppSelect = { viewModel.selectMusicApp(it) }
                            )
                        }

                        ActiveTab.AI_REPAIR -> {
                            AiRepairScreen(
                                problemDescription = problemDescription,
                                selectedSymptom = selectedSymptom,
                                selectedBitmap = selectedBitmap,
                                isAnalyzing = isAnalyzing,
                                isProUnlocked = isProUnlocked,
                                onDescriptionChange = { viewModel.setProblemDescription(it) },
                                onSymptomSelect = { viewModel.selectSymptom(it) },
                                onPhotoSelected = { viewModel.setPhoto(it) },
                                onClearPhoto = { viewModel.clearPhoto() },
                                onClearDescription = { viewModel.clearProblemDescription() },
                                onAnalyzeClick = { viewModel.startAiAnalysis() },
                                onGoProClick = { viewModel.openGoPro() },
                                onHistoryClick = { viewModel.openHistoryDialog() }
                            )
                        }
                    }

                    // Floating Bottom Navigation Bar
                    BottomNavBar(
                        activeTab = activeTab,
                        onTabSelected = { viewModel.switchTab(it) },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }

        // Modals & Dialogs
        if (showGoProSheet) {
            GoProSheet(
                selectedPlan = selectedPlan,
                onPlanSelect = { viewModel.selectPlan(it) },
                onActivate = { viewModel.activatePro() },
                onClose = { viewModel.closeGoPro() }
            )
        }

        if (showExitDialog) {
            ExitDialog(
                onExplore = { viewModel.closeExitDialog() },
                onExit = {
                    viewModel.closeExitDialog()
                    onExitApp()
                }
            )
        }

        if (currentDiagnosis != null) {
            DiagnosisResultDialog(
                diagnosis = currentDiagnosis!!,
                onDismiss = { viewModel.dismissDiagnosis() }
            )
        }

        if (showHistoryDialog) {
            HistoryDialog(
                history = diagnosisHistory,
                onSelectDiagnosis = { diag ->
                    // Open detailed report
                    viewModel.closeHistoryDialog()
                    viewModel.startAiAnalysis()
                },
                onDismiss = { viewModel.closeHistoryDialog() }
            )
        }
    }
}
