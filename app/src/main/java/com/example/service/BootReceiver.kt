package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.repository.AssistantRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val repository = AssistantRepository.getInstance(context)
            CoroutineScope(Dispatchers.IO).launch {
                val settings = repository.settings.value
                if (settings.startOnBoot && settings.backgroundServiceEnabled) {
                    BackgroundAudioService.startService(context)
                }
            }
        }
    }
}
