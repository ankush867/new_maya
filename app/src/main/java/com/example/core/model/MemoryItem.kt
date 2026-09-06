package com.example.core.model

data class MemoryItem(
    val id: Long = 0,
    val key: String,
    val value: String,
    val category: String = "General",
    val timestamp: Long = System.currentTimeMillis(),
    val isAutoSaved: Boolean = false
)
