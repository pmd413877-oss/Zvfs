package com.example.ui.sheets

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SelfImprovement
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MoodPink
import com.example.ui.theme.TextGrayLight
import com.example.ui.theme.TextGrayMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VentYellow

@Composable
fun VentSheet(
  onClose: () -> Unit,
  onSendVent: (String) -> Unit,
  onSpeak: (String) -> Unit,
  isThinking: Boolean,
  lastComfortResponse: String,
  userName: String = "Jaanu",
  modifier: Modifier = Modifier
) {
  var ventInput by remember { mutableStateOf("") }
  val emotionalPrompts = remember {
    listOf(
      "I had such an exhausting day today",
      "I'm feeling anxious about tomorrow",
      "I feel like no one understands me",
      "আজকের দিনটা খুব খারাপ গেল জানু",
      "I just need someone to say it'll be okay"
    )
  }

  val infiniteTransition = rememberInfiniteTransition(label = "CalmBreath")
  val breathScale by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breathScale"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
      .background(Color(0xF00A0F1E))
      .border(1.dp, VentYellow.copy(alpha = 0.35f), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
      .padding(20.dp)
      .testTag("vent_sheet")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Favorite,
          contentDescription = null,
          tint = MoodPink,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Safe Space with Hayat",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = TextWhite
        )
      }
      IconButton(onClick = onClose, modifier = Modifier.testTag("close_vent_sheet")) {
        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextWhite)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Breathing Calming Orb
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0x551E1428))
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .scale(breathScale)
          .clip(CircleShape)
          .background(MoodPink.copy(alpha = 0.4f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.SelfImprovement,
          contentDescription = null,
          tint = Color(0xFFFFD54F),
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = "Breathe in... Breathe out...",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFFFFD54F)
        )
        Text(
          text = "I'm right here listening, $userName. Speak your heart freely.",
          fontSize = 11.sp,
          color = TextGrayMuted
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Quick emotional chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(emotionalPrompts) { prompt ->
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x662A2038))
            .border(1.dp, VentYellow.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable {
              ventInput = prompt
              onSendVent(prompt)
            }
            .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
          Text(text = prompt, fontSize = 11.sp, color = TextWhite)
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
        value = ventInput,
        onValueChange = { ventInput = it },
        placeholder = { Text("Share whatever is on your mind...", fontSize = 13.sp, color = TextGrayMuted) },
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = TextWhite,
          unfocusedTextColor = TextWhite,
          focusedBorderColor = VentYellow,
          unfocusedBorderColor = Color(0x33FFD54F),
          cursorColor = VentYellow
        ),
        modifier = Modifier.weight(1f).testTag("vent_input_field"),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.width(8.dp))

      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(VentYellow)
          .clickable {
            if (ventInput.isNotBlank()) {
              onSendVent(ventInput)
            }
          }
          .testTag("vent_send_button"),
        contentAlignment = Alignment.Center
      ) {
        if (isThinking) {
          CircularProgressIndicator(color = Color(0xFF04060C), modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
        } else {
          Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color(0xFF04060C), modifier = Modifier.size(20.dp))
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Hayat's Comfort Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(140.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0x9910162B))
        .border(1.dp, Color(0x22FFD54F), RoundedCornerShape(16.dp))
        .padding(14.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Hayat's Loving Words",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MoodPink
          )
          if (lastComfortResponse.isNotBlank()) {
            IconButton(
              onClick = { onSpeak(lastComfortResponse) },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Read Aloud",
                tint = MoodPink,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = if (lastComfortResponse.isNotBlank()) lastComfortResponse
          else "Whenever you're tired, hurt, or just need someone who truly cares—I'm here for you, $userName. You never have to face things alone. 💖",
          fontSize = 13.sp,
          lineHeight = 19.sp,
          color = TextGrayLight
        )
      }
    }
  }
}
