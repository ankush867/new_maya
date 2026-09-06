package com.example.core.model

data class JournalEntry(
    val id: Long = 0,
    val title: String,
    val content: String,
    val mood: String = "Neutral", // "Happy", "Productive", "Calm", "Tired", "Creative"
    val timestamp: Long = System.currentTimeMillis(),
    val tags: List<String> = emptyList()
)
