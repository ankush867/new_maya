package com.example.core.model

enum class AssistantState(val label: String) {
    IDLE("Idle"),
    WAKE_LISTENING("Listening for wake phrase..."),
    LISTENING("Listening..."),
    THINKING("Thinking..."),
    SPEAKING("Speaking..."),
    INTERRUPTED("Interrupted"),
    EXECUTING_TOOL("Executing tool..."),
    ERROR("Error"),
    OFFLINE("Offline")
}
