package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.model.MayaRule

@Entity(tableName = "maya_rules")
data class RuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val isEnabled: Boolean,
    val category: String
) {
    fun toDomain(): MayaRule = MayaRule(
        id = id,
        title = title,
        description = description,
        isEnabled = isEnabled,
        category = category
    )

    companion object {
        fun fromDomain(rule: MayaRule): RuleEntity = RuleEntity(
            id = rule.id,
            title = rule.title,
            description = rule.description,
            isEnabled = rule.isEnabled,
            category = rule.category
        )
    }
}
