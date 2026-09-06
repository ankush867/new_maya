package com.example.data.repository

import android.content.Context
import com.example.core.audio.AudioCaptureManager
import com.example.core.audio.AudioPlaybackManager
import com.example.core.audio.SpeechRecognizerHelper
import com.example.core.audio.TextToSpeechHelper
import com.example.core.audio.WakeWordManager
import com.example.core.audio.WakeWordState
import com.example.core.gemini.GeminiChatService
import com.example.core.gemini.GeminiVisionService
import com.example.core.gemini.LiveSessionManager
import com.example.core.model.AppSettings
import com.example.core.model.AssistantState
import com.example.core.model.ChatMessage
import com.example.core.model.ConnectionState
import com.example.core.model.JournalEntry
import com.example.core.model.MayaRule
import com.example.core.model.MemoryItem
import com.example.core.model.MessageSender
import com.example.core.tools.ToolExecutionEngine
import com.example.data.local.MayaDatabase
import com.example.data.local.PreferencesDataStore
import com.example.data.local.entity.ChatEntity
import com.example.data.local.entity.JournalEntity
import com.example.data.local.entity.MemoryEntity
import com.example.data.local.entity.RuleEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class AssistantRepository private constructor(private val context: Context) {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val db = MayaDatabase.getInstance(context)
    val preferences = PreferencesDataStore(context)

    val audioCapture = AudioCaptureManager(context, repositoryScope)
    val audioPlayback = AudioPlaybackManager(repositoryScope)
    val toolEngine = ToolExecutionEngine(context)
    val visionService = GeminiVisionService()
    val ttsHelper = TextToSpeechHelper(context)
    val speechRecognizerHelper = SpeechRecognizerHelper(context)
    val geminiChatService = GeminiChatService(context, toolEngine)

    val liveSession = LiveSessionManager(
        context = context,
        scope = repositoryScope,
        audioCapture = audioCapture,
        audioPlayback = audioPlayback,
        toolEngine = toolEngine
    )

    val wakeWord = WakeWordManager(context, repositoryScope)

    private val _chatState = MutableStateFlow(AssistantState.IDLE)

    val assistantState: StateFlow<AssistantState> = combine(
        liveSession.assistantState,
        _chatState,
        ttsHelper.isSpeaking,
        speechRecognizerHelper.isListening
    ) { liveState, chatState, isSpeaking, isListening ->
        when {
            isSpeaking -> AssistantState.SPEAKING
            isListening -> AssistantState.LISTENING
            chatState == AssistantState.THINKING || chatState == AssistantState.EXECUTING_TOOL -> chatState
            chatState == AssistantState.LISTENING -> AssistantState.LISTENING
            liveState != AssistantState.IDLE -> liveState
            else -> AssistantState.IDLE
        }
    }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.Eagerly,
        initialValue = AssistantState.IDLE
    )

    val connectionState: StateFlow<ConnectionState> = liveSession.connectionState
    val wakeWordState: StateFlow<WakeWordState> = wakeWord.state

    val micAmplitude: StateFlow<Float> = combine(
        audioCapture.audioAmplitude,
        speechRecognizerHelper.rmsAmplitude
    ) { a, b -> maxOf(a, b) }.stateIn(
        scope = repositoryScope,
        started = SharingStarted.Eagerly,
        initialValue = 0f
    )
    val speakerAmplitude: StateFlow<Float> = audioPlayback.playbackAmplitude

    val settings: StateFlow<AppSettings> = preferences.settingsFlow.stateIn(
        scope = repositoryScope,
        started = SharingStarted.Eagerly,
        initialValue = AppSettings()
    )

    val memories: Flow<List<MemoryItem>> = db.memoryDao().getAllMemoriesFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    val journalEntries: Flow<List<JournalEntry>> = db.journalDao().getAllEntriesFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    val rules: Flow<List<MayaRule>> = db.ruleDao().getAllRulesFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    val chatHistory: Flow<List<ChatMessage>> = db.chatDao().getAllMessagesFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    // Energy tracking for card UI (0 to 100)
    private val _energyLevel = MutableStateFlow(96)
    val energyLevel: StateFlow<Int> = _energyLevel.asStateFlow()

    init {
        // Populate default rules if database is empty
        repositoryScope.launch(Dispatchers.IO) {
            val existing = db.ruleDao().getEnabledRules()
            if (existing.isEmpty()) {
                db.ruleDao().insertRules(
                    listOf(
                        RuleEntity(0, "Witty & Casual Persona", "Maya responds with youthful charm, light wit, and intelligence.", true, "Behavior"),
                        RuleEntity(0, "Concise Spoken Voice", "Keep verbal replies brief and conversational, perfect for headphones.", true, "Response"),
                        RuleEntity(0, "Multilingual Auto-Detect", "Automatically answer in Hinglish, Hindi, or English based on user speech.", true, "Response"),
                        RuleEntity(0, "Confirm High-Impact Tools", "Confirm contact selection when multiple people share the same name.", true, "Safety"),
                        RuleEntity(0, "Battery & Charging Alerts", "Announce when charging starts or battery falls below 20%.", true, "Execution")
                    )
                )
            }

            // Sync settings to LiveSession
            settings.collect { s ->
                liveSession.userName = s.userName
                liveSession.assistantName = s.assistantName
                liveSession.userLanguage = s.preferredLanguage
                liveSession.assistantVoice = s.assistantVoice
                liveSession.personalityStyle = s.personalityStyle
                liveSession.voiceGuardianEnabled = s.voiceGuardianEnabled
                liveSession.blockUnknownVoices = s.blockUnknownVoices
                liveSession.customApiKey = s.customApiKey
                wakeWord.wakePhrase = s.wakePhrase
            }
        }

        // Listen for new messages from liveSession to persist in database
        repositoryScope.launch(Dispatchers.IO) {
            liveSession.newMessages.collect { msg ->
                db.chatDao().insertMessage(ChatEntity.fromDomain(msg))
            }
        }

        // Voice input recognized from device microphone
        speechRecognizerHelper.onSpeechResult = { spokenText ->
            sendUserTextMessage(spokenText)
        }

        // Wake word callback
        wakeWord.onWakeWordTriggered = {
            startAssistantVoiceSession()
        }
    }

    fun toggleVoiceAssistant() {
        if (assistantState.value == AssistantState.IDLE || assistantState.value == AssistantState.OFFLINE || assistantState.value == AssistantState.ERROR) {
            startAssistantVoiceSession(withGreeting = true)
        } else {
            stopAssistantVoiceSession()
        }
    }

    fun startAssistantVoiceSession(withGreeting: Boolean = true) {
        wakeWord.onSessionActive()
        val currentSettings = settings.value
        val rawName = currentSettings.userName.trim()
        val displayName = if (rawName.isNotBlank()) rawName else "User"

        val langCode = when {
            currentSettings.preferredLanguage.contains("Hindi", ignoreCase = true) -> "hi-IN"
            currentSettings.preferredLanguage.contains("English", ignoreCase = true) -> "en-IN"
            else -> "hi-IN"
        }

        if (withGreeting) {
            val greeting = "Hey $displayName! Kaise ho? Main hamesha aapke sath hoon, boliye kya kaam karna hai!"
            _chatState.value = AssistantState.SPEAKING

            // Show greeting in chat
            repositoryScope.launch(Dispatchers.IO) {
                db.chatDao().insertMessage(
                    ChatEntity.fromDomain(
                        ChatMessage(
                            sender = MessageSender.MAYA,
                            text = greeting,
                            isVoice = true
                        )
                    )
                )
            }

            ttsHelper.speak(greeting, speed = 1.02f) {
                _chatState.value = AssistantState.LISTENING
                speechRecognizerHelper.startListening(languageCode = langCode, continuous = false)
                liveSession.startLiveSession()
            }
        } else {
            _chatState.value = AssistantState.LISTENING
            speechRecognizerHelper.startListening(languageCode = langCode, continuous = false)
            liveSession.startLiveSession()
        }
    }

    fun stopAssistantVoiceSession() {
        speechRecognizerHelper.stopListening()
        liveSession.stopLiveSession()
        ttsHelper.stop()
        _chatState.value = AssistantState.IDLE
        wakeWord.onSessionFinished()
        if (settings.value.wakeWordEnabled) {
            wakeWord.startListening()
        }
    }

    fun interruptSpeaking() {
        speechRecognizerHelper.cancel()
        liveSession.handleBargeIn()
        ttsHelper.stop()
        _chatState.value = AssistantState.IDLE
    }

    fun sendUserTextMessage(text: String) {
        if (text.isBlank()) return

        repositoryScope.launch(Dispatchers.IO) {
            // 1. Insert user message in database
            val userMsg = ChatMessage(sender = MessageSender.USER, text = text, isVoice = false)
            db.chatDao().insertMessage(ChatEntity.fromDomain(userMsg))

            _chatState.value = AssistantState.THINKING

            val currentSettings = settings.value
            val memoriesList = db.memoryDao().getAllMemories()
            val memoriesText = memoriesList.joinToString("\n") { "- ${it.key}: ${it.value}" }
            val rulesList = db.ruleDao().getEnabledRules()
            val rulesText = rulesList.joinToString("\n") { "- ${it.title}: ${it.description}" }

            // 2. Generate response via GeminiChatService
            val responseText = geminiChatService.generateResponse(
                userMessage = text,
                userName = currentSettings.userName,
                assistantName = currentSettings.assistantName,
                personalityStyle = currentSettings.personalityStyle,
                userLanguage = currentSettings.preferredLanguage,
                voiceGuardianEnabled = currentSettings.voiceGuardianEnabled,
                blockUnknownVoices = currentSettings.blockUnknownVoices,
                activeMemories = memoriesText,
                activeRules = rulesText,
                customKey = currentSettings.customApiKey,
                onToolExecuted = { toolName, summary ->
                    repositoryScope.launch(Dispatchers.IO) {
                        db.chatDao().insertMessage(
                            ChatEntity.fromDomain(
                                ChatMessage(
                                    sender = MessageSender.SYSTEM,
                                    text = "Executed '$toolName': $summary",
                                    isVoice = false,
                                    toolCallName = toolName,
                                    toolResult = summary
                                )
                            )
                        )
                    }
                }
            )

            // 3. Insert Maya assistant response in database
            val mayaMsg = ChatMessage(sender = MessageSender.MAYA, text = responseText, isVoice = false)
            db.chatDao().insertMessage(ChatEntity.fromDomain(mayaMsg))

            _chatState.value = AssistantState.IDLE

            // 4. Vocalize response if desired
            ttsHelper.speak(responseText, currentSettings.speakingSpeed)
        }
    }

    // Dynamic Greeting Calculator
    fun getGreetingText(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good morning,"
            in 12..16 -> "Good afternoon,"
            in 17..21 -> "Good evening,"
            else -> "Good night,"
        }
    }

    // Memory operations
    suspend fun saveMemory(key: String, value: String, category: String = "General") {
        db.memoryDao().insertMemory(
            MemoryEntity(
                id = 0,
                key = key,
                value = value,
                category = category,
                timestamp = System.currentTimeMillis(),
                isAutoSaved = false
            )
        )
    }

    suspend fun deleteMemory(id: Long) {
        db.memoryDao().deleteById(id)
    }

    suspend fun clearAllMemories() {
        db.memoryDao().clearAll()
    }

    // Journal operations
    suspend fun saveJournalEntry(title: String, content: String, mood: String, tags: List<String> = emptyList()) {
        db.journalDao().insertEntry(
            JournalEntity(
                id = 0,
                title = title,
                content = content,
                mood = mood,
                timestamp = System.currentTimeMillis(),
                tagsRaw = tags.joinToString(",")
            )
        )
    }

    suspend fun deleteJournalEntry(id: Long) {
        db.journalDao().deleteById(id)
    }

    // Rules operations
    suspend fun toggleRule(id: Long, isEnabled: Boolean) {
        db.ruleDao().setRuleEnabled(id, isEnabled)
    }

    suspend fun saveRule(title: String, description: String, isEnabled: Boolean = true, category: String = "Custom") {
        db.ruleDao().insertRule(RuleEntity(0, title, description, isEnabled, category))
    }

    suspend fun deleteRule(id: Long) {
        db.ruleDao().deleteById(id)
    }

    suspend fun addCustomRule(title: String, description: String, category: String = "Custom") {
        db.ruleDao().insertRule(RuleEntity(0, title, description, true, category))
    }

    // Chat history operations
    suspend fun clearChatHistory() {
        db.chatDao().clearHistory()
    }

    companion object {
        @Volatile
        private var INSTANCE: AssistantRepository? = null

        fun getInstance(context: Context): AssistantRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = AssistantRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
