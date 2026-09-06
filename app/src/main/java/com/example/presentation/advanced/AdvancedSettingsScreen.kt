package com.example.presentation.advanced

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.settings.SettingsCardGroup
import com.example.presentation.settings.SettingsRowItem
import com.example.presentation.settings.SettingsSectionHeader
import com.example.ui.theme.MayaCardBorder
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaGreenNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaRedNeon
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary

@Composable
fun AdvancedSettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("advanced_settings_screen")
    ) {
        // Header
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
            Column {
                Text(
                    text = "Advanced Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
                Text(
                    text = "Security, guardrails, audio triggers & hardware sync",
                    fontSize = 12.sp,
                    color = MayaCyanNeon
                )
            }
        }

        // Section: APPEARANCE & AUDIO
        SettingsSectionHeader(title = "APPEARANCE & HARDWARE")
        SettingsCardGroup {
            SettingsRowItem(
                icon = Icons.Default.Palette,
                title = "Theme & Visual Customization",
                subtitle = "Neon cyber accents, animated orb sizing & glow",
                iconColor = MayaCyanNeon,
                tag = "adv_theme",
                onClick = { onNavigate("adv_theme") }
            )
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.4f))
            SettingsRowItem(
                icon = Icons.Default.VolumeUp,
                title = "Audio & Echo Cancellation",
                subtitle = "Barge-in sensitivity, hardware AEC, speaker filter",
                iconColor = MayaPurpleNeon,
                tag = "adv_audio",
                onClick = { onNavigate("adv_audio") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section: SECURITY & PROTECTION
        SettingsSectionHeader(title = "SECURITY & PROTECTION")
        SettingsCardGroup {
            SettingsRowItem(
                icon = Icons.Default.RecordVoiceOver,
                title = "Voice Guardian",
                subtitle = "Voice biometric authentication & anti-spoofing",
                iconColor = MayaCyanNeon,
                tag = "adv_voice_guardian",
                onClick = { onNavigate("adv_voice_guardian") }
            )
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.4f))
            SettingsRowItem(
                icon = Icons.Default.Emergency,
                title = "Emergency SOS",
                subtitle = "Distress voice trigger & emergency contact alert",
                iconColor = MayaRedNeon,
                tag = "adv_emergency_sos",
                onClick = { onNavigate("adv_emergency_sos") }
            )
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.4f))
            SettingsRowItem(
                icon = Icons.Default.TouchApp,
                title = "Touch Guard",
                subtitle = "Pocket anti-theft & accidental touch prevention",
                iconColor = Color(0xFFF59E0B),
                tag = "adv_touch_guard",
                onClick = { onNavigate("adv_touch_guard") }
            )
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.4f))
            SettingsRowItem(
                icon = Icons.Default.Lock,
                title = "Screen Lock Integration",
                subtitle = "Spoken status announcements on screen unlock",
                iconColor = MayaGreenNeon,
                tag = "adv_screen_lock",
                onClick = { onNavigate("adv_screen_lock") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section: SYSTEM TRIGGERS
        SettingsSectionHeader(title = "SYSTEM TRIGGERS")
        SettingsCardGroup {
            SettingsRowItem(
                icon = Icons.Default.Bolt,
                title = "Event Triggers & Charging",
                subtitle = "Announce charger connected, low battery alert",
                iconColor = MayaCyanNeon,
                tag = "adv_triggers",
                onClick = { onNavigate("adv_triggers") }
            )
        }
    }
}
