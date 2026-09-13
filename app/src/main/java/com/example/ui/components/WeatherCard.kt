package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CarDashboardState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WeatherCard(
    carState: CarDashboardState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(230.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E3A5F),
                        Color(0xFF132338),
                        Color(0xFF0F1826)
                    )
                )
            )
            .border(1.dp, Color(0xFF233E60), RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("card_weather_widget")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Temperature + Date, and Bright Sun on Right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "${carState.temperatureC}°C",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = carState.weatherDate,
                        color = Color(0xFF60A5FA),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Vibrant Sun Graphic with Rays
                GlowingSunIcon(modifier = Modifier.size(52.dp))
            }

            // Bottom Left Location
            Column {
                Text(
                    text = "Tumayr",
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "SA",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun GlowingSunIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val center = Offset(centerX, centerY)
        val coreRadius = size.minDimension * 0.26f

        // Outer glow
        drawCircle(
            color = Color(0xFFFBBF24).copy(alpha = 0.25f),
            radius = coreRadius * 1.8f,
            center = center
        )

        // Sun core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFEF08A),
                    Color(0xFFFBBF24),
                    Color(0xFFF59E0B)
                ),
                center = center,
                radius = coreRadius
            ),
            radius = coreRadius,
            center = center
        )

        // Rays
        val rayCount = 12
        val rayInner = coreRadius * 1.35f
        val rayOuter = coreRadius * 1.75f

        for (i in 0 until rayCount) {
            val angle = Math.toRadians((i * (360.0 / rayCount)))
            val startX = centerX + (rayInner * cos(angle)).toFloat()
            val startY = centerY + (rayInner * sin(angle)).toFloat()
            val endX = centerX + (rayOuter * cos(angle)).toFloat()
            val endY = centerY + (rayOuter * sin(angle)).toFloat()

            drawLine(
                color = Color(0xFFFBBF24),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}
