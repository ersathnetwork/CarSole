package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CarDashboardState

@Composable
fun NavigationRadarCard(
    carState: CarDashboardState,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF141320))
            .border(1.dp, Color(0xFF262338), RoundedCornerShape(20.dp))
            .clickable { onMapClick() }
            .testTag("card_navigation_radar")
    ) {
        // Background Map Canvas Simulation (Stylized Dark Map)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Subtle secondary roads
            val secondaryRoadColor = Color(0xFF232233)
            val mainRoadColor = Color(0xFF38354D)
            val highwayColor = Color(0xFF4A4466)

            // Draw road network
            val path1 = Path().apply {
                moveTo(0f, h * 0.4f)
                cubicTo(w * 0.15f, h * 0.45f, w * 0.25f, h * 0.35f, w * 0.45f, h * 0.5f)
                cubicTo(w * 0.5f, h * 0.65f, w * 0.55f, h * 0.85f, w * 0.6f, h)
            }
            drawPath(path1, color = mainRoadColor, style = Stroke(width = 5.dp.toPx()))

            val path2 = Path().apply {
                moveTo(0f, h * 0.7f)
                lineTo(w * 0.25f, h * 0.55f)
                lineTo(w * 0.45f, h * 0.5f)
                lineTo(w * 0.6f, h * 0.35f)
            }
            drawPath(path2, color = highwayColor, style = Stroke(width = 3.dp.toPx()))

            val path3 = Path().apply {
                moveTo(w * 0.3f, 0f)
                lineTo(w * 0.38f, h * 0.45f)
                lineTo(w * 0.45f, h * 0.5f)
            }
            drawPath(path3, color = secondaryRoadColor, style = Stroke(width = 2.dp.toPx()))

            // Navigation position beacon at Hautat Sudair (around w * 0.34f, h * 0.54f)
            val beaconX = w * 0.33f
            val beaconY = h * 0.54f

            // Outer glow pulse
            drawCircle(
                color = Color(0xFF3B82F6).copy(alpha = 0.25f),
                radius = 18.dp.toPx(),
                center = Offset(beaconX, beaconY)
            )
            // Inner circle
            drawCircle(
                color = Color(0xFF3B82F6),
                radius = 7.dp.toPx(),
                center = Offset(beaconX, beaconY)
            )
            // Center white dot
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = Offset(beaconX, beaconY)
            )
        }

        // Map town labels on left
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 85.dp, top = 20.dp)
        ) {
            Text(
                text = "Hautat Sudair",
                color = Color(0xFFC7986A),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "حوطة سدير",
                color = Color(0xFFC7986A).copy(alpha = 0.85f),
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Al Janoubiyah",
                color = Color(0xFFC7986A),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "الجنوبية",
                color = Color(0xFFC7986A).copy(alpha = 0.85f),
                fontSize = 10.sp
            )
        }

        // Google watermark bottom left
        Text(
            text = "Google",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 12.dp)
        )

        // Right side Radar & Location info panel
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
                .width(170.dp)
                .padding(top = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            // Location Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = carState.locationArabic,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = carState.locationName,
                        color = Color(0xFF9E9BAC),
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Radar Alert
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${carState.nearbyRadars} radars nearby",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Radars",
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(16.dp)
                )
            }

            // Subtle divider line
            HorizontalDivider(
                color = Color(0xFF28263D),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth(0.95f)
            )

            // Total Radars Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Total Radars",
                    color = Color(0xFF9E9BAC),
                    fontSize = 12.sp
                )

                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .border(1.5.dp, Color(0xFFD97706), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${carState.totalRadars}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
