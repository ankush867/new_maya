package com.example.presentation.advanced

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
fun ThemeSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AssistantRepository.getInstance(context) }
    val settings by repository.settings.collectAsState()

    var typeface by remember(settings.themeTypeface) { mutableStateOf(settings.themeTypeface) }
    var fontSize by remember(settings.themeFontSize) { mutableFloatStateOf(settings.themeFontSize.toFloatOrNull() ?: 1.0f) }
    var surfaceStyle by remember(settings.themeSurfaceStyle) { mutableStateOf(settings.themeSurfaceStyle) }
    var cornerStyle by remember(settings.themeCornerStyle) { mutableStateOf(settings.themeCornerStyle) }

    var orbStyle by remember(settings.orbStyle) { mutableStateOf(settings.orbStyle) }
    var orbColor by remember(settings.orbColorName) { mutableStateOf(settings.orbColorName) }
    var orbSize by remember(settings.orbSizeDp) { mutableFloatStateOf(settings.orbSizeDp.toFloat()) }
    var useOrbOnHome by remember(settings.useOrbOnHome) { mutableStateOf(settings.useOrbOnHome) }

    val fontOptions = listOf("System", "Mono", "Serif", "Geometric")
    val surfaceOptions = listOf("Frosted Glass", "Solid", "Deep Matte")
    val cornerOptions = listOf("Square", "Rounded", "Full Pill")
    val orbStyles = listOf("Cosmic Pulse", "Cyber Neon", "Minimal Ring", "Particle Sphere")

    val colorPalettes = listOf(
        Pair("Cyan", MayaCyanNeon),
        Pair("Purple", MayaPurpleNeon),
        Pair("Green", MayaGreenNeon),
        Pair("Pink", MayaPinkNeon),
        Pair("Amber", MayaAmberNeon),
        Pair("Electric Blue", Color(0xFF38BDF8))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("theme_settings_screen")
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
                    text = "Theme & Visuals",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
                Text(
                    text = "Typography, surfaces, accents & dynamic orb",
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
            // 1. Typography Section (Screenshots 5 & 6)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TextFields, contentDescription = null, tint = MayaCyanNeon, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FONT & TYPOGRAPHY", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MayaTextMuted)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Typeface", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MayaTextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        fontOptions.forEach { opt ->
                            val isSelected = typeface.equals(opt, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MayaCyanNeon.copy(alpha = 0.2f) else MayaCardSurface,
                                border = BorderStroke(1.dp, if (isSelected) MayaCyanNeon else MayaCardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { typeface = opt }
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MayaCyanNeon else MayaTextSecondary,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Text Size Scale", fontSize = 13.sp, color = MayaTextSecondary)
                        Text("${"%.1f".format(fontSize)}x", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MayaCyanNeon)
                    }
                    Slider(
                        value = fontSize,
                        onValueChange = { fontSize = it },
                        valueRange = 0.85f..1.25f,
                        steps = 4,
                        colors = SliderDefaults.colors(thumbColor = MayaCyanNeon, activeTrackColor = MayaCyanNeon)
                    )
                }
            }

            // 2. Surface & Corners Section
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wallpaper, contentDescription = null, tint = MayaPurpleNeon, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SURFACES & CORNERS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MayaTextMuted)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Surface Glassmorphism", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MayaTextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        surfaceOptions.forEach { opt ->
                            val isSelected = surfaceStyle.equals(opt, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MayaPurpleNeon.copy(alpha = 0.2f) else MayaCardSurface,
                                border = BorderStroke(1.dp, if (isSelected) MayaPurpleNeon else MayaCardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { surfaceStyle = opt }
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MayaPurpleNeon else MayaTextSecondary,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Corner Curvature", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MayaTextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        cornerOptions.forEach { opt ->
                            val isSelected = cornerStyle.equals(opt, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MayaCyanNeon.copy(alpha = 0.15f) else MayaCardSurface,
                                border = BorderStroke(1.dp, if (isSelected) MayaCyanNeon else MayaCardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { cornerStyle = opt }
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MayaCyanNeon else MayaTextSecondary,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // 3. Orb Customization (Screenshots 7 & 8)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = MayaCyanNeon, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ORB CUSTOMIZATION", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MayaTextMuted)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Visual Style", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MayaTextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        orbStyles.take(2).forEach { opt ->
                            val isSelected = orbStyle.equals(opt, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MayaCyanNeon.copy(alpha = 0.18f) else MayaCardSurface,
                                border = BorderStroke(1.dp, if (isSelected) MayaCyanNeon else MayaCardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { orbStyle = opt }
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MayaCyanNeon else MayaTextSecondary,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        orbStyles.takeLast(2).forEach { opt ->
                            val isSelected = orbStyle.equals(opt, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MayaCyanNeon.copy(alpha = 0.18f) else MayaCardSurface,
                                border = BorderStroke(1.dp, if (isSelected) MayaCyanNeon else MayaCardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { orbStyle = opt }
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MayaCyanNeon else MayaTextSecondary,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Accent Color Palette", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MayaTextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        colorPalettes.forEach { (name, color) ->
                            val isSelected = orbColor.equals(name, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { orbColor = name },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MayaNavyBackground,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Use Orb on Home Screen", fontSize = 14.sp, color = MayaTextPrimary)
                        Switch(
                            checked = useOrbOnHome,
                            onCheckedChange = { useOrbOnHome = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = MayaCyanNeon, checkedTrackColor = Color(0xFF003852))
                        )
                    }
                }
            }

            // Save Button
            Button(
                onClick = {
                    scope.launch {
                        repository.preferences.updateSettings {
                            it.copy(
                                themeTypeface = typeface,
                                themeFontSize = "%.1f".format(fontSize),
                                themeSurfaceStyle = surfaceStyle,
                                themeCornerStyle = cornerStyle,
                                orbStyle = orbStyle,
                                orbColorName = orbColor,
                                orbSizeDp = orbSize.toInt(),
                                useOrbOnHome = useOrbOnHome
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MayaCyanNeon),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Save Theme & Visuals", color = MayaNavyBackground, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
