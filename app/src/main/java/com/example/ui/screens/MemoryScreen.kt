package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MemoryEntity
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextGrayLight
import com.example.ui.theme.TextGrayMuted
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MemoryScreen(
  memories: List<MemoryEntity>,
  onAddMemory: (String, String, String) -> Unit,
  onDeleteMemory: (MemoryEntity) -> Unit,
  onClearAll: () -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("All") }
  var showAddDialog by remember { mutableStateOf(false) }

  val categories = listOf("All", "Preference", "Personal", "Study", "Vision")

  val filteredMemories = memories.filter { memory ->
    (selectedCategory == "All" || memory.category.equals(selectedCategory, ignoreCase = true)) &&
      (searchQuery.isBlank() || memory.title.contains(searchQuery, true) || memory.content.contains(searchQuery, true))
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp, vertical = 10.dp)
      .testTag("memory_screen")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Memory Core",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = TextWhite
        )
        Text(
          text = "What Hayat remembers about you",
          fontSize = 12.sp,
          color = CyanNeon
        )
      }

      Row {
        IconButton(
          onClick = { showAddDialog = true },
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(CyanNeon)
            .testTag("add_memory_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Memory", tint = DeepBlack, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = onClearAll,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0x33FF5252))
            .testTag("clear_all_memories_button")
        ) {
          Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Memories", tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyanNeon) },
      placeholder = { Text("Search memories, facts, or preferences...", fontSize = 12.sp, color = TextGrayMuted) },
      colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextWhite,
        unfocusedTextColor = TextWhite,
        focusedBorderColor = CyanNeon,
        unfocusedBorderColor = Color(0x3300F5FF),
        cursorColor = CyanNeon
      ),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("memory_search_field"),
      singleLine = true
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Category chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(categories) { cat ->
        val isSelected = cat == selectedCategory
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) CyanNeon else Color(0x800E172C))
            .border(1.dp, if (isSelected) CyanNeon else Color(0x3300F5FF), RoundedCornerShape(12.dp))
            .clickable { selectedCategory = cat }
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = cat,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) DeepBlack else TextWhite
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Memories list
    if (filteredMemories.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = TextGrayMuted,
            modifier = Modifier.size(54.dp)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "No memories found",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = TextGrayLight
          )
          Text(
            text = "Tell Hayat your favorite things or tap '+' to record a memory",
            fontSize = 11.sp,
            color = TextGrayMuted
          )
        }
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.weight(1f)
      ) {
        items(filteredMemories, key = { it.id }) { memory ->
          val dateStr = remember(memory.timestamp) {
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(memory.timestamp))
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0x990C1428))
              .border(1.dp, Color(0x2200F5FF), RoundedCornerShape(16.dp))
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = PurpleNeon,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = memory.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0x33B54CFF))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(text = memory.category, fontSize = 9.sp, color = PurpleNeon)
                  }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = memory.content,
                  fontSize = 12.sp,
                  lineHeight = 17.sp,
                  color = TextGrayLight
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = "Saved on $dateStr",
                  fontSize = 10.sp,
                  color = TextGrayMuted
                )
              }

              IconButton(
                onClick = { onDeleteMemory(memory) },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Delete,
                  contentDescription = "Delete Memory",
                  tint = Color(0xFFFF5252).copy(alpha = 0.7f),
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }
  }

  // Add Memory Dialog
  if (showAddDialog) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Preference") }

    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      containerColor = Color(0xFF0C1326),
      title = { Text("Add Memory for Hayat", color = CyanNeon, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title / Topic") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextWhite,
              unfocusedTextColor = TextWhite,
              focusedBorderColor = CyanNeon
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Details for Hayat to remember") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextWhite,
              unfocusedTextColor = TextWhite,
              focusedBorderColor = CyanNeon
            ),
            modifier = Modifier.fillMaxWidth().height(100.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (title.isNotBlank() && content.isNotBlank()) {
              onAddMemory(title, content, category)
              showAddDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = CyanNeon)
        ) {
          Text("Save Memory", color = DeepBlack, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) {
          Text("Cancel", color = TextGrayLight)
        }
      }
    )
  }
}
