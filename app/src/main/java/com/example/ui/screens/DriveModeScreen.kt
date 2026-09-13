package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.model.CarDashboardState
import com.example.ui.components.CarHeroCard
import com.example.ui.components.CarSoleTopBar
import com.example.ui.components.MusicHubCard
import com.example.ui.components.NavigationRadarCard
import com.example.ui.components.WeatherCard

@Composable
fun DriveModeScreen(
    carState: CarDashboardState,
    isProUnlocked: Boolean,
    onGoProClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onStartDriving: () -> Unit,
    onMusicAppSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF09080F))
    ) {
        // Top Bar
        CarSoleTopBar(
            onGoProClick = onGoProClick,
            onSettingsClick = onSettingsClick,
            isProUnlocked = isProUnlocked
        )

        // Scrollable Dashboard Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Card
            CarHeroCard(
                onStartDriving = onStartDriving
            )

            // Navigation Radar Card
            NavigationRadarCard(
                carState = carState,
                onMapClick = onStartDriving
            )

            // Split 50/50 Cards: Music Hub & Weather Widget
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MusicHubCard(
                    carState = carState,
                    onAppSelect = onMusicAppSelect,
                    modifier = Modifier.weight(1f)
                )

                WeatherCard(
                    carState = carState,
                    modifier = Modifier.weight(1f)
                )
            }

            // Bottom space to prevent bottom navigation bar overlay
            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}
