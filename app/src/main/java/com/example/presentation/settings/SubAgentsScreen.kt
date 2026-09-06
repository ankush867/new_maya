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
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TipsAndUpdates
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
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaGreenNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary

@Composable
fun SubAgentsScreen(
    onNavigateBack: () -> Unit
) {
    var orchestratorActive by remember { mutableStateOf(true) }
    var dailyBriefingActive by remember { mutableStateOf(true) }
    var memorySynthesizerActive by remember { mutableStateOf(true) }
    var codeAssistantActive by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("subagents_screen")
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
                    text = "Sub-agents & Workers",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
                Text(
                    text = "Background AI tasks managed autonomously",
                    fontSize = 12.sp,
                    color = MayaCyanNeon
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AgentCard(
                icon = Icons.Default.Hub,
                name = "Task Orchestrator Agent",
                role = "Decomposes complex multimodal user prompts into step-by-step tool pipelines.",
                isActive = orchestratorActive,
                onToggle = { orchestratorActive = it },
                color = MayaCyanNeon
            )

            AgentCard(
                icon = Icons.Default.Schedule,
                name = "Morning Briefing Agent",
                role = "Compiles weather, calendar events, battery health, and news at 8:00 AM.",
                isActive = dailyBriefingActive,
                onToggle = { dailyBriefingActive = it },
                color = MayaGreenNeon
            )

            AgentCard(
                icon = Icons.Default.TipsAndUpdates,
                name = "Memory Synthesizer",
                role = "Analyzes conversation transcripts and extracts facts to persist into Memory Vault.",
                isActive = memorySynthesizerActive,
                onToggle = { memorySynthesizerActive = it },
                color = MayaPurpleNeon
            )

            AgentCard(
                icon = Icons.Default.Code,
                name = "Technical Code Explainer",
                role = "Specialized sub-agent for debugging, math explanations, and code snippet breakdowns.",
                isActive = codeAssistantActive,
                onToggle = { codeAssistantActive = it },
                color = Color(0xFFF59E0B)
            )
        }
    }
}

@Composable
private fun AgentCard(
    icon: ImageVector,
    name: String,
    role: String,
    isActive: Boolean,
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.15f),
                border = BorderStroke(0.5.dp, color.copy(alpha = 0.4f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
                Text(
                    text = role,
                    fontSize = 11.sp,
                    color = MayaTextSecondary,
                    lineHeight = 15.sp
                )
            }

            Switch(
                checked = isActive,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MayaCyanNeon,
                    checkedTrackColor = Color(0xFF003852)
                )
            )
        }
    }
}
