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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.local.ChatMessageEntity
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
fun TranscriptSheet(
  messages: List<ChatMessageEntity>,
  isThinking: Boolean,
  onClose: () -> Unit,
  onSendMessage: (String) -> Unit,
  onVoiceClick: () -> Unit,
  onReplayVoice: (String) -> Unit,
  onClearChat: () -> Unit,
  modifier: Modifier = Modifier
) {
  var textInput by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .height(520.dp)
      .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
      .background(Color(0xF2080D1C))
      .border(1.dp, Color(0x3300F5FF), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
      .padding(18.dp)
      .testTag("transcript_sheet")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Conversation Transcript",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = TextWhite
        )
        Text(
          text = "Real-time AI Dialogue & Voice",
          fontSize = 11.sp,
          color = CyanNeon
        )
      }

      Row {
        IconButton(onClick = onClearChat, modifier = Modifier.testTag("clear_chat_button")) {
          Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Chat", tint = TextGrayMuted)
        }
        IconButton(onClick = onClose, modifier = Modifier.testTag("close_transcript_sheet")) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = TextWhite)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Messages list
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        val isUser = msg.role == "user"
        val timeString = remember(msg.timestamp) {
          SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(msg.timestamp))
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(0.85f)
              .clip(
                RoundedCornerShape(
                  topStart = 16.dp,
                  topEnd = 16.dp,
                  bottomStart = if (isUser) 16.dp else 4.dp,
                  bottomEnd = if (isUser) 4.dp else 16.dp
                )
              )
              .background(
                if (isUser) Color(0x99172B4D) else Color(0xCC0E172C)
              )
              .border(
                1.dp,
                if (isUser) PurpleNeon.copy(alpha = 0.4f) else CyanNeon.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
              )
              .padding(12.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = if (isUser) "You" else "HAYAT",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isUser) PurpleNeon else CyanNeon
                )
                Text(text = timeString, fontSize = 9.sp, color = TextGrayMuted)
              }

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = msg.text,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = TextWhite
              )

              if (!isUser) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onReplayVoice(msg.text) }
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Replay Speech",
                    tint = CyanNeon,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(text = "Play voice", fontSize = 10.sp, color = CyanNeon)
                }
              }
            }
          }
        }
      }

      if (isThinking) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x990E172C))
                .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                  color = CyanNeon,
                  strokeWidth = 2.dp,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hayat is thinking...", fontSize = 12.sp, color = TextGrayLight)
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Input Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = textInput,
        onValueChange = { textInput = it },
        placeholder = { Text("Type in English or Bengali...", fontSize = 12.sp, color = TextGrayMuted) },
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = TextWhite,
          unfocusedTextColor = TextWhite,
          focusedBorderColor = CyanNeon,
          unfocusedBorderColor = Color(0x3300F5FF),
          cursorColor = CyanNeon
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f).testTag("transcript_input_field"),
        singleLine = true
      )

      Spacer(modifier = Modifier.width(8.dp))

      // Send Button
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(CyanNeon)
          .clickable {
            if (textInput.isNotBlank()) {
              onSendMessage(textInput)
              textInput = ""
            }
          }
          .testTag("transcript_send_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = DeepBlack, modifier = Modifier.size(20.dp))
      }

      Spacer(modifier = Modifier.width(6.dp))

      // Voice Toggle
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(Color(0x3300F5FF))
          .clickable { onVoiceClick() }
          .testTag("transcript_voice_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Mic, contentDescription = "Mic", tint = CyanNeon, modifier = Modifier.size(20.dp))
      }
    }
  }
}
