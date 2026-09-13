package com.example.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun GoProSheet(
    selectedPlan: String,
    onPlanSelect: (String) -> Unit,
    onActivate: () -> Unit,
    onClose: () -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        var activeSlide by remember { mutableIntStateOf(0) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090812))
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Row: Close Button (X)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E1C2E))
                            .testTag("btn_close_pro")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hero Slider Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activeSlide = (activeSlide + 1) % 2 }
                ) {
                    if (activeSlide == 0) {
                        Text(
                            text = "Unlock All Widgets",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Smarter widgets. Faster access. A better driving experience every day.",
                            color = Color(0xFFA5A0B7),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    } else {
                        Text(
                            text = "CarPlay Sounds",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Upgrade your car audio and connection experience in seconds.",
                            color = Color(0xFFA5A0B7),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Indicator Dots [ •  • ]
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(if (activeSlide == 0) 18.dp else 6.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (activeSlide == 0) Color(0xFFA78BFA) else Color(0xFF332F4C))
                        )
                        Box(
                            modifier = Modifier
                                .width(if (activeSlide == 1) 18.dp else 6.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (activeSlide == 1) Color(0xFFA78BFA) else Color(0xFF332F4C))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Subscription Plan Selector (Weekly vs Yearly)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    PlanOptionCard(
                        title = "Weekly",
                        badge = "3-day free trial",
                        price = "SAR 29.99",
                        period = "/ week",
                        isSelected = selectedPlan == "Weekly",
                        onSelect = { onPlanSelect("Weekly") },
                        modifier = Modifier.weight(1f)
                    )

                    PlanOptionCard(
                        title = "Yearly",
                        badge = "Best Value",
                        price = "SAR 189.99",
                        period = "/ year",
                        isSelected = selectedPlan == "Yearly",
                        onSelect = { onPlanSelect("Yearly") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // What's Included / Premium Features
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF131120))
                        .border(1.dp, Color(0xFF26223B), RoundedCornerShape(20.dp))
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "WHAT'S INCLUDED",
                        color = Color(0xFF8E8B9F),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    ProFeatureRow("✨", "Customize CarPlay & Android Auto Screen")
                    ProFeatureRow("🎵", "Custom Connection & Engine Sounds")
                    ProFeatureRow("📲", "Full-Screen Speed & Radar Widgets")
                    ProFeatureRow("🗺️", "Live Satellite Dashboard Navigation")
                    ProFeatureRow("🚙", "Multi-Vehicle Garage & AI Diagnostics")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // CTA Button: "Try for SAR0.00"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF6338CA),
                                    Color(0xFF7E3FF2),
                                    Color(0xFF9333EA)
                                )
                            )
                        )
                        .clickable { onActivate() }
                        .padding(vertical = 16.dp)
                        .testTag("btn_confirm_pro"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Try for SAR0.00",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Disclaimer Subtext (Screenshot 6)
                Text(
                    text = "🚫 No Payment NOW! After 3-Day Free trial SAR29.99 will be renewed every week. Auto Renewable Cancel Anytime",
                    color = Color(0xFF7E7A91),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Footer links
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Terms of Use",
                        color = Color(0xFF8E8B9F),
                        fontSize = 11.sp,
                        modifier = Modifier.clickable { onClose() }
                    )
                    Text(
                        text = "  •  ",
                        color = Color(0xFF555268),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Privacy Policy",
                        color = Color(0xFF8E8B9F),
                        fontSize = 11.sp,
                        modifier = Modifier.clickable { onClose() }
                    )
                    Text(
                        text = "  •  ",
                        color = Color(0xFF555268),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Restore",
                        color = Color(0xFF8E8B9F),
                        fontSize = 11.sp,
                        modifier = Modifier.clickable { onActivate() }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun PlanOptionCard(
    title: String,
    badge: String,
    price: String,
    period: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) Color(0xFF281C4A) else Color(0xFF131120))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFFA78BFA) else Color(0xFF26223B),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onSelect() }
            .padding(14.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) Color(0xFF7C3AED) else Color(0xFF252238))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badge,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = price,
                color = Color(0xFFA78BFA),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = period,
                color = Color(0xFF8E8B9F),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ProFeatureRow(emoji: String, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
