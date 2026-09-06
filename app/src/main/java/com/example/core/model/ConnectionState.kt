package com.example.core.model

enum class ConnectionState(val label: String) {
    DISCONNECTED("Disconnected"),
    CONNECTING("Connecting..."),
    CONNECTED("Live Connected"),
    RECONNECTING("Reconnecting..."),
    FAILED("Connection Failed")
}
