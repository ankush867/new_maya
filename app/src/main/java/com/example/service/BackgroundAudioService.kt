package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.core.model.AssistantState
import com.example.data.repository.AssistantRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BackgroundAudioService : Service() {

    companion object {
        private const val TAG = "BackgroundAudioService"
        const val CHANNEL_ID = "maya_assistant_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.example.maya.ACTION_START"
        const val ACTION_STOP = "com.example.maya.ACTION_STOP"
        const val ACTION_TOGGLE = "com.example.maya.ACTION_TOGGLE"

        fun startService(context: Context) {
            try {
                val intent = Intent(context, BackgroundAudioService::class.java).apply {
                    action = ACTION_START
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start service", e)
            }
        }

        fun stopService(context: Context) {
            try {
                val intent = Intent(context, BackgroundAudioService::class.java).apply {
                    action = ACTION_STOP
                }
                context.startService(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop service", e)
            }
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var repository: AssistantRepository
    private var stateJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        repository = AssistantRepository.getInstance(applicationContext)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                repository.stopAssistantVoiceSession()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_TOGGLE -> {
                repository.toggleVoiceAssistant()
            }
            else -> {
                startForegroundWithNotification(repository.assistantState.value)
                observeAssistantState()
            }
        }
        return START_STICKY
    }

    private fun observeAssistantState() {
        stateJob?.cancel()
        stateJob = serviceScope.launch {
            repository.assistantState.collectLatest { state ->
                updateNotification(state)
            }
        }
    }

    private fun startForegroundWithNotification(state: AssistantState) {
        val notification = buildNotification(state)
        val hasMicPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val fgsType = if (hasMicPermission) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                    } else {
                        0
                    }
                }
                startForeground(NOTIFICATION_ID, notification, fgsType)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in startForeground with type, falling back", e)
            try {
                startForeground(NOTIFICATION_ID, notification)
            } catch (e2: Exception) {
                Log.e(TAG, "Fatal error starting foreground notification", e2)
            }
        }
    }

    private fun updateNotification(state: AssistantState) {
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, buildNotification(state))
        } catch (e: Exception) {
            Log.e(TAG, "Error updating notification", e)
        }
    }

    private fun buildNotification(state: AssistantState): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, BackgroundAudioService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stateText = when (state) {
            AssistantState.LISTENING -> "Listening to your voice..."
            AssistantState.SPEAKING -> "Speaking..."
            AssistantState.THINKING -> "Thinking..."
            AssistantState.WAKE_LISTENING -> "Waiting for 'Maya'..."
            else -> "Maya is ready"
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Maya AI Assistant")
            .setContentText(stateText)
            .setSmallIcon(R.drawable.maya_app_icon)
            .setContentIntent(openPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Stop Assistant",
                stopPendingIntent
            )
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Maya Assistant Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background voice listening and Gemini Live connection"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stateJob?.cancel()
    }
}
