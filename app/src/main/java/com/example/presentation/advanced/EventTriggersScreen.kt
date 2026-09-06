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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Bolt
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
fun EventTriggersScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AssistantRepository.getInstance(context) }
    val settings by repository.settings.collectAsState()

    var chargeTrigger by remember(settings.announceChargingConnected) {
        mutableStateOf(settings.announceChargingConnected)
    }
    var batteryLowTrigger by remember(settings.announceBatteryLow) {
        mutableStateOf(settings.announceBatteryLow)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("event_triggers_screen")
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
                    text = "Event Triggers",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
                Text(
                    text = "Hardware event reactive voice announcements",
                    fontSize = 12.sp,
                    color = MayaCyanNeon
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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Charger Connected Announcement",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaTextPrimary
                            )
                            Text(
                                text = "Maya playfully acknowledges when fast charger is connected.",
                                fontSize = 12.sp,
                                color = MayaTextSecondary
                            )
                        }
                        Switch(
                            checked = chargeTrigger,
                            onCheckedChange = { chargeTrigger = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = MayaCyanNeon)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Low Battery 20% Alert",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaTextPrimary
                            )
                            Text(
                                text = "Spoken gentle reminder to plug in your phone.",
                                fontSize = 12.sp,
                                color = MayaTextSecondary
                            )
                        }
                        Switch(
                            checked = batteryLowTrigger,
                            onCheckedChange = { batteryLowTrigger = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = MayaCyanNeon)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        repository.preferences.updateSettings {
                            it.copy(
                                announceChargingConnected = chargeTrigger,
                                announceBatteryLow = batteryLowTrigger
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MayaCyanNeon),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Save Trigger Preferences", color = Color(0xFF041424), fontWeight = FontWeight.Bold)
            }
        }
    }
}
