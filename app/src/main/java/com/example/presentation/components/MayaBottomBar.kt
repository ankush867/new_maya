package com.example.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.AssistantState
import com.example.ui.theme.MayaCardBorder
import com.example.ui.theme.MayaCardSurface
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary

@Composable
fun MayaBottomBar(
    currentRoute: String,
    assistantState: AssistantState,
    micAmplitude: Float,
    onNavigate: (String) -> Unit,
    onMicClick: () -> Unit
) {
    val isMicActive = assistantState == AssistantState.LISTENING || assistantState == AssistantState.SPEAKING || assistantState == AssistantState.THINKING

    val infiniteTransition = rememberInfiniteTransition(label = "bottom_mic")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("maya_bottom_navigation"),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Glassmorphism Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            color = MayaCardSurface.copy(alpha = 0.95f),
            border = BorderStroke(1.dp, MayaCardBorder.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isSelected = currentRoute == "home",
                    tag = "nav_home",
                    onClick = { onNavigate("home") }
                )

                BottomNavItem(
                    icon = Icons.Default.DocumentScanner,
                    label = "Scan",
                    isSelected = currentRoute == "scan",
                    tag = "nav_scan",
                    onClick = { onNavigate("scan") }
                )

                // Placeholder for central button space
                Box(modifier = Modifier.size(56.dp))

                BottomNavItem(
                    icon = Icons.Default.AutoAwesome,
                    label = "Memories",
                    isSelected = currentRoute == "memories",
                    tag = "nav_memories",
                    onClick = { onNavigate("memories") }
                )

                BottomNavItem(
                    icon = Icons.Default.ChatBubbleOutline,
                    label = "Chat",
                    isSelected = currentRoute == "chat",
                    tag = "nav_chat",
                    onClick = { onNavigate("chat") }
                )
            }
        }

        // Central floating elevated voice button
        Box(
            modifier = Modifier
                .offset(y = (-18).dp)
                .size(64.dp)
                .scale(if (isMicActive) (pulseScale + micAmplitude * 0.2f) else 1f)
                .clip(CircleShape)
                .clickable(onClick = onMicClick)
                .testTag("central_voice_fab"),
            contentAlignment = Alignment.Center
        ) {
            // Glow background
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Brush.radialGradient(
                            colors = if (isMicActive) {
                                listOf(MayaCyanNeon, MayaPurpleNeon, Color.Transparent)
                            } else {
                                listOf(MayaCyanNeon.copy(alpha = 0.9f), Color(0xFF003852), Color.Transparent)
                            }
                        ),
                        shape = CircleShape
                    )
            )

            // Button Core
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = if (isMicActive) MayaNavyBackground else Color(0xFF0E1F38),
                border = BorderStroke(
                    2.dp,
                    if (isMicActive) MayaCyanNeon else MayaCyanNeon.copy(alpha = 0.8f)
                ),
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (assistantState == AssistantState.SPEAKING) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Voice Assistant Control",
                        tint = if (isMicActive) MayaCyanNeon else MayaTextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(tag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) MayaCyanNeon else MayaTextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MayaCyanNeon else MayaTextSecondary
        )
    }
}
