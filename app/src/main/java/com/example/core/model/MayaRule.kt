package com.example.core.model

data class MayaRule(
    val id: Long = 0,
    val title: String,
    val description: String,
    val isEnabled: Boolean = true,
    val category: String = "Behavior" // "Response", "Safety", "Execution", "Privacy"
)
