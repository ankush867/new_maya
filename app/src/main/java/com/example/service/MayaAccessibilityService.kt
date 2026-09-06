package com.example.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class MayaAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MayaAccessibilityService"
        var instance: MayaAccessibilityService? = null
            private set

        val isServiceConnected: Boolean
            get() = instance != null

        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            val expectedServiceName = "${context.packageName}/${MayaAccessibilityService::class.java.canonicalName}"
            val enabledServicesSetting = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServicesSetting)

            while (colonSplitter.hasNext()) {
                val componentName = colonSplitter.next()
                if (componentName.equals(expectedServiceName, ignoreCase = true)) {
                    return true
                }
            }
            return false
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i(TAG, "MayaAccessibilityService connected successfully")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Listening for system windows or lock screen transitions
    }

    override fun onInterrupt() {
        Log.w(TAG, "MayaAccessibilityService interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
        }
    }

    /**
     * Attempts to unlock the device screen using saved Pattern or PIN.
     * Dispatches gesture for pattern or performs accessibility unlock actions.
     */
    fun performDeviceUnlock(
        pattern: String,
        pin: String,
        screenWidthPx: Int = 1080,
        screenHeightPx: Int = 2400,
        onResult: (Boolean, String) -> Unit
    ) {
        val handler = Handler(Looper.getMainLooper())

        // 1. First swipe up from bottom to reveal keypad or pattern lock
        val swipePath = Path().apply {
            moveTo(screenWidthPx / 2f, screenHeightPx * 0.85f)
            lineTo(screenWidthPx / 2f, screenHeightPx * 0.35f)
        }

        val swipeGesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(swipePath, 0, 300))
            .build()

        dispatchGesture(swipeGesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)

                // 2. Wait slightly for lock screen pattern / pin pad to appear
                handler.postDelayed({
                    if (pattern.isNotBlank()) {
                        drawPatternGesture(pattern, screenWidthPx, screenHeightPx) { success ->
                            if (success) {
                                onResult(true, "Pattern drawn successfully on lock screen")
                            } else {
                                onResult(false, "Failed to draw pattern")
                            }
                        }
                    } else if (pin.isNotBlank()) {
                        enterPinGesture(pin, screenWidthPx, screenHeightPx) { success ->
                            if (success) {
                                onResult(true, "PIN keypad entered successfully on lock screen")
                            } else {
                                onResult(false, "Failed to enter PIN")
                            }
                        }
                    } else {
                        onResult(false, "Neither Pattern nor PIN configured")
                    }
                }, 400)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                onResult(false, "Swipe up cancelled")
            }
        }, null)
    }

    private fun enterPinGesture(
        pinStr: String,
        screenWidthPx: Int,
        screenHeightPx: Int,
        onComplete: (Boolean) -> Unit
    ) {
        val cleanPin = pinStr.filter { it.isDigit() }
        if (cleanPin.isEmpty()) {
            onComplete(false)
            return
        }

        val gridCenterY = screenHeightPx * 0.70f
        val keypadWidth = screenWidthPx * 0.80f
        val colSpacing = keypadWidth / 3f
        val startX = (screenWidthPx - keypadWidth) / 2f + colSpacing / 2f
        val rowSpacing = screenHeightPx * 0.085f
        val startY = gridCenterY - (rowSpacing * 1.5f)

        fun getDigitCoords(digit: Char): Pair<Float, Float>? {
            return when (digit) {
                '1' -> Pair(startX, startY)
                '2' -> Pair(startX + colSpacing, startY)
                '3' -> Pair(startX + 2 * colSpacing, startY)
                '4' -> Pair(startX, startY + rowSpacing)
                '5' -> Pair(startX + colSpacing, startY + rowSpacing)
                '6' -> Pair(startX + 2 * colSpacing, startY + rowSpacing)
                '7' -> Pair(startX, startY + 2 * rowSpacing)
                '8' -> Pair(startX + colSpacing, startY + 2 * rowSpacing)
                '9' -> Pair(startX + 2 * colSpacing, startY + 2 * rowSpacing)
                '0' -> Pair(startX + colSpacing, startY + 3 * rowSpacing)
                else -> null
            }
        }

        val handler = Handler(Looper.getMainLooper())
        var currentIdx = 0

        fun tapNextDigit() {
            if (currentIdx >= cleanPin.length) {
                // Also tap enter / confirm button (bottom-right of keypad) after short delay
                handler.postDelayed({
                    val enterX = startX + 2 * colSpacing
                    val enterY = startY + 3 * rowSpacing
                    val enterPath = Path().apply {
                        moveTo(enterX, enterY)
                        lineTo(enterX, enterY)
                    }
                    val enterGesture = GestureDescription.Builder()
                        .addStroke(GestureDescription.StrokeDescription(enterPath, 0, 80))
                        .build()

                    dispatchGesture(enterGesture, object : GestureResultCallback() {
                        override fun onCompleted(gestureDescription: GestureDescription?) {
                            onComplete(true)
                        }
                        override fun onCancelled(gestureDescription: GestureDescription?) {
                            onComplete(true) // PIN already tapped
                        }
                    }, null)
                }, 180)
                return
            }

            val digit = cleanPin[currentIdx]
            val coords = getDigitCoords(digit)
            if (coords != null) {
                val tapPath = Path().apply {
                    moveTo(coords.first, coords.second)
                    lineTo(coords.first, coords.second)
                }
                val tapGesture = GestureDescription.Builder()
                    .addStroke(GestureDescription.StrokeDescription(tapPath, 0, 70))
                    .build()

                dispatchGesture(tapGesture, object : GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription?) {
                        currentIdx++
                        handler.postDelayed({ tapNextDigit() }, 140)
                    }
                    override fun onCancelled(gestureDescription: GestureDescription?) {
                        currentIdx++
                        handler.postDelayed({ tapNextDigit() }, 140)
                    }
                }, null)
            } else {
                currentIdx++
                tapNextDigit()
            }
        }

        tapNextDigit()
    }

    private fun drawPatternGesture(
        patternStr: String,
        screenWidthPx: Int,
        screenHeightPx: Int,
        onComplete: (Boolean) -> Unit
    ) {
        val dotIndices = patternStr.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it in 0..8 }

        if (dotIndices.size < 4) {
            onComplete(false)
            return
        }

        // Standard 3x3 pattern position estimates on phone lockscreen
        val gridCenterY = screenHeightPx * 0.65f
        val gridWidth = screenWidthPx * 0.72f
        val startX = (screenWidthPx - gridWidth) / 2f
        val dotSpacing = gridWidth / 2f
        val startY = gridCenterY - dotSpacing

        fun getDotCoords(index: Int): Pair<Float, Float> {
            val col = index % 3
            val row = index / 3
            return Pair(startX + col * dotSpacing, startY + row * dotSpacing)
        }

        val patternPath = Path()
        val first = getDotCoords(dotIndices[0])
        patternPath.moveTo(first.first, first.second)

        for (i in 1 until dotIndices.size) {
            val pt = getDotCoords(dotIndices[i])
            patternPath.lineTo(pt.first, pt.second)
        }

        val patternGesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(patternPath, 0, 500))
            .build()

        dispatchGesture(patternGesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                onComplete(true)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                onComplete(false)
            }
        }, null)
    }
}
