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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.theme.MayaCardSurface
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AssistantRepository.getInstance(context) }
    val settings by repository.settings.collectAsState()

    var userName by remember(settings.userName) { mutableStateOf(settings.userName) }
    var assistantName by remember(settings.assistantName) { mutableStateOf(settings.assistantName) }
    var userGender by remember(settings.userGender) { mutableStateOf(settings.userGender) }
    var userPhoneNumber by remember(settings.userPhoneNumber) { mutableStateOf(settings.userPhoneNumber) }
    var musicApp by remember(settings.musicApp) { mutableStateOf(settings.musicApp) }
    var favoriteSong by remember(settings.favoriteSong) { mutableStateOf(settings.favoriteSong) }
    var pollinationsToken by remember(settings.pollinationsToken) { mutableStateOf(settings.pollinationsToken) }
    var language by remember(settings.preferredLanguage) { mutableStateOf(settings.preferredLanguage) }
    var customApiKey by remember(settings.customApiKey) { mutableStateOf(settings.customApiKey) }
    var langExpanded by remember { mutableStateOf(false) }

    val languages = listOf(
        "Auto (English / Hindi / Hinglish)",
        "English (US / UK / Global)",
        "Hindi (हिंदी)",
        "Spanish (Español)",
        "French (Français)",
        "German (Deutsch)",
        "Japanese (日本語)"
    )

    val genderOptions = listOf("Male", "Female", "Non-binary", "Prefer not to say")
    val musicApps = listOf("Spotify", "YouTube Music", "Apple Music", "Gaana", "JioSaavn")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("personal_settings_screen")
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
                text = "Personal Profile & Names",
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
            // User Name Field
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Your Name (Owner / Master)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Text(
                        text = "The assistant will strictly bond to and address you by this name.",
                        fontSize = 12.sp,
                        color = MayaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        modifier = Modifier.fillMaxWidth().testTag("user_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MayaCyanNeon,
                            unfocusedBorderColor = MayaCardBorder,
                            focusedTextColor = MayaTextPrimary,
                            unfocusedTextColor = MayaTextPrimary
                        )
                    )
                }
            }

            // Assistant Name Field
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AI Assistant / Girlfriend Name",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Text(
                        text = "Set your custom name for your AI companion (e.g., Maya, Zoya, Priya).",
                        fontSize = 12.sp,
                        color = MayaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = assistantName,
                        onValueChange = { assistantName = it },
                        modifier = Modifier.fillMaxWidth().testTag("assistant_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MayaPurpleNeon,
                            unfocusedBorderColor = MayaCardBorder,
                            focusedTextColor = MayaTextPrimary,
                            unfocusedTextColor = MayaTextPrimary
                        )
                    )
                }
            }

            // Gender & Phone Number (Screenshots 1 & 2)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Your Gender & Identity",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        genderOptions.take(2).forEach { opt ->
                            val isSelected = userGender.equals(opt, ignoreCase = true)
                            androidx.compose.material3.FilterChip(
                                selected = isSelected,
                                onClick = { userGender = opt },
                                label = { Text(opt, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        genderOptions.takeLast(2).forEach { opt ->
                            val isSelected = userGender.equals(opt, ignoreCase = true)
                            androidx.compose.material3.FilterChip(
                                selected = isSelected,
                                onClick = { userGender = opt },
                                label = { Text(opt, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Your Phone Number",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Text(
                        text = "Used for auto-contact recognition and emergency SMS alert dispatch.",
                        fontSize = 12.sp,
                        color = MayaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = userPhoneNumber,
                        onValueChange = { userPhoneNumber = it },
                        placeholder = { Text("+91 98765 43210", color = Color(0xFF64748B)) },
                        modifier = Modifier.fillMaxWidth().testTag("user_phone_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MayaCyanNeon,
                            unfocusedBorderColor = MayaCardBorder,
                            focusedTextColor = MayaTextPrimary,
                            unfocusedTextColor = MayaTextPrimary
                        ),
                        singleLine = true
                    )
                }
            }

            // Music Preferences (Screenshots 1 & 2)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Preferred Music Player",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Text(
                        text = "Maya will launch this app when you say 'Play music'.",
                        fontSize = 12.sp,
                        color = MayaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        musicApps.take(3).forEach { app ->
                            val isSelected = musicApp.equals(app, ignoreCase = true)
                            androidx.compose.material3.FilterChip(
                                selected = isSelected,
                                onClick = { musicApp = app },
                                label = { Text(app, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Favorite Song or Artist",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = favoriteSong,
                        onValueChange = { favoriteSong = it },
                        placeholder = { Text("e.g. Tum Hi Ho / Arijit Singh", color = Color(0xFF64748B)) },
                        modifier = Modifier.fillMaxWidth().testTag("favorite_song_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MayaPurpleNeon,
                            unfocusedBorderColor = MayaCardBorder,
                            focusedTextColor = MayaTextPrimary,
                            unfocusedTextColor = MayaTextPrimary
                        ),
                        singleLine = true
                    )
                }
            }

            // Language Selection Dropdown
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Spoken Language & Accent",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Text(
                        text = "Maya automatically responds in Hinglish, Hindi, or English based on how you speak.",
                        fontSize = 12.sp,
                        color = MayaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = langExpanded,
                        onExpandedChange = { langExpanded = !langExpanded }
                    ) {
                        OutlinedTextField(
                            value = language,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = langExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("language_selector"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MayaCyanNeon,
                                unfocusedBorderColor = MayaCardBorder,
                                focusedTextColor = MayaTextPrimary,
                                unfocusedTextColor = MayaTextPrimary
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = langExpanded,
                            onDismissRequest = { langExpanded = false },
                            modifier = Modifier.background(MayaCardSurface)
                        ) {
                            languages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang, color = MayaTextPrimary) },
                                    onClick = {
                                        language = lang
                                        langExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Gemini API Key Override Field
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "API Key",
                            tint = MayaPurpleNeon,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Custom Gemini API Key",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MayaTextPrimary
                        )
                    }
                    Text(
                        text = "Optional. Override with your own Google AI Studio Gemini API Key for higher quotas.",
                        fontSize = 12.sp,
                        color = MayaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customApiKey,
                        onValueChange = { customApiKey = it },
                        placeholder = { Text("AIzaSy...", color = Color(0xFF64748B)) },
                        modifier = Modifier.fillMaxWidth().testTag("custom_api_key_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MayaPurpleNeon,
                            unfocusedBorderColor = MayaCardBorder,
                            focusedTextColor = MayaTextPrimary,
                            unfocusedTextColor = MayaTextPrimary
                        ),
                        singleLine = true
                    )
                }
            }

            // Save Button
            Button(
                onClick = {
                    scope.launch {
                        repository.preferences.updateSettings {
                            it.copy(
                                userName = userName,
                                assistantName = assistantName,
                                userGender = userGender,
                                userPhoneNumber = userPhoneNumber,
                                musicApp = musicApp,
                                favoriteSong = favoriteSong,
                                pollinationsToken = pollinationsToken,
                                preferredLanguage = language,
                                customApiKey = customApiKey
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_personal_settings_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MayaCyanNeon),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save",
                    tint = Color(0xFF041424),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Profile Changes",
                    color = Color(0xFF041424),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
