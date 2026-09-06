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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun SettingsScreen(
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("settings_screen")
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MayaTextPrimary
            )
            Text(
                text = "Personalize Maya's intelligence, voice, tools & protection",
                fontSize = 13.sp,
                color = MayaTextSecondary
            )
        }

        // 1. ACCOUNT CATEGORY
        SettingsSectionHeader(title = "ACCOUNT")
        SettingsCardGroup {
            SettingsRowItem(
                icon = Icons.Default.AccountCircle,
                title = "Personal",
                subtitle = "Name, Preferred Language, Gemini Model, Music",
                iconColor = MayaCyanNeon,
                tag = "setting_personal",
                onClick = { onNavigate("settings_personal") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. ASSISTANT CATEGORY
        SettingsSectionHeader(title = "ASSISTANT")
        SettingsCardGroup {
            SettingsRowItem(
                icon = Icons.Default.RecordVoiceOver,
                title = "Voice",
                subtitle = "Maya, Friday & Venom vocal personas with audio preview",
                iconColor = MayaCyanNeon,
                tag = "setting_voice",
                onClick = { onNavigate("settings_voice") }
            )
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.4f))
            SettingsRowItem(
                icon = Icons.Default.Psychology,
                title = "Maya Assistant",
                subtitle = "Personality, Voice, Speed, Tone, Expressiveness",
                iconColor = MayaPurpleNeon,
                tag = "setting_maya_assistant",
                onClick = { onNavigate("settings_assistant") }
            )
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.4f))
            SettingsRowItem(
                icon = Icons.Default.Extension,
                title = "Skills & Function Calling",
                subtitle = "Active tools, device controls, web search",
                iconColor = MayaCyanNeon,
                tag = "setting_skills",
                onClick = { onNavigate("settings_skills") }
            )
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.4f))
            SettingsRowItem(
                icon = Icons.Default.Hub,
                title = "Sub-agents",
                subtitle = "Autonomous background task workers",
                iconColor = MayaGreenNeon,
                tag = "setting_subagents",
                onClick = { onNavigate("settings_subagents") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. WORK & MESSAGES CATEGORY
        SettingsSectionHeader(title = "WORK & MESSAGES")
        SettingsCardGroup {
            SettingsRowItem(
                icon = Icons.Default.Email,
                title = "Email Integration",
                subtitle = "Gmail compose, drafts, priority alerts",
                iconColor = Color(0xFFEA4335),
                tag = "setting_email",
                onClick = { onNavigate("settings_email") }
            )
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.4f))
            SettingsRowItem(
                icon = Icons.Default.Message,
                title = "WhatsApp Integration",
                subtitle = "Contact messaging, voice dictation, auto-drafts",
                iconColor = MayaGreenNeon,
                tag = "setting_whatsapp",
                onClick = { onNavigate("settings_whatsapp") }
            )
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.4f))
            SettingsRowItem(
                icon = Icons.Default.Share,
                title = "Social Media",
                subtitle = "Connected social platforms & updates",
                iconColor = MayaCyanNeon,
                tag = "setting_social",
                onClick = { onNavigate("settings_social") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. CONNECTED ACCOUNTS CATEGORY
        SettingsSectionHeader(title = "CONNECTED ACCOUNTS")
        SettingsCardGroup {
            SettingsRowItem(
                icon = Icons.Default.Link,
                title = "Google Workspace & Media",
                subtitle = "Calendar, Contacts, YouTube & Spotify",
                iconColor = MayaCyanNeon,
                tag = "setting_connected_accounts",
                onClick = { onNavigate("settings_connected") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. SYSTEM & ADVANCED CATEGORY
        SettingsSectionHeader(title = "SYSTEM & SECURITY")
        SettingsCardGroup {
            SettingsRowItem(
                icon = Icons.Default.Lock,
                title = "Pattern & PIN (Voice Unlock)",
                subtitle = "Save pattern/PIN so Maya can open phone on command",
                iconColor = MayaCyanNeon,
                tag = "setting_screen_lock_direct",
                onClick = { onNavigate("adv_screen_lock") }
            )
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.4f))
            SettingsRowItem(
                icon = Icons.Default.Tune,
                title = "Advanced Settings",
                subtitle = "Theme, Voice Guardian, SOS, Touch Guard, Screen Lock",
                iconColor = MayaPurpleNeon,
                tag = "setting_advanced",
                onClick = { onNavigate("settings_advanced") }
            )
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.4f))
            SettingsRowItem(
                icon = Icons.Default.Gavel,
                title = "Maya Rules",
                subtitle = "Custom prompt rules & behavioral guardrails",
                iconColor = MayaCyanNeon,
                tag = "setting_rules",
                onClick = { onNavigate("rules") }
            )
            HorizontalDivider(color = MayaCardBorder.copy(alpha = 0.4f))
            SettingsRowItem(
                icon = Icons.Default.Security,
                title = "App Permissions",
                subtitle = "Microphone, Contacts, Calls, Notifications",
                iconColor = MayaGreenNeon,
                tag = "setting_permissions",
                onClick = { onNavigate("permissions") }
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = MayaTextMuted,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
    )
}

@Composable
fun SettingsCardGroup(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(18.dp),
        color = MayaCardSurface,
        border = BorderStroke(1.dp, MayaCardBorder)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(38.dp),
            shape = RoundedCornerShape(10.dp),
            color = iconColor.copy(alpha = 0.15f),
            border = BorderStroke(0.5.dp, iconColor.copy(alpha = 0.4f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MayaTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MayaTextSecondary,
                lineHeight = 15.sp
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = MayaTextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}
