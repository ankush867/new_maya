package com.example.presentation.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AssistantRepository
import com.example.presentation.components.GlassCard
import com.example.ui.theme.MayaCardBorder
import com.example.ui.theme.MayaCardSurface
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaTextMuted
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import kotlinx.coroutines.launch

@Composable
fun MayaAssistantSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AssistantRepository.getInstance(context) }
    val settings by repository.settings.collectAsState()

    var personality by remember(settings.personalityStyle) { mutableStateOf(settings.personalityStyle) }
    var voice by remember(settings.assistantVoice) { mutableStateOf(settings.assistantVoice) }
    var speed by remember(settings.speakingSpeed) { mutableFloatStateOf(settings.speakingSpeed) }
    var responseLength by remember(settings.responseLength) { mutableStateOf(settings.responseLength) }
    var wakeWordEnabled by remember(settings.wakeWordEnabled) { mutableStateOf(settings.wakeWordEnabled) }
    var wakePhrase by remember(settings.wakePhrase) { mutableStateOf(settings.wakePhrase) }

    val voices = listOf(
        Pair("Aoede", "Warm, lively & confident (Recommended for Maya)"),
        Pair("Kore", "Calm, soothing & clear"),
        Pair("Puck", "Playful & expressive"),
        Pair("Fenrir", "Deep & authoritative"),
        Pair("Zephyr", "Gentle & empathetic")
    )

    val personalities = listOf(
        Pair("AI Girlfriend Mode ❤️", "Sweet, loving, affectionate & caring companion who calls you by name, jaan or babu"),
        Pair("Best Friend Mode ⚡", "Fun, roasting, high-energy buddy who chats like a real friend in casual Hinglish"),
        Pair("Witty & Intelligent 🧠", "Sharp, sarcastic, charming, and highly knowledgeable"),
        Pair("Professional Assistant 💼", "Polite, structured, executive efficiency & conciseness")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("assistant_settings_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MayaTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Maya Assistant Persona",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MayaTextPrimary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Assistant Voice Selection
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Voice",
                            tint = MayaCyanNeon,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Gemini Live Voice",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MayaTextPrimary
                        )
                    }
                    Text(
                        text = "Native ultra-low latency voice synthesizer",
                        fontSize = 12.sp,
                        color = MayaTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    voices.forEach { (vName, desc) ->
                        val isSelected = voice == vName
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { voice = vName }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { voice = vName },
                                colors = RadioButtonDefaults.colors(selectedColor = MayaCyanNeon)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = vName,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MayaCyanNeon else MayaTextPrimary
                                )
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = MayaTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 2. Personality Style
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Personality",
                            tint = MayaPurpleNeon,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Personality & Tone",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MayaTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    personalities.forEach { (style, desc) ->
                        val isSelected = personality.contains(style.substringBefore(" ").trim(), ignoreCase = true) || personality == style
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { personality = style }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { personality = style },
                                colors = RadioButtonDefaults.colors(selectedColor = MayaPurpleNeon)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = style,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) MayaPurpleNeon else MayaTextPrimary
                                )
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = MayaTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 3. Speaking Speed Slider
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Speed",
                                tint = MayaCyanNeon,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Speaking Pace",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaTextPrimary
                            )
                        }
                        Text(
                            text = "${"%.2f".format(speed)}x",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MayaCyanNeon
                        )
                    }

                    Slider(
                        value = speed,
                        onValueChange = { speed = it },
                        valueRange = 0.75f..1.5f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = MayaCyanNeon,
                            activeTrackColor = MayaCyanNeon,
                            inactiveTrackColor = MayaCardBorder
                        ),
                        modifier = Modifier.testTag("speed_slider")
                    )
                }
            }

            // 4. Wake Phrase & Local Detection
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Wake Word",
                                tint = MayaCyanNeon,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Hands-free Wake Detection",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MayaTextPrimary
                                )
                                Text(
                                    text = "Triggers on 'Maya', 'Hey Maya', or 'Zoya'",
                                    fontSize = 11.sp,
                                    color = MayaTextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = wakeWordEnabled,
                            onCheckedChange = { wakeWordEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MayaCyanNeon,
                                checkedTrackColor = Color(0xFF003852)
                            ),
                            modifier = Modifier.testTag("wake_word_switch")
                        )
                    }
                }
            }

            // 5. Intelligent Behaviors (Conversation Mode, Proactive, Alerts)
            var conversationMode by remember(settings.conversationModeEnabled) { mutableStateOf(settings.conversationModeEnabled) }
            var messageAlerts by remember(settings.messageAlertsEnabled) { mutableStateOf(settings.messageAlertsEnabled) }
            var autoStartWake by remember(settings.autoStartWakeWord) { mutableStateOf(settings.autoStartWakeWord) }
            var proactiveMaya by remember(settings.proactiveMaya) { mutableStateOf(settings.proactiveMaya) }
            var callAnnounce by remember(settings.callAnnounceEnabled) { mutableStateOf(settings.callAnnounceEnabled) }
            var keepRingtone by remember(settings.keepRingtonePlaying) { mutableStateOf(settings.keepRingtonePlaying) }
            var drivingMode by remember(settings.drivingModeEnabled) { mutableStateOf(settings.drivingModeEnabled) }
            var drivingReply by remember(settings.drivingModeAutoReply) { mutableStateOf(settings.drivingModeAutoReply) }

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Behavior & Context Triggers",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )

                    // Conversation Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Conversation Mode", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MayaTextPrimary)
                            Text("Talk naturally with emotion-adaptive voice", fontSize = 11.sp, color = MayaTextSecondary)
                        }
                        Switch(
                            checked = conversationMode,
                            onCheckedChange = { conversationMode = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = MayaCyanNeon, checkedTrackColor = Color(0xFF003852))
                        )
                    }

                    // Message Alerts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Message Alerts", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MayaTextPrimary)
                            Text("Notify aloud when WhatsApp messages arrive", fontSize = 11.sp, color = MayaTextSecondary)
                        }
                        Switch(
                            checked = messageAlerts,
                            onCheckedChange = { messageAlerts = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = MayaCyanNeon, checkedTrackColor = Color(0xFF003852))
                        )
                    }

                    // Auto Start
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Auto Start", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MayaTextPrimary)
                            Text("Bring wake word back after a pause", fontSize = 11.sp, color = MayaTextSecondary)
                        }
                        Switch(
                            checked = autoStartWake,
                            onCheckedChange = { autoStartWake = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = MayaCyanNeon, checkedTrackColor = Color(0xFF003852))
                        )
                    }

                    // Proactive Maya
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Proactive Maya", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MayaTextPrimary)
                            Text("Start check-ins on her own based on context", fontSize = 11.sp, color = MayaTextSecondary)
                        }
                        Switch(
                            checked = proactiveMaya,
                            onCheckedChange = { proactiveMaya = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = MayaCyanNeon, checkedTrackColor = Color(0xFF003852))
                        )
                    }

                    // Call Announcements
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Call Announcements", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MayaTextPrimary)
                            Text("Announce caller name out loud", fontSize = 11.sp, color = MayaTextSecondary)
                        }
                        Switch(
                            checked = callAnnounce,
                            onCheckedChange = { callAnnounce = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = MayaCyanNeon, checkedTrackColor = Color(0xFF003852))
                        )
                    }

                    // Keep Ringtone Playing
                    if (callAnnounce) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Keep Ringtone Playing", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MayaTextPrimary)
                                Text("Play ringtone alongside voice announcement", fontSize = 11.sp, color = MayaTextSecondary)
                            }
                            Switch(
                                checked = keepRingtone,
                                onCheckedChange = { keepRingtone = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = MayaCyanNeon, checkedTrackColor = Color(0xFF003852))
                            )
                        }
                    }

                    // Driving Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Driving Mode", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MayaTextPrimary)
                            Text("Auto reject calls & send SMS while driving", fontSize = 11.sp, color = MayaTextSecondary)
                        }
                        Switch(
                            checked = drivingMode,
                            onCheckedChange = { drivingMode = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = MayaCyanNeon, checkedTrackColor = Color(0xFF003852))
                        )
                    }
                }
            }

            // Save Button
            Button(
                onClick = {
                    scope.launch {
                        repository.preferences.updateSettings {
                            it.copy(
                                personalityStyle = personality,
                                assistantVoice = voice,
                                speakingSpeed = speed,
                                responseLength = responseLength,
                                wakeWordEnabled = wakeWordEnabled,
                                wakePhrase = wakePhrase,
                                conversationModeEnabled = conversationMode,
                                messageAlertsEnabled = messageAlerts,
                                autoStartWakeWord = autoStartWake,
                                proactiveMaya = proactiveMaya,
                                callAnnounceEnabled = callAnnounce,
                                keepRingtonePlaying = keepRingtone,
                                drivingModeEnabled = drivingMode,
                                drivingModeAutoReply = drivingReply
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_assistant_settings_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MayaCyanNeon),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save",
                    tint = Color(0xFF041424),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Voice & Persona",
                    color = Color(0xFF041424),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
