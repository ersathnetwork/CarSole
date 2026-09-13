package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CarDashboardState

@Composable
fun MusicHubCard(
    carState: CarDashboardState,
    onAppSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(230.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF261845),
                        Color(0xFF131021),
                        Color(0xFF0C0A15)
                    ),
                    radius = 360f
                )
            )
            .border(1.dp, Color(0xFF2B2544), RoundedCornerShape(20.dp))
            .padding(12.dp)
            .testTag("card_music_hub")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // App icons grid / floating cloud (YouTube, Spotify, Apple Music, etc.)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Row 1: YouTube, Spotify, Apple Music
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // YouTube
                    MusicAppBadge(
                        name = "YouTube",
                        bgColor = Color(0xFFE50914),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "YouTube",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        isSelected = carState.activeMusicSource == "YouTube",
                        onClick = { onAppSelect("YouTube") }
                    )

                    // Spotify
                    MusicAppBadge(
                        name = "Spotify",
                        bgColor = Color(0xFF1DB954),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Spotify",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        isSelected = carState.activeMusicSource == "Spotify",
                        onClick = { onAppSelect("Spotify") }
                    )

                    // Apple Music
                    MusicAppBadge(
                        name = "Apple Music",
                        bgColor = Color(0xFFFA233B),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Audiotrack,
                                contentDescription = "Apple Music",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        isSelected = carState.activeMusicSource == "Apple Music",
                        onClick = { onAppSelect("Apple Music") }
                    )
                }

                // Row 2: YT Music, Radio, Deezer, SoundCloud
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // YouTube Music
                    MusicAppBadge(
                        name = "YT Music",
                        bgColor = Color(0xFF831843),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "YouTube Music",
                                tint = Color(0xFFF472B6),
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        isSelected = carState.activeMusicSource == "YT Music",
                        onClick = { onAppSelect("YT Music") }
                    )

                    // Radio
                    MusicAppBadge(
                        name = "Radio",
                        bgColor = Color(0xFF3B82F6),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Radio,
                                contentDescription = "Radio",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        isSelected = carState.activeMusicSource == "Radio",
                        onClick = { onAppSelect("Radio") }
                    )

                    // Deezer
                    MusicAppBadge(
                        name = "Deezer",
                        bgColor = Color(0xFF7C3AED),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Equalizer,
                                contentDescription = "Deezer",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        isSelected = carState.activeMusicSource == "Deezer",
                        onClick = { onAppSelect("Deezer") }
                    )

                    // SoundCloud
                    MusicAppBadge(
                        name = "SoundCloud",
                        bgColor = Color(0xFFFF5500),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "SoundCloud",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        isSelected = carState.activeMusicSource == "SoundCloud",
                        onClick = { onAppSelect("SoundCloud") }
                    )
                }
            }

            // Bottom Label
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "YouTube, Spotify & more",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (carState.isPlayingMusic) "Playing • ${carState.activeMusicSource}" else "Tap an app to start listening",
                    color = Color(0xFF9E9BAC),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun MusicAppBadge(
    name: String,
    bgColor: Color,
    icon: @Composable () -> Unit,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor.copy(alpha = if (isSelected) 0.95f else 0.75f))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}
