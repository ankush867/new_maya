package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.model.JournalEntry

@Entity(tableName = "journal_entries")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val mood: String,
    val timestamp: Long,
    val tagsRaw: String = ""
) {
    fun toDomain(): JournalEntry = JournalEntry(
        id = id,
        title = title,
        content = content,
        mood = mood,
        timestamp = timestamp,
        tags = if (tagsRaw.isBlank()) emptyList() else tagsRaw.split(",")
    )

    companion object {
        fun fromDomain(entry: JournalEntry): JournalEntity = JournalEntity(
            id = entry.id,
            title = entry.title,
            content = entry.content,
            mood = entry.mood,
            timestamp = entry.timestamp,
            tagsRaw = entry.tags.joinToString(",")
        )
    }
}
