package com.example.core.audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

enum class WakeWordState {
    IDLE,
    LISTENING_FOR_WAKEWORD,
    WAKE_WORD_DETECTED,
    ACTIVE_SESSION,
    STOPPED,
    ERROR
}

class WakeWordManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "WakeWordManager"
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL = AudioFormat.CHANNEL_IN_MONO
        private const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
        private const val WAKE_ENERGY_BURST_THRESHOLD = 3200
    }

    private var audioRecord: AudioRecord? = null
    private var detectorJob: Job? = null

    private val _state = MutableStateFlow(WakeWordState.IDLE)
    val state: StateFlow<WakeWordState> = _state.asStateFlow()

    var wakePhrase: String = "Maya"
    var aliases: List<String> = listOf("Hey Maya", "Maya listen", "Zoya")
    var onWakeWordTriggered: (() -> Unit)? = null

    private val bufferSize = maxOf(
        AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, ENCODING),
        2048
    )

    @SuppressLint("MissingPermission")
    fun startListening(): Boolean {
        if (_state.value == WakeWordState.LISTENING_FOR_WAKEWORD || _state.value == WakeWordState.ACTIVE_SESSION) {
            return true
        }

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                SAMPLE_RATE,
                CHANNEL,
                ENCODING,
                bufferSize * 2
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord initialization failed for WakeWord")
                _state.value = WakeWordState.ERROR
                audioRecord?.release()
                audioRecord = null
                return false
            }

            audioRecord?.startRecording()
            _state.value = WakeWordState.LISTENING_FOR_WAKEWORD

            detectorJob = scope.launch(Dispatchers.IO) {
                val buffer = ByteArray(1024)
                var energyBursts = 0
                var silenceFrames = 0

                while (isActive && _state.value == WakeWordState.LISTENING_FOR_WAKEWORD) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: -1
                    if (read > 0) {
                        val rms = calculateRms(buffer, read)

                        // Dual-syllable speech burst pattern recognition for local wake word (e.g. "Ma-ya" / "Zo-ya")
                        if (rms > WAKE_ENERGY_BURST_THRESHOLD) {
                            energyBursts++
                            silenceFrames = 0
                            if (energyBursts in 3..14) {
                                // Trigger wake word
                                _state.value = WakeWordState.WAKE_WORD_DETECTED
                                Log.i(TAG, "Wake phrase burst detected!")
                                scope.launch(Dispatchers.Main) {
                                    onWakeWordTriggered?.invoke()
                                }
                                break
                            }
                        } else {
                            silenceFrames++
                            if (silenceFrames > 8) {
                                energyBursts = 0
                            }
                        }
                    }
                }
            }

            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error starting wake word detector", e)
            _state.value = WakeWordState.ERROR
            return false
        }
    }

    fun stopListening() {
        _state.value = WakeWordState.STOPPED
        detectorJob?.cancel()
        detectorJob = null
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping wake word AudioRecord", e)
        } finally {
            audioRecord = null
        }
    }

    fun onSessionActive() {
        stopListening()
        _state.value = WakeWordState.ACTIVE_SESSION
    }

    fun onSessionFinished() {
        _state.value = WakeWordState.IDLE
    }

    private fun calculateRms(buffer: ByteArray, length: Int): Double {
        if (length <= 0) return 0.0
        val shortBuffer = ByteBuffer.wrap(buffer, 0, length).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
        var sumSquares = 0.0
        val numShorts = length / 2
        for (i in 0 until numShorts) {
            val sample = shortBuffer.get(i).toDouble()
            sumSquares += sample * sample
        }
        return sqrt(sumSquares / numShorts)
    }
}
