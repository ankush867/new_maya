package com.example.presentation.advanced

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pattern
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.security.PhoneUnlockManager
import com.example.data.repository.AssistantRepository
import com.example.presentation.components.GlassCard
import com.example.presentation.components.PatternLockView
import com.example.presentation.security.AppLockScreen
import com.example.ui.theme.MayaAmberNeon
import com.example.ui.theme.MayaCardBorder
import com.example.ui.theme.MayaCardSurface
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaGreenNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaPinkNeon
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaTextMuted
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import kotlinx.coroutines.launch

@Composable
fun ScreenLockScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AssistantRepository.getInstance(context) }
    val settings by repository.settings.collectAsState()

    var isAccessibilityEnabled by remember {
        mutableStateOf(PhoneUnlockManager.isAccessibilityEnabled(context))
    }

    var unlockEnabled by remember(settings.phoneUnlockEnabled) {
        mutableStateOf(settings.phoneUnlockEnabled)
    }

    // Pattern state
    var drawnPattern by remember {
        mutableStateOf<List<Int>>(
            if (settings.phonePattern.isNotBlank()) {
                settings.phonePattern.split(",").mapNotNull { it.trim().toIntOrNull() }
            } else emptyList()
        )
    }
    var patternStatusMessage by remember { mutableStateOf<String?>(null) }

    // PIN state
    var pinValue by remember(settings.phonePin) {
        mutableStateOf(settings.phonePin)
    }
    var pinVisible by remember { mutableStateOf(false) }
    var pinStatusMessage by remember { mutableStateOf<String?>(null) }

    // Simulation Test Modal state
    var showTestSimulation by remember { mutableStateOf(false) }
    var showFineTuningDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAccessibilityEnabled = PhoneUnlockManager.isAccessibilityEnabled(context)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MayaNavyBackground)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
                .testTag("pattern_pin_screen")
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
                        text = "Pattern & PIN",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Text(
                        text = if (settings.phonePattern.isNotBlank() || settings.phonePin.isNotBlank())
                            "Configured for Maya Voice Unlock"
                        else
                            "Not set up",
                        fontSize = 12.sp,
                        color = if (settings.phonePattern.isNotBlank() || settings.phonePin.isNotBlank())
                            MayaGreenNeon
                        else
                            MayaAmberNeon
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Status Overview Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (settings.phonePattern.isNotBlank() || settings.phonePin.isNotBlank())
                                        MayaGreenNeon.copy(alpha = 0.15f)
                                    else
                                        MayaAmberNeon.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Status",
                                tint = if (settings.phonePattern.isNotBlank() || settings.phonePin.isNotBlank())
                                    MayaGreenNeon
                                else
                                    MayaAmberNeon,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (settings.phonePattern.isNotBlank() || settings.phonePin.isNotBlank())
                                    "Configured"
                                else
                                    "Not set up",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaTextPrimary
                            )
                            Text(
                                text = buildString {
                                    if (settings.phonePattern.isNotBlank()) append("Pattern: Saved • ")
                                    if (settings.phonePin.isNotBlank()) append("PIN: Saved • ")
                                    if (settings.phonePattern.isBlank() && settings.phonePin.isBlank()) {
                                        append("Configure PIN or Pattern to allow Maya to unlock hands-free")
                                    } else {
                                        append("Voice unlock ready")
                                    }
                                },
                                fontSize = 12.sp,
                                color = MayaTextSecondary
                            )
                        }
                    }
                }

                // 2. Enable Switch Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Let Maya unlock the phone",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaTextPrimary
                            )
                            Text(
                                text = "Save a pattern or a PIN below first.",
                                fontSize = 12.sp,
                                color = MayaTextSecondary
                            )
                        }
                        Switch(
                            checked = unlockEnabled,
                            onCheckedChange = { isChecked ->
                                unlockEnabled = isChecked
                                scope.launch {
                                    repository.preferences.updateSettings {
                                        it.copy(phoneUnlockEnabled = isChecked)
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MayaCyanNeon,
                                checkedTrackColor = Color(0xFF0E3A5A)
                            )
                        )
                    }
                }

                // 3. Accessibility Service Warning Banner (Screenshots 3 & 4)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF2A1C0E),
                    border = BorderStroke(1.dp, MayaAmberNeon.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = MayaAmberNeon,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Accessibility service OFF hai — Settings > Accessibility me 'Maya' ON kar do (app ki Permissions screen pe shortcut hai), phir bolo.",
                                fontSize = 12.sp,
                                color = Color(0xFFFDE68A),
                                lineHeight = 17.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { PhoneUnlockManager.openAccessibilitySettings(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = MayaAmberNeon),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Open Accessibility", color = Color(0xFF241400), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // 4. Pattern Card (Screenshot 3)
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
                                Icon(Icons.Default.Pattern, contentDescription = null, tint = MayaCyanNeon, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Pattern",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MayaTextPrimary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (settings.phonePattern.isNotBlank()) MayaGreenNeon.copy(alpha = 0.15f) else Color(0xFF334155),
                                border = BorderStroke(1.dp, if (settings.phonePattern.isNotBlank()) MayaGreenNeon.copy(alpha = 0.4f) else Color.Transparent)
                            ) {
                                Text(
                                    text = if (settings.phonePattern.isNotBlank()) "Saved" else "No pattern saved",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (settings.phonePattern.isNotBlank()) MayaGreenNeon else MayaTextMuted,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Draw the pattern you use on this phone's lock screen.",
                            fontSize = 12.sp,
                            color = MayaTextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Interactive Pattern Canvas
                        PatternLockView(
                            modifier = Modifier
                                .size(240.dp)
                                .align(Alignment.CenterHorizontally),
                            pattern = drawnPattern,
                            onPatternChange = { updated ->
                                drawnPattern = updated
                                patternStatusMessage = null
                            }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    drawnPattern = emptyList()
                                    patternStatusMessage = null
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MayaCardBorder)
                            ) {
                                Text("Clear", color = MayaTextSecondary)
                            }

                            Button(
                                onClick = {
                                    if (drawnPattern.size < 4) {
                                        patternStatusMessage = "Use at least 4 dots"
                                    } else {
                                        val patternStr = drawnPattern.joinToString(",")
                                        scope.launch {
                                            repository.preferences.updateSettings {
                                                it.copy(phonePattern = patternStr, phoneUnlockEnabled = true)
                                            }
                                            patternStatusMessage = "Pattern saved successfully!"
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MayaCyanNeon),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Save Pattern", color = MayaNavyBackground, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (patternStatusMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = patternStatusMessage!!,
                                fontSize = 12.sp,
                                color = if (patternStatusMessage!!.contains("saved", ignoreCase = true)) MayaGreenNeon else MayaPinkNeon,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MayaTextMuted, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Use at least 4 dots — Android requires that too.",
                                fontSize = 11.sp,
                                color = MayaTextMuted
                            )
                        }
                    }
                }

                // 5. PIN Card (Screenshot 4)
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
                                Icon(Icons.Default.Key, contentDescription = null, tint = MayaCyanNeon, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PIN",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MayaTextPrimary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (settings.phonePin.isNotBlank()) MayaGreenNeon.copy(alpha = 0.15f) else Color(0xFF334155),
                                border = BorderStroke(1.dp, if (settings.phonePin.isNotBlank()) MayaGreenNeon.copy(alpha = 0.4f) else Color.Transparent)
                            ) {
                                Text(
                                    text = if (settings.phonePin.isNotBlank()) "Saved" else "No PIN saved",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (settings.phonePin.isNotBlank()) MayaGreenNeon else MayaTextMuted,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "If this phone uses a PIN, put it here instead. A PIN is the more reliable of the two — Maya presses the real keypad buttons, so there is no position to get right.",
                            fontSize = 12.sp,
                            color = MayaTextSecondary,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = pinValue,
                            onValueChange = {
                                pinValue = it
                                pinStatusMessage = null
                            },
                            placeholder = { Text("Enter your phone PIN", color = MayaTextMuted) },
                            visualTransformation = if (pinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            trailingIcon = {
                                IconButton(onClick = { pinVisible = !pinVisible }) {
                                    Icon(
                                        imageVector = if (pinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle PIN Visibility",
                                        tint = MayaCyanNeon
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MayaCyanNeon,
                                unfocusedBorderColor = MayaCardBorder,
                                focusedTextColor = MayaTextPrimary,
                                unfocusedTextColor = MayaTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    pinValue = ""
                                    scope.launch {
                                        repository.preferences.updateSettings { it.copy(phonePin = "") }
                                        pinStatusMessage = "PIN removed"
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MayaCardBorder)
                            ) {
                                Text("Clear", color = MayaTextSecondary)
                            }

                            Button(
                                onClick = {
                                    if (pinValue.isBlank()) {
                                        pinStatusMessage = "Please enter a PIN"
                                    } else {
                                        scope.launch {
                                            repository.preferences.updateSettings {
                                                it.copy(phonePin = pinValue, phoneUnlockEnabled = true)
                                            }
                                            pinStatusMessage = "PIN saved successfully!"
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MayaCyanNeon),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Save PIN", color = MayaNavyBackground, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (pinStatusMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = pinStatusMessage!!,
                                fontSize = 12.sp,
                                color = if (pinStatusMessage!!.contains("saved", ignoreCase = true)) MayaGreenNeon else MayaPinkNeon,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // 6. Test Card (Screenshot 4)
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MayaGreenNeon, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Test",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Locks this phone, then tries to open it",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MayaTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "The screen goes dark for a couple of seconds. If it doesn't come back on its own, unlock it yourself — nothing is stuck.",
                            fontSize = 12.sp,
                            color = MayaTextSecondary,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                showTestSimulation = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MayaGreenNeon),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text(
                                "Lock and try to unlock",
                                color = Color(0xFF041424),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // 7. Fine-tuning row (Screenshot 4)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MayaCardSurface,
                    border = BorderStroke(1.dp, MayaCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showFineTuningDialog = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fine-tuning - Only if the test below fails",
                            fontSize = 13.sp,
                            color = MayaTextSecondary
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Fine-tuning",
                            tint = MayaTextMuted
                        )
                    }
                }
            }
        }

        // Test Lock Overlay Simulation
        if (showTestSimulation) {
            AppLockScreen(
                settings = settings,
                isSimulationTest = true,
                onUnlocked = {
                    showTestSimulation = false
                },
                onDismiss = {
                    showTestSimulation = false
                }
            )
        }

        // Fine-tuning Modal / Dialog
        if (showFineTuningDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showFineTuningDialog = false },
                title = { Text("Fine-Tuning Settings", color = MayaTextPrimary) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Swipe duration: 300 ms\nInput delay: 400 ms\nDot stroke duration: 500 ms",
                            color = MayaTextSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            "Accessibility gesture coordinates dynamically scale to your device screen resolution (${context.resources.displayMetrics.widthPixels} x ${context.resources.displayMetrics.heightPixels}).",
                            color = MayaTextMuted,
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showFineTuningDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MayaCyanNeon)
                    ) {
                        Text("Done", color = MayaNavyBackground)
                    }
                },
                containerColor = MayaCardSurface
            )
        }
    }
}
