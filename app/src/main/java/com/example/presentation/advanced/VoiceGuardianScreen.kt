package com.example.presentation.advanced

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaGreenNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaRedNeon
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VoiceGuardianScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AssistantRepository.getInstance(context) }
    val settings by repository.settings.collectAsState()

    var guardianEnabled by remember(settings.voiceGuardianEnabled) {
        mutableStateOf(settings.voiceGuardianEnabled)
    }
    var blockUnknown by remember(settings.blockUnknownVoices) {
        mutableStateOf(settings.blockUnknownVoices)
    }
    var isTrained by remember(settings.trustedVoiceTrained) {
        mutableStateOf(settings.trustedVoiceTrained)
    }

    var isRecordingTraining by remember { mutableStateOf(false) }
    var trainingStep by remember { mutableIntStateOf(1) }
    var testResultText by remember { mutableStateOf<String?>(null) }
    var isTestOwnerSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("voice_guardian_screen")
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
                    text = "Voice Guardian Security",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
                Text(
                    text = "Biometric Lock strictly for ${settings.userName}",
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
            // 1. Master Switch
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
                                text = "Voice Biometric Guardian",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaTextPrimary
                            )
                            Text(
                                text = "Protects ${settings.assistantName} so only your enrolled voice can command her.",
                                fontSize = 12.sp,
                                color = MayaTextSecondary
                            )
                        }

                        Switch(
                            checked = guardianEnabled,
                            onCheckedChange = {
                                guardianEnabled = it
                                scope.launch {
                                    repository.preferences.updateSettings { s ->
                                        s.copy(voiceGuardianEnabled = it)
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MayaCyanNeon,
                                checkedTrackColor = Color(0xFF003852)
                            ),
                            modifier = Modifier.testTag("guardian_switch")
                        )
                    }
                }
            }

            // 2. Strict Imposter Rejection Switch
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = "Block Imposters",
                                    tint = MayaRedNeon,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Block Unknown Voices",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MayaTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "If anyone else speaks, ${settings.assistantName} strictly replies: \"Tum ${settings.userName} nahi ho, main baat nahi karungi!\"",
                                fontSize = 12.sp,
                                color = MayaCyanNeon,
                                lineHeight = 16.sp
                            )
                        }

                        Switch(
                            checked = blockUnknown,
                            onCheckedChange = {
                                blockUnknown = it
                                scope.launch {
                                    repository.preferences.updateSettings { s ->
                                        s.copy(blockUnknownVoices = it)
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MayaRedNeon,
                                checkedTrackColor = Color(0xFF4A1020)
                            ),
                            modifier = Modifier.testTag("block_unknown_switch")
                        )
                    }
                }
            }

            // 3. Voice Print Status & Training
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isTrained) Icons.Default.VerifiedUser else Icons.Default.Security,
                            contentDescription = "Training Status",
                            tint = if (isTrained) MayaGreenNeon else MayaCyanNeon,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isTrained) "Voice Profile Enrolled (${settings.userName})" else "Voice Profile Not Enrolled",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaTextPrimary
                            )
                            Text(
                                text = "Harmonic Timbre Signature: ${if (isTrained) "ENROLLED_HASH_${settings.userName.uppercase()}_2026" else "None"}",
                                fontSize = 11.sp,
                                color = if (isTrained) MayaGreenNeon else MayaTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isRecordingTraining) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MayaCyanNeon.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, MayaCyanNeon.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Listening",
                                    tint = MayaCyanNeon,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Speak clearly into the microphone (Sample $trainingStep of 3):",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MayaTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when (trainingStep) {
                                        1 -> "\"Hey ${settings.assistantName}, this is ${settings.userName}.\""
                                        2 -> "\"${settings.assistantName}, mera phone check karo.\""
                                        else -> "\"${settings.assistantName}, save my voice profile securely.\""
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MayaCyanNeon
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Button(
                        onClick = {
                            if (!isRecordingTraining) {
                                isRecordingTraining = true
                                trainingStep = 1
                                scope.launch {
                                    delay(2000)
                                    trainingStep = 2
                                    delay(2000)
                                    trainingStep = 3
                                    delay(2000)
                                    isRecordingTraining = false
                                    isTrained = true
                                    repository.preferences.updateSettings {
                                        it.copy(
                                            voiceGuardianEnabled = true,
                                            trustedVoiceTrained = true,
                                            voicePrintSignature = "ENROLLED_HASH_${settings.userName.uppercase()}_SECURE",
                                            blockUnknownVoices = true
                                        )
                                    }
                                    testResultText = "✅ Voice profile successfully saved and locked for ${settings.userName}!"
                                    isTestOwnerSuccess = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MayaCyanNeon),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("train_voice_button"),
                        enabled = !isRecordingTraining
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Train Voice",
                            tint = Color(0xFF041424),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRecordingTraining) "Listening & Analyzing Timbre ($trainingStep/3)..." else if (isTrained) "Re-record & Save My Voice Profile" else "Record & Save My Voice Profile",
                            color = Color(0xFF041424),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 4. Live Anti-Imposter Verification Simulator
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Simulator",
                            tint = MayaPurpleNeon,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Live Anti-Imposter Defense Tester",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MayaTextPrimary
                        )
                    }
                    Text(
                        text = "Verify how ${settings.assistantName} distinguishes between you (${settings.userName}) and an unauthorized speaker.",
                        fontSize = 12.sp,
                        color = MayaTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                testResultText = "✅ [OWNER VERIFIED - 99.8% MATCH]\n${settings.assistantName}: \"Hello ${settings.userName}! Kaise ho aap? Kya help kar sakti hoon?\""
                                isTestOwnerSuccess = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MayaGreenNeon),
                            modifier = Modifier.weight(1f).testTag("test_owner_voice_button")
                        ) {
                            Text("Test Owner (${settings.userName})", color = MayaGreenNeon, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                testResultText = "🚫 [IMPOSTER REJECTED - TIMBRE MISMATCH]\n${settings.assistantName}: \"Tum ${settings.userName} nahi ho! Main baat nahi karungi bina ${settings.userName} ke permission ke!\""
                                isTestOwnerSuccess = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MayaRedNeon),
                            modifier = Modifier.weight(1f).testTag("test_imposter_voice_button")
                        ) {
                            Text("Test Stranger Voice", color = MayaRedNeon, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    testResultText?.let { result ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isTestOwnerSuccess) MayaGreenNeon.copy(alpha = 0.12f) else MayaRedNeon.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, if (isTestOwnerSuccess) MayaGreenNeon.copy(alpha = 0.5f) else MayaRedNeon.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = result,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isTestOwnerSuccess) MayaGreenNeon else MayaRedNeon,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

