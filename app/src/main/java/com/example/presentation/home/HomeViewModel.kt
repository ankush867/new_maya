package com.example.presentation.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.AppSettings
import com.example.core.model.AssistantState
import com.example.core.model.ConnectionState
import com.example.data.repository.AssistantRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AssistantRepository.getInstance(application)

    val assistantState: StateFlow<AssistantState> = repository.assistantState
    val connectionState: StateFlow<ConnectionState> = repository.connectionState
    val micAmplitude: StateFlow<Float> = repository.micAmplitude
    val speakerAmplitude: StateFlow<Float> = repository.speakerAmplitude
    val settings: StateFlow<AppSettings> = repository.settings
    val energyLevel: StateFlow<Int> = repository.energyLevel

    fun getGreeting(): String = repository.getGreetingText()

    fun toggleVoice() {
        repository.toggleVoiceAssistant()
    }

    fun interruptVoice() {
        repository.interruptSpeaking()
    }

    fun sendQuickPrompt(prompt: String) {
        repository.sendUserTextMessage(prompt)
    }
}
