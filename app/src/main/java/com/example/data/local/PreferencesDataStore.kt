package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "maya_preferences")

class PreferencesDataStore(private val context: Context) {

    companion object {
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_ASSISTANT_NAME = stringPreferencesKey("assistant_name")
        val KEY_LANGUAGE = stringPreferencesKey("preferred_language")
        val KEY_VOICE = stringPreferencesKey("assistant_voice")
        val KEY_ASSISTANT_PERSONA = stringPreferencesKey("assistant_persona")
        val KEY_PERSONALITY = stringPreferencesKey("personality_style")
        val KEY_SPEED = floatPreferencesKey("speaking_speed")
        val KEY_RESPONSE_LENGTH = stringPreferencesKey("response_length")
        val KEY_WAKE_PHRASE = stringPreferencesKey("wake_phrase")
        val KEY_WAKE_WORD_ENABLED = booleanPreferencesKey("wake_word_enabled")
        val KEY_BG_SERVICE_ENABLED = booleanPreferencesKey("bg_service_enabled")
        val KEY_START_ON_BOOT = booleanPreferencesKey("start_on_boot")
        val KEY_FLOATING_ASSISTANT = booleanPreferencesKey("floating_assistant")
        val KEY_ECHO_GUARD = booleanPreferencesKey("echo_guard")
        val KEY_VOICE_GUARDIAN = booleanPreferencesKey("voice_guardian")
        val KEY_TRUSTED_VOICE_TRAINED = booleanPreferencesKey("trusted_voice_trained")
        val KEY_VOICE_PRINT = stringPreferencesKey("voice_print_signature")
        val KEY_BLOCK_UNKNOWN_VOICES = booleanPreferencesKey("block_unknown_voices")
        val KEY_EMERGENCY_NAME = stringPreferencesKey("emergency_contact_name")
        val KEY_EMERGENCY_PHONE = stringPreferencesKey("emergency_contact_phone")
        val KEY_EMERGENCY_SOS_ENABLED = booleanPreferencesKey("emergency_sos_enabled")
        val KEY_TOUCH_GUARD = booleanPreferencesKey("touch_guard")
        val KEY_SCREEN_LOCK_ANNOUNCE = booleanPreferencesKey("screen_lock_announce")
        val KEY_WHATSAPP_AUTOREPLY = booleanPreferencesKey("whatsapp_autoreply")
        val KEY_WHATSAPP_ONLY_ASLEEP = booleanPreferencesKey("whatsapp_only_asleep")
        val KEY_WHATSAPP_INCLUDE_GROUPS = booleanPreferencesKey("whatsapp_include_groups")
        val KEY_WHATSAPP_INSTRUCTIONS = stringPreferencesKey("whatsapp_instructions")
        val KEY_WHATSAPP_FALLBACK = stringPreferencesKey("whatsapp_fallback")
        val KEY_WHATSAPP_NOTE = stringPreferencesKey("whatsapp_note")
        val KEY_WHATSAPP_ONLY_CHATS = stringPreferencesKey("whatsapp_only_chats")
        val KEY_WHATSAPP_NEVER_CHATS = stringPreferencesKey("whatsapp_never_chats")
        val KEY_ANNOUNCE_CHARGING = booleanPreferencesKey("announce_charging")
        val KEY_ANNOUNCE_BATTERY_LOW = booleanPreferencesKey("announce_battery_low")
        val KEY_FREE_MINUTES = intPreferencesKey("free_minutes_left")
        val KEY_FREE_SECONDS = intPreferencesKey("free_seconds_left")
        val KEY_IS_PRO_USER = booleanPreferencesKey("is_pro_user")
        val KEY_CUSTOM_API_KEY = stringPreferencesKey("custom_api_key")
        // Lock System & Phone Unlock
        val KEY_PHONE_PATTERN = stringPreferencesKey("phone_pattern")
        val KEY_PHONE_PIN = stringPreferencesKey("phone_pin")
        val KEY_PHONE_UNLOCK_ENABLED = booleanPreferencesKey("phone_unlock_enabled")
        val KEY_APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        // Appearance & Orb
        val KEY_ORB_STYLE = stringPreferencesKey("orb_style")
        val KEY_ORB_COLOR = stringPreferencesKey("orb_color_name")
        val KEY_ORB_SIZE = intPreferencesKey("orb_size_dp")
        val KEY_USE_ORB_HOME = booleanPreferencesKey("use_orb_on_home")
        // Theme
        val KEY_THEME_TYPEFACE = stringPreferencesKey("theme_typeface")
        val KEY_THEME_FONT_SIZE = stringPreferencesKey("theme_font_size")
        val KEY_THEME_SURFACE = stringPreferencesKey("theme_surface_style")
        val KEY_THEME_CORNER = stringPreferencesKey("theme_corner_style")
        // Personal
        val KEY_USER_GENDER = stringPreferencesKey("user_gender")
        val KEY_USER_PHONE = stringPreferencesKey("user_phone")
        val KEY_MUSIC_APP = stringPreferencesKey("music_app")
        val KEY_FAVORITE_SONG = stringPreferencesKey("favorite_song")
        // Social
        val KEY_SOCIAL_HANDLE = stringPreferencesKey("social_handle")
        val KEY_SOCIAL_PLATFORM = stringPreferencesKey("social_platform")
        val KEY_SOCIAL_VOICE = stringPreferencesKey("social_voice")
        val KEY_SOCIAL_DAILY_STORY = booleanPreferencesKey("social_daily_story")
        val KEY_SOCIAL_AUTO_POST = booleanPreferencesKey("social_auto_post")
        // Sub-agents
        val KEY_SUBAGENT_MODELS = stringPreferencesKey("subagent_models")
        val KEY_SUBAGENT_BRAIN_DEFAULT = booleanPreferencesKey("subagent_brain_default")
        // Assistant
        val KEY_CONVERSATION_MODE = booleanPreferencesKey("conversation_mode")
        val KEY_MESSAGE_ALERTS = booleanPreferencesKey("message_alerts")
        val KEY_AUTO_START_WAKE = booleanPreferencesKey("auto_start_wake")
        val KEY_PROACTIVE_MAYA = booleanPreferencesKey("proactive_maya")
        val KEY_CALL_ANNOUNCE = booleanPreferencesKey("call_announce")
        val KEY_KEEP_RINGTONE = booleanPreferencesKey("keep_ringtone")
        val KEY_DRIVING_MODE = booleanPreferencesKey("driving_mode")
        val KEY_DRIVING_REPLY = stringPreferencesKey("driving_reply")
        val KEY_POLLINATIONS_TOKEN = stringPreferencesKey("pollinations_token")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            userName = prefs[KEY_USER_NAME] ?: "Ankush",
            assistantName = prefs[KEY_ASSISTANT_NAME] ?: "Maya",
            preferredLanguage = prefs[KEY_LANGUAGE] ?: "Auto (English / Hindi / Hinglish)",
            assistantVoice = prefs[KEY_VOICE] ?: "Aoede",
            assistantPersona = prefs[KEY_ASSISTANT_PERSONA] ?: "Maya",
            personalityStyle = prefs[KEY_PERSONALITY] ?: "AI Girlfriend Mode",
            speakingSpeed = prefs[KEY_SPEED] ?: 1.0f,
            responseLength = prefs[KEY_RESPONSE_LENGTH] ?: "Concise",
            wakePhrase = prefs[KEY_WAKE_PHRASE] ?: "Maya",
            wakeWordEnabled = prefs[KEY_WAKE_WORD_ENABLED] ?: true,
            backgroundServiceEnabled = prefs[KEY_BG_SERVICE_ENABLED] ?: true,
            startOnBoot = prefs[KEY_START_ON_BOOT] ?: false,
            floatingAssistantEnabled = prefs[KEY_FLOATING_ASSISTANT] ?: false,
            echoGuardEnabled = prefs[KEY_ECHO_GUARD] ?: true,
            voiceGuardianEnabled = prefs[KEY_VOICE_GUARDIAN] ?: true,
            trustedVoiceTrained = prefs[KEY_TRUSTED_VOICE_TRAINED] ?: true,
            voicePrintSignature = prefs[KEY_VOICE_PRINT] ?: "ENROLLED_TIMBRE_HASH_ANKUSH_01",
            blockUnknownVoices = prefs[KEY_BLOCK_UNKNOWN_VOICES] ?: true,
            emergencyContactName = prefs[KEY_EMERGENCY_NAME] ?: "",
            emergencyContactPhone = prefs[KEY_EMERGENCY_PHONE] ?: "",
            emergencySosEnabled = prefs[KEY_EMERGENCY_SOS_ENABLED] ?: false,
            touchGuardEnabled = prefs[KEY_TOUCH_GUARD] ?: false,
            screenLockAnnounce = prefs[KEY_SCREEN_LOCK_ANNOUNCE] ?: true,
            whatsAppAutoReplyEnabled = prefs[KEY_WHATSAPP_AUTOREPLY] ?: false,
            whatsAppAutoReplyOnlyAsleep = prefs[KEY_WHATSAPP_ONLY_ASLEEP] ?: true,
            whatsAppAutoReplyIncludeGroups = prefs[KEY_WHATSAPP_INCLUDE_GROUPS] ?: false,
            whatsAppAutoReplyInstructions = prefs[KEY_WHATSAPP_INSTRUCTIONS] ?: "Politely say I am busy right now and will reply myself soon. Keep it short, warm and friendly. Reply in the same language they wrote in.",
            whatsAppAutoReplyFallback = prefs[KEY_WHATSAPP_FALLBACK] ?: "Abhi busy hoon, thodi der me reply karta hoon.",
            whatsAppAutoReplyNote = prefs[KEY_WHATSAPP_NOTE] ?: "- Maya (auto-reply)",
            whatsAppOnlyChats = prefs[KEY_WHATSAPP_ONLY_CHATS] ?: "",
            whatsAppNeverChats = prefs[KEY_WHATSAPP_NEVER_CHATS] ?: "Mummy, Boss",
            announceChargingConnected = prefs[KEY_ANNOUNCE_CHARGING] ?: true,
            announceBatteryLow = prefs[KEY_ANNOUNCE_BATTERY_LOW] ?: true,
            freeMinutesLeft = prefs[KEY_FREE_MINUTES] ?: 9999,
            freeSecondsLeft = prefs[KEY_FREE_SECONDS] ?: 0,
            isProUser = prefs[KEY_IS_PRO_USER] ?: true,
            phonePattern = prefs[KEY_PHONE_PATTERN] ?: "",
            phonePin = prefs[KEY_PHONE_PIN] ?: "",
            phoneUnlockEnabled = prefs[KEY_PHONE_UNLOCK_ENABLED] ?: false,
            appLockEnabled = prefs[KEY_APP_LOCK_ENABLED] ?: false,
            orbStyle = prefs[KEY_ORB_STYLE] ?: "MAYA 2047",
            orbColorName = prefs[KEY_ORB_COLOR] ?: "Persona",
            orbSizeDp = prefs[KEY_ORB_SIZE] ?: 230,
            useOrbOnHome = prefs[KEY_USE_ORB_HOME] ?: false,
            themeTypeface = prefs[KEY_THEME_TYPEFACE] ?: "Inter",
            themeFontSize = prefs[KEY_THEME_FONT_SIZE] ?: "Default",
            themeSurfaceStyle = prefs[KEY_THEME_SURFACE] ?: "Glass",
            themeCornerStyle = prefs[KEY_THEME_CORNER] ?: "Rounded",
            userGender = prefs[KEY_USER_GENDER] ?: "Male",
            userPhoneNumber = prefs[KEY_USER_PHONE] ?: "",
            musicApp = prefs[KEY_MUSIC_APP] ?: "Spotify",
            favoriteSong = prefs[KEY_FAVORITE_SONG] ?: "aaya vo fir najar aise baat jirne lagi fir s",
            socialHandle = prefs[KEY_SOCIAL_HANDLE] ?: "@ankush",
            socialDefaultPlatform = prefs[KEY_SOCIAL_PLATFORM] ?: "Instagram",
            socialCaptionVoice = prefs[KEY_SOCIAL_VOICE] ?: "funny, seedha simple, motivational",
            socialDailyStoryEnabled = prefs[KEY_SOCIAL_DAILY_STORY] ?: false,
            socialAutoPostEnabled = prefs[KEY_SOCIAL_AUTO_POST] ?: false,
            subAgentCodingModels = prefs[KEY_SUBAGENT_MODELS] ?: "gemini-2.5-flash,gemini-3.1-flash-lite,gemini-2.5-flash-lite",
            subAgentBrainDefault = prefs[KEY_SUBAGENT_BRAIN_DEFAULT] ?: true,
            conversationModeEnabled = prefs[KEY_CONVERSATION_MODE] ?: false,
            messageAlertsEnabled = prefs[KEY_MESSAGE_ALERTS] ?: true,
            autoStartWakeWord = prefs[KEY_AUTO_START_WAKE] ?: true,
            proactiveMaya = prefs[KEY_PROACTIVE_MAYA] ?: false,
            callAnnounceEnabled = prefs[KEY_CALL_ANNOUNCE] ?: true,
            keepRingtonePlaying = prefs[KEY_KEEP_RINGTONE] ?: false,
            drivingModeEnabled = prefs[KEY_DRIVING_MODE] ?: false,
            drivingModeAutoReply = prefs[KEY_DRIVING_REPLY] ?: "{name} abhi drive kar rahe hain, isliye call nahi utha paye. Free hote hi call back karenge. - Maya (auto reply)",
            pollinationsToken = prefs[KEY_POLLINATIONS_TOKEN] ?: "",
            customApiKey = prefs[KEY_CUSTOM_API_KEY] ?: ""
        )
    }

    suspend fun updateSettings(update: (AppSettings) -> AppSettings) {
        context.dataStore.edit { prefs ->
            val current = AppSettings(
                userName = prefs[KEY_USER_NAME] ?: "Ankush",
                assistantName = prefs[KEY_ASSISTANT_NAME] ?: "Maya",
                preferredLanguage = prefs[KEY_LANGUAGE] ?: "Auto (English / Hindi / Hinglish)",
                assistantVoice = prefs[KEY_VOICE] ?: "Aoede",
                assistantPersona = prefs[KEY_ASSISTANT_PERSONA] ?: "Maya",
                personalityStyle = prefs[KEY_PERSONALITY] ?: "AI Girlfriend Mode",
                speakingSpeed = prefs[KEY_SPEED] ?: 1.0f,
                responseLength = prefs[KEY_RESPONSE_LENGTH] ?: "Concise",
                wakePhrase = prefs[KEY_WAKE_PHRASE] ?: "Maya",
                wakeWordEnabled = prefs[KEY_WAKE_WORD_ENABLED] ?: true,
                backgroundServiceEnabled = prefs[KEY_BG_SERVICE_ENABLED] ?: true,
                startOnBoot = prefs[KEY_START_ON_BOOT] ?: false,
                floatingAssistantEnabled = prefs[KEY_FLOATING_ASSISTANT] ?: false,
                echoGuardEnabled = prefs[KEY_ECHO_GUARD] ?: true,
                voiceGuardianEnabled = prefs[KEY_VOICE_GUARDIAN] ?: true,
                trustedVoiceTrained = prefs[KEY_TRUSTED_VOICE_TRAINED] ?: true,
                voicePrintSignature = prefs[KEY_VOICE_PRINT] ?: "ENROLLED_TIMBRE_HASH_ANKUSH_01",
                blockUnknownVoices = prefs[KEY_BLOCK_UNKNOWN_VOICES] ?: true,
                emergencyContactName = prefs[KEY_EMERGENCY_NAME] ?: "",
                emergencyContactPhone = prefs[KEY_EMERGENCY_PHONE] ?: "",
                emergencySosEnabled = prefs[KEY_EMERGENCY_SOS_ENABLED] ?: false,
                touchGuardEnabled = prefs[KEY_TOUCH_GUARD] ?: false,
                screenLockAnnounce = prefs[KEY_SCREEN_LOCK_ANNOUNCE] ?: true,
                whatsAppAutoReplyEnabled = prefs[KEY_WHATSAPP_AUTOREPLY] ?: false,
                whatsAppAutoReplyOnlyAsleep = prefs[KEY_WHATSAPP_ONLY_ASLEEP] ?: true,
                whatsAppAutoReplyIncludeGroups = prefs[KEY_WHATSAPP_INCLUDE_GROUPS] ?: false,
                whatsAppAutoReplyInstructions = prefs[KEY_WHATSAPP_INSTRUCTIONS] ?: "Politely say I am busy right now and will reply myself soon. Keep it short, warm and friendly. Reply in the same language they wrote in.",
                whatsAppAutoReplyFallback = prefs[KEY_WHATSAPP_FALLBACK] ?: "Abhi busy hoon, thodi der me reply karta hoon.",
                whatsAppAutoReplyNote = prefs[KEY_WHATSAPP_NOTE] ?: "- Maya (auto-reply)",
                whatsAppOnlyChats = prefs[KEY_WHATSAPP_ONLY_CHATS] ?: "",
                whatsAppNeverChats = prefs[KEY_WHATSAPP_NEVER_CHATS] ?: "Mummy, Boss",
                announceChargingConnected = prefs[KEY_ANNOUNCE_CHARGING] ?: true,
                announceBatteryLow = prefs[KEY_ANNOUNCE_BATTERY_LOW] ?: true,
                freeMinutesLeft = prefs[KEY_FREE_MINUTES] ?: 9999,
                freeSecondsLeft = prefs[KEY_FREE_SECONDS] ?: 0,
                isProUser = prefs[KEY_IS_PRO_USER] ?: true,
                phonePattern = prefs[KEY_PHONE_PATTERN] ?: "",
                phonePin = prefs[KEY_PHONE_PIN] ?: "",
                phoneUnlockEnabled = prefs[KEY_PHONE_UNLOCK_ENABLED] ?: false,
                appLockEnabled = prefs[KEY_APP_LOCK_ENABLED] ?: false,
                orbStyle = prefs[KEY_ORB_STYLE] ?: "MAYA 2047",
                orbColorName = prefs[KEY_ORB_COLOR] ?: "Persona",
                orbSizeDp = prefs[KEY_ORB_SIZE] ?: 230,
                useOrbOnHome = prefs[KEY_USE_ORB_HOME] ?: false,
                themeTypeface = prefs[KEY_THEME_TYPEFACE] ?: "Inter",
                themeFontSize = prefs[KEY_THEME_FONT_SIZE] ?: "Default",
                themeSurfaceStyle = prefs[KEY_THEME_SURFACE] ?: "Glass",
                themeCornerStyle = prefs[KEY_THEME_CORNER] ?: "Rounded",
                userGender = prefs[KEY_USER_GENDER] ?: "Male",
                userPhoneNumber = prefs[KEY_USER_PHONE] ?: "",
                musicApp = prefs[KEY_MUSIC_APP] ?: "Spotify",
                favoriteSong = prefs[KEY_FAVORITE_SONG] ?: "aaya vo fir najar aise baat jirne lagi fir s",
                socialHandle = prefs[KEY_SOCIAL_HANDLE] ?: "@ankush",
                socialDefaultPlatform = prefs[KEY_SOCIAL_PLATFORM] ?: "Instagram",
                socialCaptionVoice = prefs[KEY_SOCIAL_VOICE] ?: "funny, seedha simple, motivational",
                socialDailyStoryEnabled = prefs[KEY_SOCIAL_DAILY_STORY] ?: false,
                socialAutoPostEnabled = prefs[KEY_SOCIAL_AUTO_POST] ?: false,
                subAgentCodingModels = prefs[KEY_SUBAGENT_MODELS] ?: "gemini-2.5-flash,gemini-3.1-flash-lite,gemini-2.5-flash-lite",
                subAgentBrainDefault = prefs[KEY_SUBAGENT_BRAIN_DEFAULT] ?: true,
                conversationModeEnabled = prefs[KEY_CONVERSATION_MODE] ?: false,
                messageAlertsEnabled = prefs[KEY_MESSAGE_ALERTS] ?: true,
                autoStartWakeWord = prefs[KEY_AUTO_START_WAKE] ?: true,
                proactiveMaya = prefs[KEY_PROACTIVE_MAYA] ?: false,
                callAnnounceEnabled = prefs[KEY_CALL_ANNOUNCE] ?: true,
                keepRingtonePlaying = prefs[KEY_KEEP_RINGTONE] ?: false,
                drivingModeEnabled = prefs[KEY_DRIVING_MODE] ?: false,
                drivingModeAutoReply = prefs[KEY_DRIVING_REPLY] ?: "{name} abhi drive kar rahe hain, isliye call nahi utha paye. Free hote hi call back karenge. - Maya (auto reply)",
                pollinationsToken = prefs[KEY_POLLINATIONS_TOKEN] ?: "",
                customApiKey = prefs[KEY_CUSTOM_API_KEY] ?: ""
            )
            val updated = update(current)
            prefs[KEY_USER_NAME] = updated.userName
            prefs[KEY_ASSISTANT_NAME] = updated.assistantName
            prefs[KEY_LANGUAGE] = updated.preferredLanguage
            prefs[KEY_VOICE] = updated.assistantVoice
            prefs[KEY_ASSISTANT_PERSONA] = updated.assistantPersona
            prefs[KEY_PERSONALITY] = updated.personalityStyle
            prefs[KEY_SPEED] = updated.speakingSpeed
            prefs[KEY_RESPONSE_LENGTH] = updated.responseLength
            prefs[KEY_WAKE_PHRASE] = updated.wakePhrase
            prefs[KEY_WAKE_WORD_ENABLED] = updated.wakeWordEnabled
            prefs[KEY_BG_SERVICE_ENABLED] = updated.backgroundServiceEnabled
            prefs[KEY_START_ON_BOOT] = updated.startOnBoot
            prefs[KEY_FLOATING_ASSISTANT] = updated.floatingAssistantEnabled
            prefs[KEY_ECHO_GUARD] = updated.echoGuardEnabled
            prefs[KEY_VOICE_GUARDIAN] = updated.voiceGuardianEnabled
            prefs[KEY_TRUSTED_VOICE_TRAINED] = updated.trustedVoiceTrained
            prefs[KEY_VOICE_PRINT] = updated.voicePrintSignature
            prefs[KEY_BLOCK_UNKNOWN_VOICES] = updated.blockUnknownVoices
            prefs[KEY_EMERGENCY_NAME] = updated.emergencyContactName
            prefs[KEY_EMERGENCY_PHONE] = updated.emergencyContactPhone
            prefs[KEY_EMERGENCY_SOS_ENABLED] = updated.emergencySosEnabled
            prefs[KEY_TOUCH_GUARD] = updated.touchGuardEnabled
            prefs[KEY_SCREEN_LOCK_ANNOUNCE] = updated.screenLockAnnounce
            prefs[KEY_WHATSAPP_AUTOREPLY] = updated.whatsAppAutoReplyEnabled
            prefs[KEY_WHATSAPP_ONLY_ASLEEP] = updated.whatsAppAutoReplyOnlyAsleep
            prefs[KEY_WHATSAPP_INCLUDE_GROUPS] = updated.whatsAppAutoReplyIncludeGroups
            prefs[KEY_WHATSAPP_INSTRUCTIONS] = updated.whatsAppAutoReplyInstructions
            prefs[KEY_WHATSAPP_FALLBACK] = updated.whatsAppAutoReplyFallback
            prefs[KEY_WHATSAPP_NOTE] = updated.whatsAppAutoReplyNote
            prefs[KEY_WHATSAPP_ONLY_CHATS] = updated.whatsAppOnlyChats
            prefs[KEY_WHATSAPP_NEVER_CHATS] = updated.whatsAppNeverChats
            prefs[KEY_ANNOUNCE_CHARGING] = updated.announceChargingConnected
            prefs[KEY_ANNOUNCE_BATTERY_LOW] = updated.announceBatteryLow
            prefs[KEY_FREE_MINUTES] = updated.freeMinutesLeft
            prefs[KEY_FREE_SECONDS] = updated.freeSecondsLeft
            prefs[KEY_IS_PRO_USER] = updated.isProUser
            prefs[KEY_PHONE_PATTERN] = updated.phonePattern
            prefs[KEY_PHONE_PIN] = updated.phonePin
            prefs[KEY_PHONE_UNLOCK_ENABLED] = updated.phoneUnlockEnabled
            prefs[KEY_APP_LOCK_ENABLED] = updated.appLockEnabled
            prefs[KEY_ORB_STYLE] = updated.orbStyle
            prefs[KEY_ORB_COLOR] = updated.orbColorName
            prefs[KEY_ORB_SIZE] = updated.orbSizeDp
            prefs[KEY_USE_ORB_HOME] = updated.useOrbOnHome
            prefs[KEY_THEME_TYPEFACE] = updated.themeTypeface
            prefs[KEY_THEME_FONT_SIZE] = updated.themeFontSize
            prefs[KEY_THEME_SURFACE] = updated.themeSurfaceStyle
            prefs[KEY_THEME_CORNER] = updated.themeCornerStyle
            prefs[KEY_USER_GENDER] = updated.userGender
            prefs[KEY_USER_PHONE] = updated.userPhoneNumber
            prefs[KEY_MUSIC_APP] = updated.musicApp
            prefs[KEY_FAVORITE_SONG] = updated.favoriteSong
            prefs[KEY_SOCIAL_HANDLE] = updated.socialHandle
            prefs[KEY_SOCIAL_PLATFORM] = updated.socialDefaultPlatform
            prefs[KEY_SOCIAL_VOICE] = updated.socialCaptionVoice
            prefs[KEY_SOCIAL_DAILY_STORY] = updated.socialDailyStoryEnabled
            prefs[KEY_SOCIAL_AUTO_POST] = updated.socialAutoPostEnabled
            prefs[KEY_SUBAGENT_MODELS] = updated.subAgentCodingModels
            prefs[KEY_SUBAGENT_BRAIN_DEFAULT] = updated.subAgentBrainDefault
            prefs[KEY_CONVERSATION_MODE] = updated.conversationModeEnabled
            prefs[KEY_MESSAGE_ALERTS] = updated.messageAlertsEnabled
            prefs[KEY_AUTO_START_WAKE] = updated.autoStartWakeWord
            prefs[KEY_PROACTIVE_MAYA] = updated.proactiveMaya
            prefs[KEY_CALL_ANNOUNCE] = updated.callAnnounceEnabled
            prefs[KEY_KEEP_RINGTONE] = updated.keepRingtonePlaying
            prefs[KEY_DRIVING_MODE] = updated.drivingModeEnabled
            prefs[KEY_DRIVING_REPLY] = updated.drivingModeAutoReply
            prefs[KEY_POLLINATIONS_TOKEN] = updated.pollinationsToken
            prefs[KEY_CUSTOM_API_KEY] = updated.customApiKey
        }
    }
}
