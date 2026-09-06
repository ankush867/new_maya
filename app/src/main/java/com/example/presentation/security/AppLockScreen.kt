package com.example.presentation.security

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pattern
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.core.model.AppSettings
import com.example.core.security.PhoneUnlockManager
import com.example.presentation.components.PatternLockView
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
import kotlinx.coroutines.delay

@Composable
fun AppLockScreen(
    settings: AppSettings,
    onUnlocked: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    isSimulationTest: Boolean = false
) {
    var selectedTab by remember {
        mutableIntStateOf(if (settings.phonePattern.isNotBlank()) 0 else 1)
    }

    var enteredPattern by remember { mutableStateOf<List<Int>>(emptyList()) }
    var enteredPin by remember { mutableStateOf("") }
    var pinVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isVoiceListening by remember { mutableStateOf(false) }

    // Listen to voice unlock events triggered by Maya
    LaunchedEffect(Unit) {
        PhoneUnlockManager.unlockEvents.collect { event ->
            successMessage = "Voice Verified: Unlocked by Maya"
            delay(600)
            onUnlocked()
        }
    }

    // If simulation test, simulate auto-unlock after 2 seconds
    LaunchedEffect(isSimulationTest) {
        if (isSimulationTest) {
            delay(1500)
            successMessage = "Maya auto-unlock sequence executed"
            delay(800)
            onUnlocked()
        }
    }

    fun verifyPattern(pattern: List<Int>) {
        val patternStr = pattern.joinToString(",")
        if (patternStr == settings.phonePattern || (settings.phonePattern.isBlank() && pattern.size >= 4)) {
            successMessage = "Pattern accepted"
            onUnlocked()
        } else {
            errorMessage = "Incorrect pattern. Try again."
            enteredPattern = emptyList()
        }
    }

    fun verifyPin() {
        if (enteredPin == settings.phonePin || (settings.phonePin.isBlank() && enteredPin.length >= 4)) {
            successMessage = "PIN accepted"
            onUnlocked()
        } else {
            errorMessage = "Incorrect PIN. Try again."
            enteredPin = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .testTag("app_lock_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MayaPurpleNeon.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = MayaCyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isSimulationTest) "Test Lock Simulation" else "Maya Secure Lock",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                }

                if (onDismiss != null) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MayaTextMuted
                        )
                    }
                }
            }

            // Mode Selector Tabs (Pattern & PIN options)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MayaCardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MayaCardBorder),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MayaCyanNeon,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MayaCyanNeon
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            errorMessage = null
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Pattern, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pattern")
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            errorMessage = null
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("PIN / Pass")
                            }
                        }
                    )
                }
            }

            // Feedback / Status message
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (successMessage != null) {
                    Text(
                        text = successMessage!!,
                        color = MayaGreenNeon,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MayaPinkNeon,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = if (selectedTab == 0) "Draw your saved pattern to open" else "Enter your saved PIN / password",
                        color = MayaTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            // Interactive Input Area
            if (selectedTab == 0) {
                // Pattern Lock View
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PatternLockView(
                        modifier = Modifier.size(280.dp),
                        pattern = enteredPattern,
                        onPatternChange = { updated ->
                            enteredPattern = updated
                            errorMessage = null
                            if (updated.size >= 4 && settings.phonePattern.isNotBlank()) {
                                if (updated.size == settings.phonePattern.split(",").size) {
                                    verifyPattern(updated)
                                }
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { enteredPattern = emptyList() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Clear", color = MayaTextSecondary)
                        }

                        Button(
                            onClick = { verifyPattern(enteredPattern) },
                            enabled = enteredPattern.size >= 4,
                            colors = ButtonDefaults.buttonColors(containerColor = MayaCyanNeon),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Unlock", color = MayaNavyBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // PIN Input View
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = {
                            if (it.length <= 12) {
                                enteredPin = it
                                errorMessage = null
                            }
                        },
                        placeholder = { Text("Enter PIN or Password", color = MayaTextMuted) },
                        visualTransformation = if (pinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        trailingIcon = {
                            IconButton(onClick = { pinVisible = !pinVisible }) {
                                Icon(
                                    imageVector = if (pinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Visibility",
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { verifyPin() },
                        enabled = enteredPin.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MayaCyanNeon),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Unlock", color = MayaNavyBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            // Bottom Voice Unlock Prompt
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MayaCardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MayaPurpleNeon.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isVoiceListening = true
                        // Simulate Maya recognizing "phone open karo"
                        successMessage = "Listening: 'Maya phone unlock karo'..."
                        PhoneUnlockManager.triggerUnlock(
                            context = null ?: com.example.service.MayaAccessibilityService.instance ?: return@clickable,
                            pattern = settings.phonePattern,
                            pin = settings.phonePin,
                            byVoice = true
                        ) { success, _ ->
                            if (success) {
                                successMessage = "Maya: Phone unlocked with saved credentials!"
                                onUnlocked()
                            }
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MayaCyanNeon.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Unlock",
                            tint = MayaCyanNeon,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Voice-Activated Unlock",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MayaCyanNeon
                        )
                        Text(
                            text = "Say: 'Maya mera phone open karo' / 'phone unlock karo'",
                            fontSize = 11.sp,
                            color = MayaTextSecondary
                        )
                    }
                }
            }
        }
    }
}
