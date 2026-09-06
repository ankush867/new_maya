package com.example.core.model

data class AppSettings(
    val userName: String = "Ankush",
    val assistantName: String = "Maya",
    val preferredLanguage: String = "Auto (English / Hindi / Hinglish)",
    val assistantVoice: String = "Aoede", // Aoede (Breezy), Kore (Firm), Leda (Youthful), Zephyr (Bright), etc.
    val assistantPersona: String = "Maya", // Maya, Friday, Venom
    val personalityStyle: String = "AI Girlfriend Mode", // AI Girlfriend Mode, Best Friend Mode, Witty & Intelligent, Professional Assistant
    val speakingSpeed: Float = 1.0f,
    val responseLength: String = "Concise", // Concise, Balanced, Detailed
    val wakePhrase: String = "Maya",
    val wakeAliases: List<String> = listOf("Hey Maya", "Maya listen", "Zoya"),
    val wakeWordEnabled: Boolean = true,
    val backgroundServiceEnabled: Boolean = true,
    val startOnBoot: Boolean = false,
    val floatingAssistantEnabled: Boolean = false,
    val echoGuardEnabled: Boolean = true,
    val voiceGuardianEnabled: Boolean = true,
    val trustedVoiceTrained: Boolean = true,
    val voicePrintSignature: String = "ENROLLED_TIMBRE_HASH_ANKUSH_01",
    val blockUnknownVoices: Boolean = true,
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val emergencySosEnabled: Boolean = false,
    val touchGuardEnabled: Boolean = false,
    val screenLockAnnounce: Boolean = true,
    val whatsAppAutoReplyEnabled: Boolean = false,
    val whatsAppAutoReplyOnlyAsleep: Boolean = true,
    val whatsAppAutoReplyIncludeGroups: Boolean = false,
    val whatsAppAutoReplyInstructions: String = "Politely say I am busy right now and will reply myself soon. Keep it short, warm and friendly. Reply in the same language they wrote in.",
    val whatsAppAutoReplyFallback: String = "Abhi busy hoon, thodi der me reply karta hoon.",
    val whatsAppAutoReplyNote: String = "- Maya (auto-reply)",
    val whatsAppOnlyChats: String = "",
    val whatsAppNeverChats: String = "Mummy, Boss",
    val announceChargingConnected: Boolean = true,
    val announceBatteryLow: Boolean = true,
    val freeMinutesLeft: Int = 9999,
    val freeSecondsLeft: Int = 0,
    val isProUser: Boolean = true,
    val accentColorHex: String = "#00F0FF",
    // Lock System & Phone Unlock
    val phonePattern: String = "", // Comma-separated dot indexes e.g. "0,1,2,5"
    val phonePin: String = "", // Saved PIN or password
    val phoneUnlockEnabled: Boolean = false, // Let Maya unlock the phone
    val appLockEnabled: Boolean = false, // In-app lock screen
    // Appearance & Orb Customization
    val orbStyle: String = "MAYA 2047", // MAYA 2047, Maya Nova, Jarvis
    val orbColorName: String = "Persona", // Persona, Jarvis Orange, Ultron Blue, Neon, Green
    val orbSizeDp: Int = 230,
    val useOrbOnHome: Boolean = false,
    val orbGlowIntensity: Float = 0.8f,
    // Theme Customization
    val themeTypeface: String = "Inter", // Inter, System, Serif, Monospace, Handwritten
    val themeFontSize: String = "Default", // Compact, Default, Large, Larger
    val themeSurfaceStyle: String = "Glass", // Flat, Glass, Soft, Clay
    val themeCornerStyle: String = "Rounded", // Sharp, Soft, Rounded, Pillowy
    // Personal Settings
    val userGender: String = "Male", // Male, Female, Prefer not to say
    val userPhoneNumber: String = "",
    val musicApp: String = "Spotify", // YT Music, Spotify, YouTube
    val favoriteSong: String = "aaya vo fir najar aise baat jirne lagi fir s",
    // Social Media
    val socialHandle: String = "@ankush",
    val socialDefaultPlatform: String = "Instagram", // Instagram, Facebook
    val socialCaptionVoice: String = "funny, seedha simple, motivational",
    val socialDailyStoryEnabled: Boolean = false,
    val socialAutoPostEnabled: Boolean = false,
    // Sub-agents
    val subAgentCodingModels: String = "gemini-2.5-flash,gemini-3.1-flash-lite,gemini-2.5-flash-lite",
    val subAgentBrainDefault: Boolean = true,
    // Assistant Settings
    val conversationModeEnabled: Boolean = false,
    val messageAlertsEnabled: Boolean = true,
    val autoStartWakeWord: Boolean = true,
    val proactiveMaya: Boolean = false,
    val callAnnounceEnabled: Boolean = true,
    val keepRingtonePlaying: Boolean = false,
    val drivingModeEnabled: Boolean = false,
    val drivingModeAutoReply: String = "{name} abhi drive kar rahe hain, isliye call nahi utha paye. Free hote hi call back karenge. - Maya (auto reply)",
    val pollinationsToken: String = "",
    val customApiKey: String = ""
)
