package com.example.presentation.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.AssistantState
import com.example.core.model.ChatMessage
import com.example.core.model.MessageSender
import com.example.data.repository.AssistantRepository
import com.example.presentation.components.GlassCard
import com.example.ui.theme.MayaCardBorder
import com.example.ui.theme.MayaCardSurface
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaTextMuted
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AssistantRepository.getInstance(context) }

    val chatMessages by repository.chatHistory.collectAsState(initial = emptyList())
    val assistantState by repository.assistantState.collectAsState()
    val micAmplitude by repository.micAmplitude.collectAsState()
    val speakerAmplitude by repository.speakerAmplitude.collectAsState()
    val settings by repository.settings.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .padding(bottom = 76.dp)
            .testTag("chat_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${settings.assistantName} Live Chat",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
                Text(
                    text = "${settings.personalityStyle} • ${assistantState.label}",
                    fontSize = 12.sp,
                    color = MayaCyanNeon
                )
            }

            if (chatMessages.isNotEmpty()) {
                IconButton(
                    onClick = { scope.launch { repository.clearChatHistory() } },
                    modifier = Modifier.testTag("clear_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear Chat",
                        tint = MayaTextMuted
                    )
                }
            }
        }

        // Messages List
        if (chatMessages.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Voice Assistant Ready",
                        tint = MayaCyanNeon.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Maya is ready to talk",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Speak naturally, ask to launch apps, send WhatsApp messages, or check the weather.",
                        fontSize = 12.sp,
                        color = MayaTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(chatMessages, key = { it.id }) { msg ->
                    ChatBubble(message = msg)
                }
            }
        }

        // Audio Activity Waveform Banner when active
        if (assistantState == AssistantState.LISTENING || assistantState == AssistantState.SPEAKING || assistantState == AssistantState.THINKING) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = MayaCardSurface,
                border = BorderStroke(1.dp, MayaCyanNeon.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Audio Activity",
                        tint = MayaCyanNeon,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = when (assistantState) {
                            AssistantState.LISTENING -> "Listening to speech..."
                            AssistantState.SPEAKING -> "Maya is speaking (tap orb or bottom mic to interrupt)"
                            AssistantState.THINKING -> "Gemini Live is thinking..."
                            else -> "Processing..."
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MayaTextPrimary
                    )
                }
            }
        }

        // Bottom Text/Voice Input Row
        val focusManager = LocalFocusManager.current
        val onSendCurrentText = {
            val text = textInput.trim()
            if (text.isNotBlank()) {
                repository.sendUserTextMessage(text)
                textInput = ""
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Message ${settings.assistantName} or ask a tool...", color = MayaTextMuted, fontSize = 13.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_text_input"),
                shape = RoundedCornerShape(22.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onSendCurrentText()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MayaCyanNeon,
                    unfocusedBorderColor = MayaCardBorder,
                    focusedTextColor = MayaTextPrimary,
                    unfocusedTextColor = MayaTextPrimary,
                    focusedContainerColor = MayaCardSurface,
                    unfocusedContainerColor = MayaCardSurface
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(8.dp))

            if (textInput.isNotBlank()) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    shape = CircleShape,
                    color = MayaCyanNeon
                ) {
                    IconButton(
                        onClick = { onSendCurrentText() },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message",
                            tint = Color(0xFF041424),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    shape = CircleShape,
                    color = if (assistantState == AssistantState.SPEAKING) MayaPurpleNeon else MayaCyanNeon
                ) {
                    IconButton(
                        onClick = { repository.toggleVoiceAssistant() },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("chat_voice_button")
                    ) {
                        Icon(
                            imageVector = if (assistantState == AssistantState.SPEAKING) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Toggle Voice",
                            tint = Color(0xFF041424),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(message.timestamp))

    when (message.sender) {
        MessageSender.USER -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                    color = Color(0xFF00527C),
                    border = BorderStroke(1.dp, MayaCyanNeon.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth(0.82f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = message.text,
                            color = MayaTextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$formattedTime • ${if (message.isVoice) "Voice" else "Text"}",
                            color = MayaCyanNeon.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }

        MessageSender.MAYA -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                    color = MayaCardSurface,
                    border = BorderStroke(1.dp, MayaCardBorder),
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Maya",
                                tint = MayaCyanNeon,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Maya",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaCyanNeon
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = message.text,
                            color = MayaTextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formattedTime,
                            color = MayaTextMuted,
                            fontSize = 10.sp,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }

        MessageSender.SYSTEM -> {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F1E33),
                border = BorderStroke(1.dp, MayaPurpleNeon.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Tool Executed",
                        tint = MayaPurpleNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message.text,
                        fontSize = 12.sp,
                        color = MayaPurpleNeon,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
