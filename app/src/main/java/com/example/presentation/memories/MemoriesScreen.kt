package com.example.presentation.memories

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.MemoryItem
import com.example.data.repository.AssistantRepository
import com.example.presentation.components.GlassCard
import com.example.ui.theme.MayaCardBorder
import com.example.ui.theme.MayaCardSurface
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaPurpleNeon
import com.example.ui.theme.MayaRedNeon
import com.example.ui.theme.MayaTextMuted
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MemoriesScreen(
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AssistantRepository.getInstance(context) }

    val memories by repository.memories.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "General", "Personal", "Work", "Preferences", "Health")

    val filteredMemories = memories.filter { item ->
        val matchesCategory = (selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true))
        val matchesSearch = searchQuery.isBlank() || item.key.contains(searchQuery, ignoreCase = true) || item.value.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .padding(bottom = 80.dp)
            .testTag("memories_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Memories",
                            tint = MayaCyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Memory Vault",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MayaTextPrimary
                        )
                    }
                    Text(
                        text = "Maya actively remembers your facts, preferences & routines",
                        fontSize = 12.sp,
                        color = MayaTextSecondary
                    )
                }

                if (memories.isNotEmpty()) {
                    IconButton(
                        onClick = { scope.launch { repository.clearAllMemories() } },
                        modifier = Modifier.testTag("clear_all_memories_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear all memories",
                            tint = MayaTextMuted
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("memory_search_input"),
                placeholder = { Text("Search memories...", color = MayaTextMuted, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MayaTextSecondary
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MayaCyanNeon,
                    unfocusedBorderColor = MayaCardBorder,
                    focusedTextColor = MayaTextPrimary,
                    unfocusedTextColor = MayaTextPrimary,
                    focusedContainerColor = MayaCardSurface,
                    unfocusedContainerColor = MayaCardSurface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = cat },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MayaCyanNeon.copy(alpha = 0.2f) else MayaCardSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MayaCyanNeon else MayaCardBorder
                        )
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MayaCyanNeon else MayaTextSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Memory List
            if (filteredMemories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (memories.isEmpty()) "No memories saved yet" else "No matching memories",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MayaTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Maya automatically remembers things when you talk to her, or you can add one manually.",
                            fontSize = 12.sp,
                            color = MayaTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredMemories, key = { it.id }) { item ->
                        MemoryCard(
                            item = item,
                            onDelete = { scope.launch { repository.deleteMemory(item.id) } }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add Memory
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = MayaCyanNeon,
            contentColor = Color(0xFF041424),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
                .testTag("add_memory_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Memory")
        }
    }

    if (showAddDialog) {
        AddMemoryDialog(
            categories = categories.filter { it != "All" },
            onDismiss = { showAddDialog = false },
            onSave = { key, value, category ->
                scope.launch {
                    repository.saveMemory(key, value, category)
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun MemoryCard(
    item: MemoryItem,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(item.timestamp))

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MayaPurpleNeon.copy(alpha = 0.2f),
                        border = BorderStroke(0.5.dp, MayaPurpleNeon.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = item.category.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MayaPurpleNeon,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.key,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MayaTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.value,
                    fontSize = 13.sp,
                    color = MayaTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formattedDate,
                    fontSize = 10.sp,
                    color = MayaTextMuted
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp).testTag("delete_memory_${item.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Memory",
                    tint = MayaRedNeon.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AddMemoryDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var key by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "General") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MayaCardSurface,
        title = {
            Text("Add Memory to Maya", color = MayaTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("Topic / Key (e.g. Favorite Coffee)", color = MayaTextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("add_memory_key_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MayaCyanNeon,
                        unfocusedBorderColor = MayaCardBorder,
                        focusedTextColor = MayaTextPrimary,
                        unfocusedTextColor = MayaTextPrimary
                    )
                )

                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Detail (e.g. Oat Milk Caramel Latte)", color = MayaTextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("add_memory_value_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MayaCyanNeon,
                        unfocusedBorderColor = MayaCardBorder,
                        focusedTextColor = MayaTextPrimary,
                        unfocusedTextColor = MayaTextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (key.isNotBlank() && value.isNotBlank()) onSave(key, value, selectedCategory) },
                colors = ButtonDefaults.buttonColors(containerColor = MayaCyanNeon),
                modifier = Modifier.testTag("save_memory_button")
            ) {
                Text("Remember", color = Color(0xFF041424), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MayaTextSecondary)
            }
        }
    )
}
