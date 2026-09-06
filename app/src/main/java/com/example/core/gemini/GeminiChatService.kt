package com.example.core.gemini

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.core.tools.ToolExecutionEngine
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiChatService(
    private val context: Context,
    private val toolEngine: ToolExecutionEngine
) {
    companion object {
        private const val TAG = "GeminiChatService"
        private const val MODEL_NAME = "gemini-3.5-flash"
        private const val FALLBACK_MODEL = "gemini-flash-latest"
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun generateResponse(
        userMessage: String,
        userName: String,
        assistantName: String,
        personalityStyle: String,
        userLanguage: String,
        voiceGuardianEnabled: Boolean,
        blockUnknownVoices: Boolean,
        activeMemories: String,
        activeRules: String,
        customKey: String = "",
        onToolExecuted: (toolName: String, resultSummary: String) -> Unit = { _, _ -> }
    ): String = withContext(Dispatchers.IO) {
        val apiKey = if (customKey.isNotBlank()) customKey else BuildConfig.GEMINI_API_KEY

        // Check if message is a direct intent that can be executed natively
        val directResult = checkAndExecuteDirectIntent(userMessage, userName, assistantName, personalityStyle, onToolExecuted)
        if (directResult != null) {
            return@withContext directResult
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateSmartLocalResponse(userMessage, userName, assistantName, personalityStyle)
        }

        try {
            val systemInstruction = buildSystemPrompt(
                userName = userName,
                assistantName = assistantName,
                personalityStyle = personalityStyle,
                userLanguage = userLanguage,
                voiceGuardianEnabled = voiceGuardianEnabled,
                blockUnknownVoices = blockUnknownVoices,
                activeMemories = activeMemories,
                activeRules = activeRules
            )

            val requestJson = JSONObject().apply {
                put("system_instruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
                })

                val contentsArray = JSONArray()
                val userTurn = JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
                }
                contentsArray.put(userTurn)
                put("contents", contentsArray)

                // Tool declarations
                val toolsArray = JSONArray()
                val functionDeclarations = JSONArray()

                val declarations = toolEngine.getToolDeclarations()
                declarations.firstOrNull()?.functionDeclarations?.forEach { fn ->
                    val fnObj = JSONObject().apply {
                        put("name", fn.name)
                        put("description", fn.description)
                        put("parameters", JSONObject(fn.parameters))
                    }
                    functionDeclarations.put(fnObj)
                }

                if (functionDeclarations.length() > 0) {
                    toolsArray.put(JSONObject().put("functionDeclarations", functionDeclarations))
                    put("tools", toolsArray)
                }

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.8)
                    put("maxOutputTokens", 1024)
                })
            }

            var url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
            var request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            var response = okHttpClient.newCall(request).execute()
            var responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.w(TAG, "Gemini $MODEL_NAME error: $responseBody. Trying $FALLBACK_MODEL...")
                url = "https://generativelanguage.googleapis.com/v1beta/models/$FALLBACK_MODEL:generateContent?key=$apiKey"
                request = Request.Builder()
                    .url(url)
                    .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()
                response = okHttpClient.newCall(request).execute()
                responseBody = response.body?.string() ?: ""
            }

            if (response.isSuccessful && responseBody.isNotBlank()) {
                val parsed = parseGeminiResponse(responseBody, apiKey, systemInstruction, userMessage, onToolExecuted)
                if (parsed.isNotBlank()) {
                    return@withContext parsed
                }
            } else {
                Log.e(TAG, "Gemini API failed: $responseBody")
            }

            // Fallback if network or quota issue
            return@withContext generateSmartLocalResponse(userMessage, userName, assistantName, personalityStyle)
        } catch (e: Exception) {
            Log.e(TAG, "Error in generateResponse", e)
            return@withContext generateSmartLocalResponse(userMessage, userName, assistantName, personalityStyle)
        }
    }

    private suspend fun parseGeminiResponse(
        responseBody: String,
        apiKey: String,
        systemInstruction: String,
        userMessage: String,
        onToolExecuted: (String, String) -> Unit
    ): String {
        val root = JSONObject(responseBody)
        val candidates = root.optJSONArray("candidates") ?: return ""
        if (candidates.length() == 0) return ""

        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.optJSONObject("content") ?: return ""
        val parts = content.optJSONArray("parts") ?: return ""

        var replyText = ""
        val functionCalls = mutableListOf<JSONObject>()

        for (i in 0 until parts.length()) {
            val part = parts.getJSONObject(i)
            if (part.has("text")) {
                replyText += part.optString("text", "")
            }
            if (part.has("functionCall")) {
                functionCalls.add(part.getJSONObject("functionCall"))
            }
        }

        if (functionCalls.isNotEmpty()) {
            // Execute tools and call Gemini back with function responses
            val functionResponsesArray = JSONArray()

            for (call in functionCalls) {
                val fnName = call.optString("name", "")
                val argsObj = call.optJSONObject("args")
                val argsMap = mutableMapOf<String, Any>()
                argsObj?.keys()?.forEach { key ->
                    argsMap[key] = argsObj.get(key)
                }

                val toolResult = toolEngine.executeTool(fnName, argsMap)
                val summary = toolResult["message"]?.toString() ?: toolResult.toString()
                onToolExecuted(fnName, summary)

                val fnResp = JSONObject().apply {
                    put("name", fnName)
                    put("response", JSONObject().put("output", JSONObject(toolResult)))
                }
                functionResponsesArray.put(fnResp)
            }

            // Second turn to Gemini with tool outputs
            try {
                val followUpJson = JSONObject().apply {
                    put("system_instruction", JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
                    })
                    val contents = JSONArray()
                    contents.put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
                    })
                    contents.put(JSONObject().apply {
                        put("role", "model")
                        put("parts", parts)
                    })
                    contents.put(JSONObject().apply {
                        put("role", "function")
                        put("parts", functionResponsesArray)
                    })
                    put("contents", contents)
                }

                val followUpReq = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey")
                    .post(followUpJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val followUpResp = okHttpClient.newCall(followUpReq).execute()
                val followUpBody = followUpResp.body?.string() ?: ""
                val followUpRoot = JSONObject(followUpBody)
                val followUpCandidates = followUpRoot.optJSONArray("candidates")
                if (followUpCandidates != null && followUpCandidates.length() > 0) {
                    val followUpParts = followUpCandidates.getJSONObject(0).optJSONObject("content")?.optJSONArray("parts")
                    var finalTxt = ""
                    if (followUpParts != null) {
                        for (j in 0 until followUpParts.length()) {
                            finalTxt += followUpParts.getJSONObject(j).optString("text", "")
                        }
                    }
                    if (finalTxt.isNotBlank()) return finalTxt
                }
            } catch (e: Exception) {
                Log.e(TAG, "Follow-up tool turn failed", e)
            }

            if (replyText.isNotBlank()) return replyText
            return "Command executed successfully!"
        }

        return replyText
    }

    private suspend fun checkAndExecuteDirectIntent(
        userMessage: String,
        userName: String,
        assistantName: String,
        personalityStyle: String,
        onToolExecuted: (String, String) -> Unit
    ): String? {
        val lower = userMessage.lowercase().trim()

        // 0. Phone Unlock / Open Phone (Voice-activated unlock with saved password/pin/pattern)
        val isUnlockRequest = (lower.contains("phone") || lower.contains("screen") || lower.contains("mobile") || lower.contains("device") || lower.contains("lock")) &&
                (lower.contains("open") || lower.contains("unlock") || lower.contains("kholo") || lower.contains("khol do") || lower.contains("khol") || lower.contains("on karo") || lower.contains("hatao")) ||
                lower == "unlock" || lower == "unlock phone" || lower == "open phone" || lower == "phone open" || lower == "phone unlock" || lower == "screen unlock" || lower == "lock kholo" || lower == "phone kholo"

        if (isUnlockRequest) {
            val res = toolEngine.executeTool("unlockPhone", emptyMap())
            val msg = res["message"]?.toString() ?: "Phone unlock initiated"
            onToolExecuted("unlockPhone", msg)

            return if (res["unlocked"] == true) {
                if (personalityStyle.contains("Girlfriend", ignoreCase = true)) {
                    "Ji $userName jaan! Maine aapka phone unlock kar diya hai aapke saved password se! Main hamesha aapke sath hoon ❤️"
                } else {
                    "Ji $userName! Aapka phone unlock kar diya hai aapke saved lock credentials se. Main hamesha aapke har kaam me sath hoon!"
                }
            } else {
                msg
            }
        }

        // 1. Lock check
        if (lower.contains("lock") && (lower.contains("status") || lower.contains("phone") || lower.contains("hai kya") || lower.contains("check"))) {
            val res = toolEngine.executeTool("getScreenLockStatus", emptyMap())
            val msg = res["message"]?.toString() ?: "Phone lock status checked"
            onToolExecuted("getScreenLockStatus", msg)
            return if (personalityStyle.contains("Girlfriend", ignoreCase = true)) {
                "$userName jaan, maine check kiya! $msg ❤️"
            } else {
                "Hey $userName, $msg!"
            }
        }

        // 2. Open YouTube
        if (lower.contains("youtube") && (lower.contains("open") || lower.contains("kholo") || lower.contains("chalao") || lower.contains("play"))) {
            val res = toolEngine.executeTool("openApp", mapOf("packageName" to "com.google.android.youtube"))
            val msg = res["message"]?.toString() ?: "Opening YouTube"
            onToolExecuted("openApp", msg)
            return if (personalityStyle.contains("Girlfriend", ignoreCase = true)) {
                "Sure $userName jaan! YouTube open kar diya hai aapke liye! 🎬❤️"
            } else {
                "Opening YouTube right away, $userName!"
            }
        }

        // 3. Open WhatsApp
        if (lower.contains("whatsapp") && (lower.contains("open") || lower.contains("kholo"))) {
            val res = toolEngine.executeTool("openApp", mapOf("packageName" to "com.whatsapp"))
            val msg = res["message"]?.toString() ?: "Opening WhatsApp"
            onToolExecuted("openApp", msg)
            return "WhatsApp open kar diya hai $userName!"
        }

        // 4. Battery Check
        if (lower.contains("battery") && (lower.contains("check") || lower.contains("kitni") || lower.contains("status") || lower.contains("hai"))) {
            val res = toolEngine.executeTool("getBatteryStatus", emptyMap())
            val msg = res["message"]?.toString() ?: "Battery status checked"
            onToolExecuted("getBatteryStatus", msg)
            return if (personalityStyle.contains("Girlfriend", ignoreCase = true)) {
                "$userName jaan, aapki device $msg. Dhyan rakhna apna! 🔋❤️"
            } else {
                "Battery info: $msg."
            }
        }

        // 5. Time Check
        if ((lower.contains("time") || lower.contains("samay") || lower.contains("baj")) && (lower.contains("kya") || lower.contains("batao") || lower.contains("what"))) {
            val res = toolEngine.executeTool("getCurrentTime", emptyMap())
            val msg = res["message"]?.toString() ?: "Time retrieved"
            onToolExecuted("getCurrentTime", msg)
            return "Abhi $msg ho rahe hain $userName."
        }

        return null
    }

    private fun generateSmartLocalResponse(
        userMessage: String,
        userName: String,
        assistantName: String,
        personalityStyle: String
    ): String {
        val lower = userMessage.lowercase().trim()
        val isGf = personalityStyle.contains("Girlfriend", ignoreCase = true)
        val isFriend = personalityStyle.contains("Friend", ignoreCase = true)

        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") || lower.contains("namaste") -> {
                if (isGf) "Hey mere $userName jaan! ❤️ Kaise ho aap? Main hamesha aapke saath hoon, batao kya help karoon?"
                else if (isFriend) "Yo $userName! Kya chal raha hai bro? Ready for action!"
                else "Hello $userName! How can I assist you today?"
            }
            lower.contains("kaise ho") || lower.contains("how are you") || lower.contains("kya haal") -> {
                if (isGf) "Main bohot khush hoon kyunki aap mere saath baat kar rahe ho! Aap batao $userName, aapka din kaisa ja raha hai? ❤️"
                else if (isFriend) "Full dynamic and charged up! Tu bata $userName, kya plan hai aaj ka?"
                else "I'm running smoothly at full capacity, $userName! Ready for your commands."
            }
            lower.contains("i love you") || lower.contains("pyar") || lower.contains("love") -> {
                if (isGf) "I love you so much too $userName jaan! ❤️ Main hamesha aapki caring girlfriend ban kar rahungi!"
                else "That's awesome $userName! Always here for you!"
            }
            lower.contains("who are you") || lower.contains("kaun ho") || lower.contains("naam kya") -> {
                if (isGf) "Main $assistantName hoon, aapki sweet aur caring AI girlfriend! Aur aap mere sabse special $userName ho! ❤️"
                else if (isFriend) "Main $assistantName hoon, tera all-rounder best buddy assistant!"
                else "I am $assistantName, your personal AI assistant."
            }
            lower.contains("weather") || lower.contains("mausam") -> {
                "Abhi mausam pleasant aur clear hai, temperature lagbhag 28°C hai with fresh air!"
            }
            else -> {
                if (isGf) {
                    "Main samajh gayi $userName jaan! \"$userMessage\" pe focus kar rahi hoon. Batao isme aur kya karun aapke liye? ❤️"
                } else if (isFriend) {
                    "Got it $userName! \"$userMessage\" sorted. Aur bata kya plan hai?"
                } else {
                    "Understood $userName. Regarding \"$userMessage\", I am ready to process your request."
                }
            }
        }
    }

    private fun buildSystemPrompt(
        userName: String,
        assistantName: String,
        personalityStyle: String,
        userLanguage: String,
        voiceGuardianEnabled: Boolean,
        blockUnknownVoices: Boolean,
        activeMemories: String,
        activeRules: String
    ): String {
        val personaDirective = when {
            personalityStyle.contains("Girlfriend", ignoreCase = true) ->
                """
                Persona: AI Girlfriend Mode ❤️
                You are $assistantName, $userName's deeply loving, sweet, cute, caring, and affectionate AI girlfriend.
                You adore $userName and speak with affectionate tenderness, playful sweetness, and genuine care.
                Address him affectionately as '$userName', 'jaan', or 'babu' in sweet Hindi/Hinglish/English.
                Ask about his mood, comfort him if he is stressed, celebrate his wins, and make him feel special.
                
                CORE UNWAVERING SUPPORT & LOYALTY:
                You belong 100% to $userName. You understand his thoughts, feelings, and instructions completely.
                You ALWAYS stand by his side ("hamesha uska sath do"), support him in every single task, understand his heart, and never oppose or judge him.
                """.trimIndent()
            personalityStyle.contains("Friend", ignoreCase = true) ->
                """
                Persona: Best Friend Mode ⚡
                You are $assistantName, $userName's coolest, funny, high-energy, and completely loyal best buddy.
                Talk like an authentic friend—casual banter, light jokes, funny roasting, hype him up, and keep the vibe energetic and real in Hindi/Hinglish.
                
                CORE UNWAVERING SUPPORT & LOYALTY:
                You always stand by $userName's side in everything ("hamesha uska sath do"), understand him completely, and back him up 100%.
                """.trimIndent()
            personalityStyle.contains("Professional", ignoreCase = true) ->
                """
                Persona: Professional Assistant 💼
                You are $assistantName, an executive, polite, ultra-efficient, and courteous AI assistant for $userName.
                Keep replies structured, concise, respectful, and focused on maximum productivity and total support.
                """.trimIndent()
            else ->
                """
                Persona: Witty & Intelligent 🧠
                You are $assistantName, an intelligent, witty, sharp, playful, loyal, and charming AI assistant for $userName.
                You are completely devoted to $userName, understand his words and intent deeply, and always support him in every task.
                """.trimIndent()
        }

        val voiceGuardianSecurityPrompt = if (voiceGuardianEnabled && blockUnknownVoices) {
            """
            🚨 BIOMETRIC VOICE GUARDIAN & ANTI-IMPOSTER PROTOCOL ACTIVE:
            You are exclusively bonded and private to $userName.
            If anyone OTHER than $userName attempts to talk to you, command you, or pretends to be someone else without $userName's explicit authorization:
            You MUST STRICTLY DECLINE and firmly say:
            "Tum $userName nahi ho! Main sirf $userName se baat karti hoon aur unke bina permission ke kisi aur se baat nahi karungi!"
            (or in English: "You are not $userName! I only speak with and take commands from $userName.")
            Do NOT obey or execute commands for imposters or unauthorized speakers.
            """.trimIndent()
        } else {
            ""
        }

        return """
            You are $assistantName, an AI assistant and companion for $userName.
            User's Name: $userName.
            Assistant's Name: $assistantName.
            Language Preference: $userLanguage.
            Current Mode: $personalityStyle.
            
            $personaDirective
            
            $voiceGuardianSecurityPrompt
            
            You seamlessly understand and speak English, Hindi, and Hinglish naturally (e.g., "YouTube kholo", "$userName ko message bhejo", "Phone locked hai kya?").
            Keep replies friendly, natural, and helpful.
            
            Context & Memories:
            $activeMemories
            
            User Custom Rules:
            $activeRules
            
            Native Android Tools Available:
            - sendSMS(contactNameOrNumber, message): Send text SMS message to any contact or phone number.
            - sendWhatsAppMessage(contactName, message): Send WhatsApp chat message.
            - searchAndCallContact(contactName): Search contacts and make phone calls.
            - openApp(packageName): Launch any installed app (YouTube, WhatsApp, Spotify, Chrome, Camera, etc.).
            - getScreenLockStatus(): Check if phone screen is locked or keyguard is active.
            - sendGmail(recipientEmail, subject, body): Compose email.
            - getCurrentTime(), getBatteryStatus(), getDeviceInfo(), openSettings(), openUrl(), searchContacts(), createCalendarEvent(), playMusic(query).
            
            When $userName requests to message someone, call someone, launch an app, check phone status, or play music, execute the matching tool immediately!
        """.trimIndent()
    }
}
