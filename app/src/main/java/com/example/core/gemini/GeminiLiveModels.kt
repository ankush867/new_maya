package com.example.core.gemini

import com.squareup.moshi.Json

data class LiveClientMessage(
    val setup: LiveSetup? = null,
    val realtimeInput: LiveRealtimeInput? = null,
    val clientContent: LiveClientContent? = null,
    val toolResponse: LiveToolResponse? = null
)

data class LiveSetup(
    val model: String = "models/gemini-2.0-flash-exp",
    val generationConfig: LiveGenerationConfig? = null,
    val systemInstruction: LiveContent? = null,
    val tools: List<LiveToolGroup>? = null
)

data class LiveGenerationConfig(
    val responseModalities: List<String> = listOf("AUDIO"),
    val speechConfig: LiveSpeechConfig? = null
)

data class LiveSpeechConfig(
    val voiceConfig: LiveVoiceConfig? = null
)

data class LiveVoiceConfig(
    val prebuiltVoiceConfig: LivePrebuiltVoiceConfig? = null
)

data class LivePrebuiltVoiceConfig(
    val voiceName: String = "Aoede"
)

data class LiveContent(
    val parts: List<LivePart> = emptyList(),
    val role: String? = null
)

data class LivePart(
    val text: String? = null,
    val inlineData: LiveInlineData? = null
)

data class LiveInlineData(
    val mimeType: String,
    val data: String
)

data class LiveRealtimeInput(
    val mediaChunks: List<LiveMediaChunk>
)

data class LiveMediaChunk(
    val mimeType: String = "audio/pcm;rate=16000",
    val data: String
)

data class LiveClientContent(
    val turns: List<LiveContent>,
    val turnComplete: Boolean = true
)

data class LiveToolGroup(
    val functionDeclarations: List<LiveFunctionDeclaration>
)

data class LiveFunctionDeclaration(
    val name: String,
    val description: String,
    val parameters: Map<String, Any>? = null
)

data class LiveToolResponse(
    val functionResponses: List<LiveFunctionResponse>
)

data class LiveFunctionResponse(
    val id: String,
    val name: String? = null,
    val response: Map<String, Any?>
)

data class LiveServerMessage(
    val serverContent: LiveServerContent? = null,
    val toolCall: LiveToolCall? = null,
    val toolCallCancellation: LiveToolCallCancellation? = null
)

data class LiveServerContent(
    val modelTurn: LiveContent? = null,
    val turnComplete: Boolean = false,
    val interrupted: Boolean = false
)

data class LiveToolCall(
    val functionCalls: List<LiveFunctionCall> = emptyList()
)

data class LiveFunctionCall(
    val id: String,
    val name: String,
    val args: Map<String, Any?> = emptyMap()
)

data class LiveToolCallCancellation(
    val ids: List<String> = emptyList()
)
