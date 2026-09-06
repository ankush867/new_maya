package com.example.presentation.settings

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.theme.MayaCardBorder
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaGreenNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import kotlinx.coroutines.launch

@Composable
fun WhatsAppSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AssistantRepository.getInstance(context) }
    val settings by repository.settings.collectAsState()

    var autoReplyEnabled by remember(settings.whatsAppAutoReplyEnabled) {
        mutableStateOf(settings.whatsAppAutoReplyEnabled)
    }
    var autoReplyMessage by remember {
        mutableStateOf("Hi! I'm currently away. Maya AI Assistant will notify me of your message.")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("whatsapp_settings_screen")
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
            Text(
                text = "WhatsApp Integration",
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
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Voice-to-Chat Execution",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Text(
                        text = "Say e.g. 'Maya send WhatsApp to Rahul: reach here by 5 PM'. Maya opens WhatsApp directly with the contact and message ready.",
                        fontSize = 12.sp,
                        color = MayaTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Reply Assistant",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MayaTextPrimary
                            )
                            Text(
                                text = "Suggest automated responses when busy",
                                fontSize = 11.sp,
                                color = MayaTextSecondary
                            )
                        }
                        Switch(
                            checked = autoReplyEnabled,
                            onCheckedChange = { autoReplyEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MayaGreenNeon,
                                checkedTrackColor = Color(0xFF064E3B)
                            )
                        )
                    }

                    if (autoReplyEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Auto-Reply Template",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MayaTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = autoReplyMessage,
                            onValueChange = { autoReplyMessage = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MayaGreenNeon,
                                unfocusedBorderColor = MayaCardBorder,
                                focusedTextColor = MayaTextPrimary,
                                unfocusedTextColor = MayaTextPrimary
                            )
                        )
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        repository.preferences.updateSettings {
                            it.copy(whatsAppAutoReplyEnabled = autoReplyEnabled)
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MayaGreenNeon),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Save WhatsApp Settings", color = Color(0xFF041424), fontWeight = FontWeight.Bold)
            }
        }
    }
}
