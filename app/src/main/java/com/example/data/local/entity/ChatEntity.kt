package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.model.ChatMessage
import com.example.core.model.MessageSender

@Entity(tableName = "chat_history")
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val senderName: String,
    val text: String,
    val timestamp: Long,
    val isVoice: Boolean,
    val toolCallName: String?,
    val toolResult: String?
) {
    fun toDomain(): ChatMessage = ChatMessage(
        id = id,
        sender = when (senderName) {
            "USER" -> MessageSender.USER
            "MAYA" -> MessageSender.MAYA
            else -> MessageSender.SYSTEM
        },
        text = text,
        timestamp = timestamp,
        isVoice = isVoice,
        toolCallName = toolCallName,
        toolResult = toolResult
    )

    companion object {
        fun fromDomain(msg: ChatMessage): ChatEntity = ChatEntity(
            id = msg.id,
            senderName = msg.sender.name,
            text = msg.text,
            timestamp = msg.timestamp,
            isVoice = msg.isVoice,
            toolCallName = msg.toolCallName,
            toolResult = msg.toolResult
        )
    }
}
