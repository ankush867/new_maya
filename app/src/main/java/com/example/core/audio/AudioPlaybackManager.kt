package com.example.core.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
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
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.sqrt

class AudioPlaybackManager(
    private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "AudioPlaybackManager"
        const val OUTPUT_SAMPLE_RATE = 24000 // Gemini Live default output sample rate
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val audioQueue = ConcurrentLinkedQueue<ByteArray>()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackAmplitude = MutableStateFlow(0f)
    val playbackAmplitude: StateFlow<Float> = _playbackAmplitude.asStateFlow()

    private val minBufferSize = maxOf(
        AudioTrack.getMinBufferSize(OUTPUT_SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT),
        4096
    )

    init {
        initAudioTrack()
    }

    private fun initAudioTrack() {
        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANT)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AUDIO_FORMAT)
                        .setSampleRate(OUTPUT_SAMPLE_RATE)
                        .setChannelMask(CHANNEL_CONFIG)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
            startPlaybackLoop()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize AudioTrack", e)
        }
    }

    private fun startPlaybackLoop() {
        playbackJob?.cancel()
        playbackJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                val chunk = audioQueue.poll()
                if (chunk != null && chunk.isNotEmpty()) {
                    _isPlaying.value = true
                    val amp = calculateRms(chunk)
                    _playbackAmplitude.value = (amp / 32768.0).coerceIn(0.0, 1.0).toFloat()

                    audioTrack?.write(chunk, 0, chunk.size)
                } else {
                    if (_isPlaying.value && audioQueue.isEmpty()) {
                        _isPlaying.value = false
                        _playbackAmplitude.value = 0f
                    }
                    kotlinx.coroutines.delay(10)
                }
            }
        }
    }

    fun playChunk(pcmData: ByteArray) {
        if (pcmData.isNotEmpty()) {
            audioQueue.offer(pcmData)
        }
    }

    /**
     * Interrupt / Barge-in: immediately clears queue, pauses, flushes AudioTrack buffer
     */
    fun interrupt() {
        audioQueue.clear()
        _isPlaying.value = false
        _playbackAmplitude.value = 0f
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.play()
        } catch (e: Exception) {
            Log.e(TAG, "Error interrupting AudioTrack", e)
        }
    }

    fun release() {
        audioQueue.clear()
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing AudioTrack", e)
        } finally {
            audioTrack = null
            _isPlaying.value = false
            _playbackAmplitude.value = 0f
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
