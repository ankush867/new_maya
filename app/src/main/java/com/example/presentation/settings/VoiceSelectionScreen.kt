package com.example.presentation.settings

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import com.example.ui.theme.MayaAmberNeon
import com.example.ui.theme.MayaCardBorder
import com.example.ui.theme.MayaCardSurface
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaGreenNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaTextMuted
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import kotlinx.coroutines.launch
import java.util.Locale

data class VoiceOption(
    val title: String,
    val code: String,
    val description: String,
    val pitch: Float = 1.0f,
    val speed: Float = 1.0f,
    val previewPhrase: String = "Hello! Mai Maya hu, aapki personal AI assistant. Meri aawaz ab bilkul saaf aur loud sunayi de rahi hai."
)

@Composable
fun VoiceSelectionScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AssistantRepository.getInstance(context) }
    val settings by repository.settings.collectAsState()

    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager }
    val maxMediaVolume = remember { audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15 }
    var currentMediaVolume by remember {
        mutableIntStateOf(audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: maxMediaVolume)
    }

    var selectedVoiceCode by remember(settings.assistantVoice) {
        mutableStateOf(settings.assistantVoice)
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var playingCode by remember { mutableStateOf<String?>(null) }
    var saveFeedback by remember { mutableStateOf(false) }

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                try {
                    val audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                    ttsInstance?.setAudioAttributes(audioAttributes)
                } catch (e: Exception) {
                    // Fallback to default
                }
                val langResult = ttsInstance?.setLanguage(Locale("hi", "IN"))
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    ttsInstance?.language = Locale.ENGLISH
                }
                ttsInstance?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        playingCode = null
                    }
                    override fun onError(utteranceId: String?) {
                        playingCode = null
                    }
                })
            }
        }
        tts = ttsInstance

        onDispose {
            ttsInstance?.stop()
            ttsInstance?.shutdown()
        }
    }

    // Voice lists per tab with crisp, clear frequencies and loud playback tuning
    val mayaVoices = listOf(
        VoiceOption("Breezy", "Aoede", "Warm, lively & confident (Recommended for Maya)", 1.15f, 1.12f),
        VoiceOption("Firm", "Kore", "Calm, soothing & crystal clear", 1.02f, 1.06f),
        VoiceOption("Youthful", "Leda", "Energetic, articulate & bright", 1.22f, 1.15f),
        VoiceOption("Bright", "Zephyr", "Gentle, empathetic & clear", 1.08f, 1.08f),
        VoiceOption("Upbeat", "Laomedeia", "Playful & optimistic", 1.18f, 1.15f),
        VoiceOption("Smooth", "Despina", "Velvety & sophisticated", 1.00f, 1.06f),
        VoiceOption("Clear", "Erinome", "Articulate & concise", 1.05f, 1.10f),
        VoiceOption("Easy-going", "Callirrhoe", "Relaxed & natural conversationalist", 1.06f, 1.10f),
        VoiceOption("Radiant", "Autonoe", "High clarity and audible projection", 1.15f, 1.14f),
        VoiceOption("Deep", "Fenrir", "Rich & resonant without muffled tone", 0.90f, 1.05f),
        VoiceOption("Playful", "Puck", "Bouncy & witty", 1.25f, 1.18f)
    )

    val fridayVoices = listOf(
        VoiceOption("Cortex", "Friday-1", "Crisp, British technical executive assistant", 1.08f, 1.12f, "Online and ready, Sir. Systems operating at full volume and capacity."),
        VoiceOption("Stark", "Friday-2", "Analytical & composed precision intelligence", 1.02f, 1.08f, "Diagnostic complete. Audio levels optimal. How may Friday assist you?"),
        VoiceOption("Echo", "Friday-3", "Modern sleek concierge voice", 1.10f, 1.14f, "Task schedule updated and voice synchronized."),
        VoiceOption("Atlas", "Friday-4", "Commanding & strategic vocal profile", 0.96f, 1.06f, "Standing by for directives. Speech channels clear.")
    )

    val venomVoices = listOf(
        VoiceOption("Gargoyle", "Venom-1", "Deep, resonant & authoritative", 0.80f, 1.05f, "We are listening. Our voice is loud and clear... speak!"),
        VoiceOption("Shadow", "Venom-2", "Phantom metallic tone", 0.84f, 1.08f, "The darkness responds to your command with full power."),
        VoiceOption("Titan", "Venom-3", "Heavy, seismic resonant voice", 0.78f, 1.02f, "Raw power unlocked. Maximum audio output active."),
        VoiceOption("Phantom", "Venom-4", "Distorted cybernetic edge", 0.88f, 1.12f, "Neural overdrive activated. Audio transmission at full strength.")
    )

    val currentVoices = when (selectedTab) {
        1 -> fridayVoices
        2 -> venomVoices
        else -> mayaVoices
    }

    fun playPreview(voice: VoiceOption) {
        if (playingCode == voice.code) {
            tts?.stop()
            playingCode = null
            return
        }

        // Auto boost volume if it's currently low
        if (currentMediaVolume < (maxMediaVolume * 0.5f).toInt()) {
            val boostTarget = (maxMediaVolume * 0.85f).toInt()
            audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, boostTarget, 0)
            currentMediaVolume = boostTarget
        }

        playingCode = voice.code
        tts?.apply {
            stop()
            setPitch(voice.pitch)
            setSpeechRate(voice.speed)
            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
                putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
            }
            speak(voice.previewPhrase, TextToSpeech.QUEUE_FLUSH, params, "voice_preview_${voice.code}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .testTag("voice_selection_screen")
    ) {
        // Top Header
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
                    text = "Voice",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
                Text(
                    text = "Choose Maya's voice persona & preview with full volume",
                    fontSize = 12.sp,
                    color = MayaCyanNeon
                )
            }
        }

        // Live Audio & Volume Boost Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Volume",
                            tint = MayaCyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Media Volume: ${((currentMediaVolume.toFloat() / maxMediaVolume) * 100).toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MayaTextPrimary
                        )
                    }

                    Button(
                        onClick = {
                            audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, maxMediaVolume, AudioManager.FLAG_SHOW_UI)
                            currentMediaVolume = maxMediaVolume
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MayaCyanNeon.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MayaCyanNeon),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Boost 100%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MayaCyanNeon)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = currentMediaVolume.toFloat(),
                    onValueChange = { newVal ->
                        val vol = newVal.toInt()
                        currentMediaVolume = vol
                        audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0)
                    },
                    valueRange = 0f..maxMediaVolume.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = MayaCyanNeon,
                        activeTrackColor = MayaCyanNeon,
                        inactiveTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Tip: Phone buttons often only change Ringtone volume. Keep Media Volume at 100% for loud & crisp voice synthesis.",
                    fontSize = 10.sp,
                    color = MayaTextMuted,
                    lineHeight = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Tabs: Maya / Friday / Venom (Screenshot 18)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MayaCardSurface,
            border = BorderStroke(1.dp, MayaCardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
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
                    onClick = { selectedTab = 0 },
                    text = { Text("Maya", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Friday", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Venom", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Voices List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(currentVoices) { v ->
                val isSelected = selectedVoiceCode.equals(v.code, ignoreCase = true) ||
                        selectedVoiceCode.equals(v.title, ignoreCase = true)
                val isPlaying = playingCode == v.code

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MayaCyanNeon.copy(alpha = 0.12f) else MayaCardSurface,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MayaCyanNeon else MayaCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            selectedVoiceCode = v.code
                            scope.launch {
                                repository.preferences.updateSettings {
                                    it.copy(assistantVoice = v.code)
                                }
                                saveFeedback = true
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Checkmark circle on left
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MayaCyanNeon else Color(0xFF1E293B)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MayaNavyBackground,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "${v.title} (${v.code})",
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) MayaCyanNeon else MayaTextPrimary
                                )
                                Text(
                                    text = v.description,
                                    fontSize = 12.sp,
                                    color = MayaTextSecondary
                                )
                            }
                        }

                        // Play Preview Button on right
                        IconButton(
                            onClick = { playPreview(v) },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isPlaying) MayaCyanNeon else Color(0xFF1E293B))
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = "Play voice preview",
                                tint = if (isPlaying) MayaNavyBackground else MayaCyanNeon,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedVisibility(visible = saveFeedback) {
                    Text(
                        text = "Voice updated to $selectedVoiceCode",
                        color = MayaGreenNeon,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Button(
                    onClick = {
                        scope.launch {
                            repository.preferences.updateSettings {
                                it.copy(assistantVoice = selectedVoiceCode)
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
                    Text("Apply Voice", color = MayaNavyBackground, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
