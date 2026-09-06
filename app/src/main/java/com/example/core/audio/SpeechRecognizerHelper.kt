package com.example.core.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechRecognizerHelper(private val context: Context) {
    companion object {
        private const val TAG = "SpeechRecognizerHelper"
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _rmsAmplitude = MutableStateFlow(0f)
    val rmsAmplitude: StateFlow<Float> = _rmsAmplitude.asStateFlow()

    var onSpeechResult: ((String) -> Unit)? = null
    var onSpeechPartialResult: ((String) -> Unit)? = null
    var onSpeechStateChange: ((Boolean) -> Unit)? = null

    private var isContinuousMode = false

    private var isRecognitionActive = false

    fun startListening(languageCode: String = "hi-IN", continuous: Boolean = false) {
        isContinuousMode = continuous
        isRecognitionActive = true
        mainHandler.post {
            try {
                if (speechRecognizer == null) {
                    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                        Log.w(TAG, "Speech recognition not available on device")
                        isRecognitionActive = false
                        return@post
                    }
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext)
                    setupListener()
                }

                speechRecognizer?.cancel()

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageCode)
                    putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageCode)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000L)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2500L)
                    putExtra("android.speech.extra.DICTATION_MODE", true)
                }

                speechRecognizer?.startListening(intent)
                _isListening.value = true
                onSpeechStateChange?.invoke(true)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start speech recognizer", e)
                _isListening.value = false
                isRecognitionActive = false
                onSpeechStateChange?.invoke(false)
            }
        }
    }

    fun stopListening() {
        isRecognitionActive = false
        isContinuousMode = false
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping speech recognizer", e)
            } finally {
                _isListening.value = false
                _rmsAmplitude.value = 0f
                onSpeechStateChange?.invoke(false)
            }
        }
    }

    fun cancel() {
        isRecognitionActive = false
        isContinuousMode = false
        mainHandler.post {
            try {
                speechRecognizer?.cancel()
            } catch (e: Exception) {
                Log.e(TAG, "Error cancelling speech recognizer", e)
            } finally {
                _isListening.value = false
                _rmsAmplitude.value = 0f
                onSpeechStateChange?.invoke(false)
            }
        }
    }

    fun destroy() {
        isRecognitionActive = false
        isContinuousMode = false
        mainHandler.post {
            try {
                speechRecognizer?.destroy()
                speechRecognizer = null
            } catch (e: Exception) {
                Log.e(TAG, "Error destroying speech recognizer", e)
            }
        }
    }

    private fun setupListener() {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "onReadyForSpeech")
                _isListening.value = true
                onSpeechStateChange?.invoke(true)
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech")
                _isListening.value = true
            }

            override fun onRmsChanged(rmsdB: Float) {
                val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                _rmsAmplitude.value = normalized
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech - processing voice input")
                // Keep listening indicator steady while recognizing so UI does not flicker
                _rmsAmplitude.value = 0.2f
            }

            override fun onError(error: Int) {
                Log.w(TAG, "Speech recognition error code: $error")
                _rmsAmplitude.value = 0f

                // Error 7 = ERROR_NO_MATCH, Error 6 = ERROR_SPEECH_TIMEOUT
                // If user just paused or took a second to speak, restart seamlessly without annoying disconnect
                if ((error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) && isRecognitionActive) {
                    mainHandler.postDelayed({
                        if (isRecognitionActive) {
                            try {
                                startListening(continuous = isContinuousMode)
                            } catch (e: Exception) {
                                _isListening.value = false
                                onSpeechStateChange?.invoke(false)
                            }
                        }
                    }, 400)
                    return
                }

                _isListening.value = false
                onSpeechStateChange?.invoke(false)

                if (isContinuousMode && error != SpeechRecognizer.ERROR_CLIENT && isRecognitionActive) {
                    mainHandler.postDelayed({
                        if (isRecognitionActive) {
                            startListening(continuous = true)
                        }
                    }, 800)
                }
            }

            override fun onResults(results: Bundle?) {
                _rmsAmplitude.value = 0f

                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull()?.trim()

                if (!spokenText.isNullOrBlank()) {
                    Log.i(TAG, "Speech recognized: $spokenText")
                    _isListening.value = false
                    onSpeechStateChange?.invoke(false)
                    onSpeechResult?.invoke(spokenText)
                } else if (isRecognitionActive) {
                    // Empty result, re-listen
                    mainHandler.postDelayed({
                        if (isRecognitionActive) {
                            startListening(continuous = isContinuousMode)
                        }
                    }, 300)
                    return
                }

                if (isContinuousMode && isRecognitionActive) {
                    mainHandler.postDelayed({
                        if (isRecognitionActive) {
                            startListening(continuous = true)
                        }
                    }, 500)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partial = matches?.firstOrNull()?.trim()
                if (!partial.isNullOrBlank()) {
                    onSpeechPartialResult?.invoke(partial)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }
}
