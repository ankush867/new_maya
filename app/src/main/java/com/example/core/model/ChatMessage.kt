package com.example.core.model

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isVoice: Boolean = true,
    val toolCallName: String? = null,
    val toolResult: String? = null
)

enum class MessageSender {
    USER,
    MAYA,
    SYSTEM
}
