package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CarSoleCardBorder
import com.example.ui.theme.CarSoleNeonPurple
import com.example.ui.theme.CarSolePillBg
import com.example.viewmodel.ActiveTab

@Composable
fun BottomNavBar(
    activeTab: ActiveTab,
    onTabSelected: (ActiveTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF161424).copy(alpha = 0.94f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drive Mode Tab
            val isDriveSelected = activeTab == ActiveTab.DRIVE_MODE
            val driveBgColor by animateColorAsState(
                targetValue = if (isDriveSelected) Color(0xFF28253B) else Color.Transparent,
                label = "driveBg"
            )
            val driveTextColor by animateColorAsState(
                targetValue = if (isDriveSelected) Color.White else Color(0xFF8E8B9F),
                label = "driveText"
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(driveBgColor)
                    .clickable { onTabSelected(ActiveTab.DRIVE_MODE) }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .testTag("nav_drive_mode"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = "Drive Mode",
                    tint = if (isDriveSelected) CarSoleNeonPurple else Color(0xFF8E8B9F),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Drive Mode",
                    color = driveTextColor,
                    fontSize = 14.sp,
                    fontWeight = if (isDriveSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // AI Repair Tab
            val isRepairSelected = activeTab == ActiveTab.AI_REPAIR
            val repairBgColor by animateColorAsState(
                targetValue = if (isRepairSelected) Color(0xFF28253B) else Color.Transparent,
                label = "repairBg"
            )
            val repairTextColor by animateColorAsState(
                targetValue = if (isRepairSelected) Color.White else Color(0xFF8E8B9F),
                label = "repairText"
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(repairBgColor)
                    .clickable { onTabSelected(ActiveTab.AI_REPAIR) }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .testTag("nav_ai_repair"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI Repair",
                    tint = if (isRepairSelected) CarSoleNeonPurple else Color(0xFF8E8B9F),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Repair",
                    color = repairTextColor,
                    fontSize = 14.sp,
                    fontWeight = if (isRepairSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}
