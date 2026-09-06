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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AssistantRepository
import com.example.presentation.components.GlassCard
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import kotlinx.coroutines.launch

@Composable
fun TouchGuardScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AssistantRepository.getInstance(context) }
    val settings by repository.settings.collectAsState()

    var touchGuardEnabled by remember(settings.touchGuardEnabled) {
        mutableStateOf(settings.touchGuardEnabled)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("touch_guard_screen")
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
                    text = "Touch Guard",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
                Text(
                    text = "Accidental pocket touch protection",
                    fontSize = 12.sp,
                    color = Color(0xFFF59E0B)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Proximity & Pocket Guard",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaTextPrimary
                            )
                            Text(
                                text = "Prevents accidental voice activation and screen taps when the device is inside a pocket or bag.",
                                fontSize = 12.sp,
                                color = MayaTextSecondary
                            )
                        }
                        Switch(
                            checked = touchGuardEnabled,
                            onCheckedChange = { touchGuardEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFF59E0B),
                                checkedTrackColor = Color(0xFF451A03)
                            )
                        )
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        repository.preferences.updateSettings {
                            it.copy(touchGuardEnabled = touchGuardEnabled)
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Save Touch Guard", color = Color(0xFF041424), fontWeight = FontWeight.Bold)
            }
        }
    }
}
