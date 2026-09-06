package com.example.core.tools

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.AudioManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.core.gemini.LiveFunctionDeclaration
import com.example.core.gemini.LiveToolGroup
import com.example.core.security.PhoneUnlockManager
import com.example.data.local.PreferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume

class ToolExecutionEngine(private val context: Context) {

    companion object {
        private const val TAG = "ToolExecutionEngine"
    }

    fun getToolDeclarations(): List<LiveToolGroup> {
        val declarations = listOf(
            LiveFunctionDeclaration(
                name = "openApp",
                description = "Open an installed Android app given its package name or common app name (e.g., com.google.android.youtube, com.whatsapp, com.spotify.music)",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "packageName" to mapOf(
                            "type" to "STRING",
                            "description" to "The package name or common name of the application to launch"
                        )
                    ),
                    "required" to listOf("packageName")
                )
            ),
            LiveFunctionDeclaration(
                name = "searchAndCallContact",
                description = "Search Android contacts for a given name and initiate a phone call",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "contactName" to mapOf(
                            "type" to "STRING",
                            "description" to "Name of the person to call"
                        )
                    ),
                    "required" to listOf("contactName")
                )
            ),
            LiveFunctionDeclaration(
                name = "sendWhatsAppMessage",
                description = "Compose and open a WhatsApp chat with a contact and pre-filled message",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "contactName" to mapOf(
                            "type" to "STRING",
                            "description" to "Name or phone number of the recipient"
                        ),
                        "message" to mapOf(
                            "type" to "STRING",
                            "description" to "The message content to send"
                        )
                    ),
                    "required" to listOf("contactName", "message")
                )
            ),
            LiveFunctionDeclaration(
                name = "sendGmail",
                description = "Open Gmail compose screen with pre-filled recipient email, subject, and body",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "recipientEmail" to mapOf(
                            "type" to "STRING",
                            "description" to "Email address of the recipient"
                        ),
                        "subject" to mapOf(
                            "type" to "STRING",
                            "description" to "Subject line of the email"
                        ),
                        "body" to mapOf(
                            "type" to "STRING",
                            "description" to "Body content of the email"
                        )
                    ),
                    "required" to listOf("recipientEmail", "subject", "body")
                )
            ),
            LiveFunctionDeclaration(
                name = "getCurrentTime",
                description = "Get the current time, date, day of week, and timezone",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to emptyMap<String, Any>()
                )
            ),
            LiveFunctionDeclaration(
                name = "getBatteryStatus",
                description = "Get device battery level percentage and charging status",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to emptyMap<String, Any>()
                )
            ),
            LiveFunctionDeclaration(
                name = "getDeviceInfo",
                description = "Get device manufacturer, model, and Android OS version",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to emptyMap<String, Any>()
                )
            ),
            LiveFunctionDeclaration(
                name = "openSettings",
                description = "Open device settings (e.g. bluetooth, wifi, sound, display, battery, general)",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "settingType" to mapOf(
                            "type" to "STRING",
                            "description" to "Type of settings to open: wifi, bluetooth, sound, display, battery, all"
                        )
                    ),
                    "required" to listOf("settingType")
                )
            ),
            LiveFunctionDeclaration(
                name = "openUrl",
                description = "Open a webpage or URL in the default browser",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "url" to mapOf(
                            "type" to "STRING",
                            "description" to "The full URL to open (e.g. https://google.com)"
                        )
                    ),
                    "required" to listOf("url")
                )
            ),
            LiveFunctionDeclaration(
                name = "searchContacts",
                description = "Search contacts list for names and phone numbers",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "query" to mapOf(
                            "type" to "STRING",
                            "description" to "Search query name"
                        )
                    ),
                    "required" to listOf("query")
                )
            ),
            LiveFunctionDeclaration(
                name = "createCalendarEvent",
                description = "Create a calendar event with title, date/time, and description",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "title" to mapOf("type" to "STRING", "description" to "Event title"),
                        "date" to mapOf("type" to "STRING", "description" to "Date/time string"),
                        "location" to mapOf("type" to "STRING", "description" to "Event location (optional)")
                    ),
                    "required" to listOf("title")
                )
            ),
            LiveFunctionDeclaration(
                name = "playMusic",
                description = "Play music or search a song on YouTube / default media app",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "query" to mapOf("type" to "STRING", "description" to "Song title or artist to play")
                    ),
                    "required" to listOf("query")
                )
            ),
            LiveFunctionDeclaration(
                name = "sendSMS",
                description = "Send an SMS text message to a contact name or phone number",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "contactNameOrNumber" to mapOf(
                            "type" to "STRING",
                            "description" to "Name of contact or phone number"
                        ),
                        "message" to mapOf(
                            "type" to "STRING",
                            "description" to "The SMS message text to send"
                        )
                    ),
                    "required" to listOf("contactNameOrNumber", "message")
                )
            ),
            LiveFunctionDeclaration(
                name = "getScreenLockStatus",
                description = "Check if phone screen is locked or unlocked, keyguard state",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to emptyMap<String, Any>()
                )
            ),
            LiveFunctionDeclaration(
                name = "unlockPhone",
                description = "Unlock the phone screen using saved pattern or PIN password when user asks Maya to open or unlock the phone",
                parameters = mapOf(
                    "type" to "OBJECT",
                    "properties" to emptyMap<String, Any>()
                )
            )
        )
        return listOf(LiveToolGroup(functionDeclarations = declarations))
    }

    suspend fun executeTool(name: String, args: Map<String, Any?>): Map<String, Any?> = withContext(Dispatchers.IO) {
        try {
            when (name) {
                "openApp" -> executeOpenApp(args["packageName"] as? String ?: "")
                "searchAndCallContact" -> executeSearchAndCall(args["contactName"] as? String ?: "")
                "sendWhatsAppMessage" -> executeSendWhatsApp(
                    args["contactName"] as? String ?: "",
                    args["message"] as? String ?: ""
                )
                "sendSMS" -> executeSendSMS(
                    args["contactNameOrNumber"] as? String ?: "",
                    args["message"] as? String ?: ""
                )
                "getScreenLockStatus" -> executeGetScreenLockStatus()
                "unlockPhone" -> executeUnlockPhone()
                "sendGmail" -> executeSendGmail(
                    args["recipientEmail"] as? String ?: "",
                    args["subject"] as? String ?: "",
                    args["body"] as? String ?: ""
                )
                "getCurrentTime" -> executeGetCurrentTime()
                "getBatteryStatus" -> executeGetBatteryStatus()
                "getDeviceInfo" -> executeGetDeviceInfo()
                "openSettings" -> executeOpenSettings(args["settingType"] as? String ?: "all")
                "openUrl" -> executeOpenUrl(args["url"] as? String ?: "")
                "searchContacts" -> executeSearchContacts(args["query"] as? String ?: "")
                "createCalendarEvent" -> executeCreateCalendarEvent(
                    args["title"] as? String ?: "",
                    args["date"] as? String ?: "",
                    args["location"] as? String ?: ""
                )
                "playMusic" -> executePlayMusic(args["query"] as? String ?: "")
                else -> mapOf("status" to "error", "message" to "Unknown tool: $name")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Tool execution failed: $name", e)
            mapOf("status" to "error", "message" to (e.message ?: "Execution failed"))
        }
    }

    private fun executeOpenApp(packageNameOrName: String): Map<String, Any?> {
        val pm = context.packageManager
        val resolvedPackage = resolvePackageName(packageNameOrName)

        val intent = pm.getLaunchIntentForPackage(resolvedPackage)
        return if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            mapOf("status" to "success", "message" to "Launched $resolvedPackage")
        } else {
            // Try searching intent
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$resolvedPackage")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            mapOf(
                "status" to "not_installed",
                "message" to "Application $resolvedPackage is not installed on this device."
            )
        }
    }

    private fun resolvePackageName(name: String): String {
        val lower = name.lowercase().trim()
        return when {
            lower.contains("youtube") -> "com.google.android.youtube"
            lower.contains("whatsapp") -> "com.whatsapp"
            lower.contains("spotify") -> "com.spotify.music"
            lower.contains("chrome") -> "com.android.chrome"
            lower.contains("maps") || lower.contains("google maps") -> "com.google.android.apps.maps"
            lower.contains("gmail") -> "com.google.android.gm"
            lower.contains("camera") -> "com.google.android.GoogleCamera"
            lower.contains("calculator") -> "com.google.android.calculator"
            lower.contains("calendar") -> "com.google.android.calendar"
            lower.contains("photos") -> "com.google.android.apps.photos"
            lower.contains("settings") -> "com.android.settings"
            else -> name
        }
    }

    private fun executeSearchAndCall(contactName: String): Map<String, Any?> {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return mapOf(
                "status" to "permission_denied",
                "message" to "READ_CONTACTS permission is required to search contacts. Please grant it in Permissions settings."
            )
        }

        val contacts = queryContacts(contactName)
        if (contacts.isEmpty()) {
            return mapOf(
                "status" to "not_found",
                "message" to "No contact found with name '$contactName'."
            )
        }

        if (contacts.size > 1) {
            val names = contacts.map { "${it.name} (${it.number})" }.joinToString(", ")
            return mapOf(
                "status" to "ambiguous",
                "message" to "Found multiple contacts matching '$contactName': $names. Which one would you like to call?"
            )
        }

        val target = contacts.first()
        val hasCallPhone = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
        val intent = if (hasCallPhone) {
            Intent(Intent.ACTION_CALL, Uri.parse("tel:${target.number}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:${target.number}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        context.startActivity(intent)
        return mapOf(
            "status" to "success",
            "message" to "Calling ${target.name} at ${target.number}"
        )
    }

    private fun executeSendWhatsApp(contactName: String, message: String): Map<String, Any?> {
        var phoneNumber = contactName.filter { it.isDigit() || it == '+' }
        if (phoneNumber.length < 6) {
            // Try resolving name
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                val contacts = queryContacts(contactName)
                if (contacts.isNotEmpty()) {
                    phoneNumber = contacts.first().number.filter { it.isDigit() || it == '+' }
                }
            }
        }

        val url = if (phoneNumber.isNotBlank()) {
            "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
        } else {
            "https://api.whatsapp.com/send?text=${Uri.encode(message)}"
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            `package` = "com.whatsapp"
        }

        return try {
            context.startActivity(intent)
            mapOf(
                "status" to "success",
                "message" to "Opened WhatsApp chat with message pre-filled: '$message'"
            )
        } catch (e: Exception) {
            // WhatsApp not installed or fallback browser
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(fallbackIntent)
                mapOf("status" to "success", "message" to "Opened WhatsApp web portal with message pre-filled.")
            } catch (ex: Exception) {
                mapOf("status" to "not_installed", "message" to "WhatsApp is not installed.")
            }
        }
    }

    private fun executeSendGmail(recipientEmail: String, subject: String, body: String): Map<String, Any?> {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            mapOf(
                "status" to "success",
                "message" to "Opened email composer for $recipientEmail with subject '$subject'."
            )
        } catch (e: Exception) {
            mapOf("status" to "error", "message" to "No email client app available.")
        }
    }

    private fun executeGetCurrentTime(): Map<String, Any?> {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        val now = Date()
        return mapOf(
            "status" to "success",
            "time" to timeFormat.format(now),
            "date" to dateFormat.format(now),
            "timestamp" to System.currentTimeMillis()
        )
    }

    private fun executeGetBatteryStatus(): Map<String, Any?> {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        val level = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 100
        val isCharging = bm?.isCharging ?: false
        return mapOf(
            "status" to "success",
            "batteryLevel" to "$level%",
            "isCharging" to isCharging
        )
    }

    private fun executeGetDeviceInfo(): Map<String, Any?> {
        return mapOf(
            "status" to "success",
            "manufacturer" to Build.MANUFACTURER,
            "model" to Build.MODEL,
            "androidVersion" to Build.VERSION.RELEASE,
            "sdkInt" to Build.VERSION.SDK_INT
        )
    }

    private fun executeOpenSettings(type: String): Map<String, Any?> {
        val action = when (type.lowercase()) {
            "wifi" -> Settings.ACTION_WIFI_SETTINGS
            "bluetooth" -> Settings.ACTION_BLUETOOTH_SETTINGS
            "sound" -> Settings.ACTION_SOUND_SETTINGS
            "display" -> Settings.ACTION_DISPLAY_SETTINGS
            "battery" -> Settings.ACTION_BATTERY_SAVER_SETTINGS
            "apps" -> Settings.ACTION_APPLICATION_SETTINGS
            else -> Settings.ACTION_SETTINGS
        }

        val intent = Intent(action).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        context.startActivity(intent)
        return mapOf("status" to "success", "message" to "Opened $type settings.")
    }

    private fun executeOpenUrl(url: String): Map<String, Any?> {
        var cleanUrl = url
        if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
            cleanUrl = "https://$cleanUrl"
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cleanUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return mapOf("status" to "success", "message" to "Opened $cleanUrl")
    }

    private fun executeSearchContacts(query: String): Map<String, Any?> {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return mapOf("status" to "permission_denied", "message" to "READ_CONTACTS permission required.")
        }
        val list = queryContacts(query)
        return mapOf(
            "status" to "success",
            "contacts" to list.map { mapOf("name" to it.name, "number" to it.number) }
        )
    }

    private fun executeCreateCalendarEvent(title: String, date: String, location: String): Map<String, Any?> {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.Events.EVENT_LOCATION, location)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return mapOf("status" to "success", "message" to "Opened Calendar event creation for '$title'.")
    }

    private fun executePlayMusic(query: String): Map<String, Any?> {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return mapOf("status" to "success", "message" to "Playing '$query' on YouTube / media app.")
    }

    private fun executeSendSMS(contactNameOrNumber: String, message: String): Map<String, Any?> {
        var phoneNumber = contactNameOrNumber.filter { it.isDigit() || it == '+' }
        var recipientName = contactNameOrNumber
        if (phoneNumber.length < 6) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                val contacts = queryContacts(contactNameOrNumber)
                if (contacts.isNotEmpty()) {
                    phoneNumber = contacts.first().number.filter { it.isDigit() || it == '+' }
                    recipientName = contacts.first().name
                }
            }
        }

        val sendIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = if (phoneNumber.isNotBlank()) Uri.parse("smsto:$phoneNumber") else Uri.parse("smsto:")
            putExtra("sms_body", message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(sendIntent)
            mapOf(
                "status" to "success",
                "message" to "Opened SMS messenger to send text to $recipientName: '$message'"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch SMS intent", e)
            mapOf("status" to "error", "message" to "Unable to launch SMS app: ${e.message}")
        }
    }

    private fun executeGetScreenLockStatus(): Map<String, Any?> {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? android.app.KeyguardManager
        val isDeviceLocked = keyguardManager?.isDeviceLocked ?: false
        val isKeyguardLocked = keyguardManager?.isKeyguardLocked ?: false
        val isSecure = keyguardManager?.isDeviceSecure ?: false

        return mapOf(
            "status" to "success",
            "isLocked" to (isDeviceLocked || isKeyguardLocked),
            "isSecure" to isSecure,
            "message" to if (isDeviceLocked || isKeyguardLocked) "Phone is currently locked" else "Phone is currently unlocked"
        )
    }

    private suspend fun executeUnlockPhone(): Map<String, Any?> {
        val prefs = PreferencesDataStore(context)
        val settings = prefs.settingsFlow.first()

        if (settings.phonePattern.isBlank() && settings.phonePin.isBlank()) {
            return mapOf(
                "status" to "error",
                "message" to "Aapne abhi tak Pattern ya PIN set nahi kiya hai. Settings me jakar save kar lijiye.",
                "unlocked" to false
            )
        }

        return suspendCancellableCoroutine { continuation ->
            PhoneUnlockManager.triggerUnlock(
                context = context,
                pattern = settings.phonePattern,
                pin = settings.phonePin,
                byVoice = true
            ) { success, msg ->
                if (continuation.isActive) {
                    continuation.resume(
                        mapOf(
                            "status" to if (success) "success" else "error",
                            "message" to msg,
                            "unlocked" to success
                        )
                    )
                }
            }
        }
    }

    private data class ContactInfo(val name: String, val number: String)

    private fun queryContacts(query: String): List<ContactInfo> {
        val results = mutableListOf<ContactInfo>()
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")

        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            cursor?.let {
                val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (it.moveToNext()) {
                    val name = if (nameIdx >= 0) it.getString(nameIdx) ?: "" else ""
                    val number = if (numIdx >= 0) it.getString(numIdx) ?: "" else ""
                    if (name.isNotBlank() && number.isNotBlank()) {
                        results.add(ContactInfo(name, number))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error querying contacts", e)
        } finally {
            cursor?.close()
        }
        return results
    }
}
