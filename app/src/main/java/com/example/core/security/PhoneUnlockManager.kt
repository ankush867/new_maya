package com.example.core.security

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import com.example.service.MayaAccessibilityService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object PhoneUnlockManager {

    private val _unlockEvents = MutableSharedFlow<UnlockTriggerEvent>(extraBufferCapacity = 10)
    val unlockEvents = _unlockEvents.asSharedFlow()

    data class UnlockTriggerEvent(
        val pattern: String,
        val pin: String,
        val triggeredByVoice: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    )

    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun isAccessibilityEnabled(context: Context): Boolean {
        return MayaAccessibilityService.isAccessibilityServiceEnabled(context) ||
                MayaAccessibilityService.isServiceConnected
    }

    fun triggerUnlock(
        context: Context,
        pattern: String,
        pin: String,
        byVoice: Boolean = true,
        onResult: (Boolean, String) -> Unit
    ) {
        if (pattern.isBlank() && pin.isBlank()) {
            onResult(false, "No pattern or PIN saved. Please set one up in Pattern & PIN settings.")
            return
        }

        // Wake screen if needed
        wakeScreen(context)

        // Dismiss Keyguard if possible
        dismissKeyguard(context)

        // Notify in-app lock listeners
        _unlockEvents.tryEmit(
            UnlockTriggerEvent(
                pattern = pattern,
                pin = pin,
                triggeredByVoice = byVoice
            )
        )

        val service = MayaAccessibilityService.instance
        if (service != null) {
            val metrics = context.resources.displayMetrics
            service.performDeviceUnlock(
                pattern = pattern,
                pin = pin,
                screenWidthPx = metrics.widthPixels,
                screenHeightPx = metrics.heightPixels
            ) { success, msg ->
                onResult(success, if (success) "Phone unlock ho gaya! Aapka saved password enter ho chuka hai." else msg)
            }
        } else {
            // Real instruction so user can activate accessibility service
            onResult(
                false,
                "Maya Accessibility Service band hai. Kripya Settings > Accessibility me 'Maya' ko ON karein taki Maya screen unlock gestures chala sake!"
            )
        }
    }

    private fun wakeScreen(context: Context) {
        try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            if (powerManager != null && !powerManager.isInteractive) {
                @Suppress("DEPRECATION")
                val wakeLock = powerManager.newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "maya:unlock_wake"
                )
                wakeLock.acquire(3000)
                wakeLock.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dismissKeyguard(context: Context) {
        try {
            val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && context is Activity) {
                keyguardManager?.requestDismissKeyguard(context, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
