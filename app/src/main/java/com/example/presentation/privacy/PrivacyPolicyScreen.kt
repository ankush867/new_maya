package com.example.presentation.privacy

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.components.GlassCard
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("privacy_policy_screen")
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
                    text = "Privacy & Security Policy",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
                Text(
                    text = "On-device processing & data protection guarantee",
                    fontSize = 12.sp,
                    color = MayaCyanNeon
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1. Local Audio Processing & Privacy",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Text(
                        text = "Maya processes audio in real-time. Microphone buffers are streamed directly over encrypted TLS WebSockets to the Gemini Live server and are never retained or saved on third-party tracking servers.",
                        fontSize = 13.sp,
                        color = MayaTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "2. On-Device Memory Vault Storage",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Text(
                        text = "All memories, journal entries, and custom behavioral rules are persisted strictly in your local SQLite/Room database on your device. You can view, search, export, or erase all memory entries at any time.",
                        fontSize = 13.sp,
                        color = MayaTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "3. Native Android Permissions",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Text(
                        text = "Maya strictly respects Android runtime security boundaries. Sensitive actions (e.g. initiating calls, sending messages, composing emails) utilize explicit Android intents or require your confirmation.",
                        fontSize = 13.sp,
                        color = MayaTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
