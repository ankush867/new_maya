package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.model.MemoryItem

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String,
    val value: String,
    val category: String,
    val timestamp: Long,
    val isAutoSaved: Boolean
) {
    fun toDomain(): MemoryItem = MemoryItem(
        id = id,
        key = key,
        value = value,
        category = category,
        timestamp = timestamp,
        isAutoSaved = isAutoSaved
    )

    companion object {
        fun fromDomain(item: MemoryItem): MemoryEntity = MemoryEntity(
            id = item.id,
            key = item.key,
            value = item.value,
            category = item.category,
            timestamp = item.timestamp,
            isAutoSaved = item.isAutoSaved
        )
    }
}
