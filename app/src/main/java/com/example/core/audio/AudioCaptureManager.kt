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
import kotlin.math.abs
import kotlin.math.sqrt

class AudioCaptureManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "AudioCaptureManager"
        const val SAMPLE_RATE = 16000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val VAD_ENERGY_THRESHOLD = 1800 // Amplitude threshold for voice detection
    }

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _audioAmplitude = MutableStateFlow(0f)
    val audioAmplitude: StateFlow<Float> = _audioAmplitude.asStateFlow()

    var onAudioChunk: ((ByteArray) -> Unit)? = null
    var onVoiceActivityDetected: (() -> Unit)? = null

    private val minBufferSize = maxOf(
        AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT),
        2048
    )

    @SuppressLint("MissingPermission")
    fun startCapture(): Boolean {
        if (_isRecording.value) return true

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                minBufferSize * 2
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord initialization failed")
                audioRecord?.release()
                audioRecord = null
                return false
            }

            audioRecord?.startRecording()
            _isRecording.value = true

            recordingJob = scope.launch(Dispatchers.IO) {
                val buffer = ByteArray(1024)
                var consecutiveSpeechFrames = 0

                while (isActive && _isRecording.value) {
                    val readBytes = audioRecord?.read(buffer, 0, buffer.size) ?: -1
                    if (readBytes > 0) {
                        val chunk = buffer.copyOf(readBytes)
                        val amp = calculateRms(chunk)
                        _audioAmplitude.value = (amp / 32768.0).coerceIn(0.0, 1.0).toFloat()

                        // Voice activity / interruption trigger
                        if (amp > VAD_ENERGY_THRESHOLD) {
                            consecutiveSpeechFrames++
                            if (consecutiveSpeechFrames >= 2) {
                                onVoiceActivityDetected?.invoke()
                            }
                        } else {
                            consecutiveSpeechFrames = 0
                        }

                        onAudioChunk?.invoke(chunk)
                    }
                }
            }

            return true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start audio capture", e)
            _isRecording.value = false
            return false
        }
    }

    fun stopCapture() {
        _isRecording.value = false
        recordingJob?.cancel()
        recordingJob = null
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping AudioRecord", e)
        } finally {
            audioRecord = null
            _audioAmplitude.value = 0f
        }
    }

    private fun calculateRms(buffer: ByteArray): Double {
        if (buffer.isEmpty()) return 0.0
        val shortBuffer = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
        var sumSquares = 0.0
        val numShorts = buffer.size / 2
        for (i in 0 until numShorts) {
            val sample = shortBuffer.get(i).toDouble()
            sumSquares += sample * sample
        }
        return sqrt(sumSquares / numShorts)
    }
}
