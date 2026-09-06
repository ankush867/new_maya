package com.example

import android.app.Application
import com.example.data.repository.AssistantRepository

class MayaApplication : Application() {

    lateinit var repository: AssistantRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = AssistantRepository.getInstance(this)
    }
}
