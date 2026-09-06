package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.JournalDao
import com.example.data.local.dao.MemoryDao
import com.example.data.local.dao.RuleDao
import com.example.data.local.entity.ChatEntity
import com.example.data.local.entity.JournalEntity
import com.example.data.local.entity.MemoryEntity
import com.example.data.local.entity.RuleEntity

@Database(
    entities = [
        MemoryEntity::class,
        JournalEntity::class,
        RuleEntity::class,
        ChatEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MayaDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
    abstract fun journalDao(): JournalDao
    abstract fun ruleDao(): RuleDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: MayaDatabase? = null

        fun getInstance(context: Context): MayaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MayaDatabase::class.java,
                    "maya_assistant.db"
                ).fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
