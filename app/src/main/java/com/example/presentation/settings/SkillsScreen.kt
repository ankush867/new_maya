package com.example.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.components.GlassCard
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
fun SkillsScreen(
    onNavigateBack: () -> Unit
) {
    var callSkill by remember { mutableStateOf(true) }
    var whatsAppSkill by remember { mutableStateOf(true) }
    var emailSkill by remember { mutableStateOf(true) }
    var appsSkill by remember { mutableStateOf(true) }
    var webSkill by remember { mutableStateOf(true) }
    var musicSkill by remember { mutableStateOf(true) }
    var calendarSkill by remember { mutableStateOf(true) }
    var settingsSkill by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("skills_screen")
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
                    text = "Skills & Function Calling",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
                Text(
                    text = "Native Android tools enabled for Gemini Live",
                    fontSize = 12.sp,
                    color = MayaCyanNeon
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SkillToggleItem(
                icon = Icons.Default.Call,
                title = "Phone Calling & Contact Search",
                description = "Searches device address book and places phone calls with confirmation.",
                isEnabled = callSkill,
                onToggle = { callSkill = it },
                color = MayaGreenNeon
            )

            SkillToggleItem(
                icon = Icons.Default.Message,
                title = "WhatsApp Voice Messaging",
                description = "Prepares and opens WhatsApp chats with voice dictation.",
                isEnabled = whatsAppSkill,
                onToggle = { whatsAppSkill = it },
                color = MayaGreenNeon
            )

            SkillToggleItem(
                icon = Icons.Default.Email,
                title = "Gmail Compose & Drafts",
                description = "Composes emails with subject line and body via ACTION_SENDTO.",
                isEnabled = emailSkill,
                onToggle = { emailSkill = it },
                color = Color(0xFFEA4335)
            )

            SkillToggleItem(
                icon = Icons.Default.DevicesOther,
                title = "App Launcher",
                description = "Launches installed applications (YouTube, Spotify, Maps, Camera).",
                isEnabled = appsSkill,
                onToggle = { appsSkill = it },
                color = MayaCyanNeon
            )

            SkillToggleItem(
                icon = Icons.Default.MusicNote,
                title = "Music & Media Control",
                description = "Plays songs, artists, playlists on YouTube or default media app.",
                isEnabled = musicSkill,
                onToggle = { musicSkill = it },
                color = MayaPurpleNeon
            )

            SkillToggleItem(
                icon = Icons.Default.CalendarMonth,
                title = "Calendar Event Planner",
                description = "Schedules events, reminders, and calendar appointments.",
                isEnabled = calendarSkill,
                onToggle = { calendarSkill = it },
                color = MayaCyanNeon
            )

            SkillToggleItem(
                icon = Icons.Default.Language,
                title = "Web Browsing & Search",
                description = "Opens URLs and searches Google for real-time web results.",
                isEnabled = webSkill,
                onToggle = { webSkill = it },
                color = MayaCyanNeon
            )

            SkillToggleItem(
                icon = Icons.Default.Settings,
                title = "Device Settings & Telemetry",
                description = "Reads battery level, charging status, and opens system settings.",
                isEnabled = settingsSkill,
                onToggle = { settingsSkill = it },
                color = MayaPurpleNeon
            )
        }
    }
}

@Composable
private fun SkillToggleItem(
    icon: ImageVector,
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    color: Color
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = color.copy(alpha = 0.15f),
                border = BorderStroke(0.5.dp, color.copy(alpha = 0.4f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MayaTextSecondary,
                    lineHeight = 15.sp
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MayaCyanNeon,
                    checkedTrackColor = Color(0xFF003852)
                )
            )
        }
    }
}
