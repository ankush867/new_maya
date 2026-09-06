package com.example.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TextToSpeechHelper(private val context: Context) {
    companion object {
        private const val TAG = "TextToSpeechHelper"
    }

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        initTTS()
    }

    private var pendingOnDone: (() -> Unit)? = null

    private fun initTTS() {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.let { engine ->
                    try {
                        val audioAttributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                        engine.setAudioAttributes(audioAttributes)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to set audio attributes: ${e.message}")
                    }

                    val result = engine.setLanguage(Locale("hi", "IN"))
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        engine.language = Locale.ENGLISH
                    }
                    engine.setPitch(1.05f)
                    engine.setSpeechRate(1.0f)

                    engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isSpeaking.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            _isSpeaking.value = false
                            val cb = pendingOnDone
                            pendingOnDone = null
                            cb?.invoke()
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            _isSpeaking.value = false
                            val cb = pendingOnDone
                            pendingOnDone = null
                            cb?.invoke()
                        }

                        override fun onError(utteranceId: String?, errorCode: Int) {
                            _isSpeaking.value = false
                            val cb = pendingOnDone
                            pendingOnDone = null
                            cb?.invoke()
                        }
                    })
                    isInitialized = true
                }
            } else {
                Log.e(TAG, "TTS Initialization failed: $status")
            }
        }
    }

    fun speak(text: String, speed: Float = 1.0f, onDone: (() -> Unit)? = null) {
        if (!isInitialized || tts == null) {
            initTTS()
        }
        pendingOnDone = onDone
        try {
            tts?.setSpeechRate(speed.coerceIn(0.7f, 2.0f))
            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
                putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "MAYA_SPEECH_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e(TAG, "Error speaking text", e)
            pendingOnDone = null
            onDone?.invoke()
        }
    }

    fun stop() {
        try {
            tts?.stop()
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down TTS", e)
        }
    }
}
