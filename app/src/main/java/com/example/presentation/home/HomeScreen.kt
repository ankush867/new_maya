package com.example.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.AssistantState
import com.example.core.model.ConnectionState
import com.example.presentation.components.GlassCard
import com.example.presentation.components.MayaCharacterView
import com.example.presentation.components.MayaOrb
import com.example.ui.theme.MayaAmberNeon
import com.example.ui.theme.MayaCardBorder
import com.example.ui.theme.MayaCardSurface
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaGreenNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaTextMuted
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigate: (String) -> Unit
) {
    val assistantState by viewModel.assistantState.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val micAmplitude by viewModel.micAmplitude.collectAsState()
    val speakerAmplitude by viewModel.speakerAmplitude.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val energyLevel by viewModel.energyLevel.collectAsState()

    val greeting = viewModel.getGreeting()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("home_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // 2. Greeting & Status
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$greeting ${settings.userName}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${settings.assistantName} is in ${settings.personalityStyle}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MayaPurpleNeon
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (settings.voiceGuardianEnabled) MayaGreenNeon.copy(alpha = 0.12f) else MayaTextMuted.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, if (settings.voiceGuardianEnabled) MayaGreenNeon.copy(alpha = 0.5f) else Color.Transparent),
                    modifier = Modifier.clickable { onNavigate("voice_guardian") }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Guardian Status",
                            tint = if (settings.voiceGuardianEnabled) MayaGreenNeon else MayaTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (settings.voiceGuardianEnabled) "Voice Locked" else "Open",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (settings.voiceGuardianEnabled) MayaGreenNeon else MayaTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Connection Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            when (connectionState) {
                                ConnectionState.CONNECTED -> MayaGreenNeon
                                ConnectionState.CONNECTING, ConnectionState.RECONNECTING -> MayaAmberNeon
                                else -> Color(0xFF94A3B8)
                            }
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${connectionState.label} • Gemini AI Assistant",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MayaCyanNeon
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Energy Card & Maya Character Display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Energy Card
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .height(180.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Energy",
                                tint = MayaCyanNeon,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ENERGY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaTextSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "$energyLevel%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MayaCyanNeon
                        )
                    }

                    LinearProgressIndicator(
                        progress = { energyLevel / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MayaCyanNeon,
                        trackColor = Color(0xFF1E2E4A)
                    )

                    Column {
                        Text(
                            text = "Aura Matrix: Active",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MayaTextPrimary
                        )
                        Text(
                            text = "Full neural sync across apps & voice",
                            fontSize = 11.sp,
                            color = MayaTextMuted
                        )
                    }
                }
            }

            // Maya Animated Character
            MayaCharacterView(
                state = assistantState,
                speakerAmplitude = speakerAmplitude,
                size = 180.dp,
                onClick = { viewModel.toggleVoice() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Central Audio Orb Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MayaOrb(
                state = assistantState,
                amplitude = if (assistantState == AssistantState.SPEAKING) speakerAmplitude else micAmplitude,
                size = 130.dp,
                onClick = { viewModel.toggleVoice() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = when (assistantState) {
                    AssistantState.LISTENING -> "Listening... Say 'Maya' or ask anything"
                    AssistantState.SPEAKING -> "Tap orb to interrupt Maya"
                    AssistantState.THINKING -> "Thinking with Gemini Live..."
                    AssistantState.EXECUTING_TOOL -> "Executing Android tool..."
                    else -> "Tap orb or say 'Hey Maya' to talk"
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (assistantState == AssistantState.SPEAKING) MayaCyanNeon else MayaTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Quick Actions Carousel
        Text(
            text = "QUICK ACTIONS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MayaTextMuted,
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
        ) {
            item {
                QuickActionChip(
                    icon = Icons.Default.MusicNote,
                    label = "Play Music",
                    color = MayaCyanNeon,
                    onClick = {
                        viewModel.sendQuickPrompt("Play some trending upbeat music on YouTube")
                        onNavigate("chat")
                    }
                )
            }
            item {
                QuickActionChip(
                    icon = Icons.Default.Draw,
                    label = "Whiteboard",
                    color = MayaPurpleNeon,
                    onClick = { onNavigate("study") }
                )
            }
            item {
                QuickActionChip(
                    icon = Icons.Default.EditNote,
                    label = "Journal & Mood",
                    color = MayaGreenNeon,
                    onClick = { onNavigate("journal") }
                )
            }
            item {
                QuickActionChip(
                    icon = Icons.Default.QuestionAnswer,
                    label = "Ask Anything",
                    color = MayaAmberNeon,
                    onClick = { onNavigate("chat") }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 6. Insight Cards (Weather & Intelligence)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Weather",
                            tint = MayaAmberNeon,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "28°C",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MayaTextPrimary
                        )
                    }
                    Column {
                        Text(
                            text = "Clear Sky & Sunny",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MayaTextPrimary
                        )
                        Text(
                            text = "Great time to step out",
                            fontSize = 11.sp,
                            color = MayaTextSecondary
                        )
                    }
                }
            }

            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                shape = RoundedCornerShape(18.dp),
                onClick = { onNavigate("memories") }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MEMORIES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaCyanNeon,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Maya remembers your preferences and schedule context.",
                        fontSize = 11.sp,
                        color = MayaTextSecondary,
                        lineHeight = 14.sp
                    )
                    Text(
                        text = "View memory vault →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MayaCyanNeon
                    )
                }
            }
        }
    }
}


@Composable
private fun QuickActionChip(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MayaCardSurface,
        border = BorderStroke(1.dp, MayaCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MayaTextPrimary
            )
        }
    }
}
