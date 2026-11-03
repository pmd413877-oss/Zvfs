package com.example.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.example.ui.theme.StudyMagenta
import com.example.ui.theme.TextGrayLight
import com.example.ui.theme.TextGrayMuted
import com.example.ui.theme.TextWhite

@Composable
fun StudySheet(
  onClose: () -> Unit,
  onAskStudyQuestion: (String) -> Unit,
  onSpeak: (String) -> Unit,
  isThinking: Boolean,
  lastAnswer: String,
  modifier: Modifier = Modifier
) {
  var questionText by remember { mutableStateOf("") }
  val quickTopics = remember {
    listOf(
      "Explain Quantum Entanglement simply",
      "Calculus: Chain Rule intuition",
      "Neural Networks: How Transformers work",
      "Kazi Nazrul Islam literature highlights",
      "Newton's 3 Laws of Motion with real examples"
    )
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
      .background(Color(0xF0090D1C))
      .border(1.dp, StudyMagenta.copy(alpha = 0.4f), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
      .padding(20.dp)
      .testTag("study_sheet")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.MenuBook,
          contentDescription = null,
          tint = StudyMagenta,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "HAYAT AI Tutor",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = TextWhite
        )
      }
      IconButton(onClick = onClose, modifier = Modifier.testTag("close_study_sheet")) {
        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextWhite)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Quick topic pills
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(quickTopics) { topic ->
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x66181430))
            .border(1.dp, StudyMagenta.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable {
              questionText = topic
              onAskStudyQuestion(topic)
            }
            .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
          Text(text = topic, fontSize = 11.sp, color = TextWhite)
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Input row
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = questionText,
        onValueChange = { questionText = it },
        placeholder = { Text("Ask any study question or concept...", fontSize = 13.sp, color = TextGrayMuted) },
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = TextWhite,
          unfocusedTextColor = TextWhite,
          focusedBorderColor = StudyMagenta,
          unfocusedBorderColor = Color(0x33E040FB),
          cursorColor = StudyMagenta
        ),
        modifier = Modifier.weight(1f).testTag("study_input_field"),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.width(8.dp))

      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(StudyMagenta)
          .clickable {
            if (questionText.isNotBlank()) {
              onAskStudyQuestion(questionText)
            }
          }
          .testTag("study_send_button"),
        contentAlignment = Alignment.Center
      ) {
        if (isThinking) {
          CircularProgressIndicator(color = TextWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
        } else {
          Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = TextWhite, modifier = Modifier.size(20.dp))
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Tutor Answer Area
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(170.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0x990C1224))
        .border(1.dp, Color(0x22E040FB), RoundedCornerShape(16.dp))
        .padding(14.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Hayat's Explanation",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = StudyMagenta
          )
          if (lastAnswer.isNotBlank()) {
            IconButton(
              onClick = { onSpeak(lastAnswer) },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Read Aloud",
                tint = StudyMagenta,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = if (lastAnswer.isNotBlank()) lastAnswer else "Ask me any homework problem, concept, or formula. I'll break it down step-by-step for you, Jaanu! ✨",
          fontSize = 13.sp,
          lineHeight = 19.sp,
          color = TextGrayLight
        )
      }
    }
  }
}
