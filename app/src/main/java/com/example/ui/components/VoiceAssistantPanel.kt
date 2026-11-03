package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.TextGrayMuted
import com.example.ui.theme.TextWhite

@Composable
fun VoiceAssistantPanel(
  title: String,
  subtitle: String,
  isListening: Boolean,
  isSpeaking: Boolean,
  onMicClick: () -> Unit,
  onExpandClick: () -> Unit,
  onSpeakerClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "MicGlow")
  val micScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isListening) 1.18f else 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isListening) 500 else 1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "micScale"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(22.dp))
      .background(Color(0xB30C1528))
      .border(1.dp, Color(0x3300F5FF), RoundedCornerShape(22.dp))
      .clickable { onMicClick() }
      .padding(horizontal = 14.dp, vertical = 12.dp)
      .testTag("voice_assistant_panel")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left: Speaker Icon
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(Color(0x3300F5FF))
          .clickable { onSpeakerClick() }
          .testTag("panel_speaker_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.VolumeUp,
          contentDescription = "Speaker",
          tint = CyanNeon,
          modifier = Modifier.size(19.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Center: Title & Subtitle / Waveform state
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = if (isListening) "Listening to you..." else if (isSpeaking) "Hayat is speaking..." else title,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextWhite,
          maxLines = 1
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = if (isListening) "Tap to finish speaking" else subtitle,
          fontSize = 11.sp,
          color = if (isListening) CyanNeon else TextGrayMuted
        )
      }

      // Right: Glowing Cyan Microphone Button
      Box(
        modifier = Modifier
          .size(42.dp)
          .scale(micScale)
          .clip(CircleShape)
          .background(
            brush = Brush.radialGradient(
              colors = listOf(
                if (isListening) Color(0xFFFF5252) else CyanNeon,
                if (isListening) Color(0xFFD32F2F) else Color(0xFF00ADB5)
              )
            )
          )
          .clickable { onMicClick() }
          .testTag("panel_mic_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
          contentDescription = if (isListening) "Stop Listening" else "Start Listening",
          tint = Color(0xFF04060C),
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(6.dp))

      // Expand / Collapse Chevron (Opens Transcript Sheet)
      IconButton(
        onClick = onExpandClick,
        modifier = Modifier
          .size(32.dp)
          .testTag("expand_transcript_button")
      ) {
        Icon(
          imageVector = Icons.Default.KeyboardArrowUp,
          contentDescription = "Expand Chat Log",
          tint = TextGrayMuted,
          modifier = Modifier.size(22.dp)
        )
      }
    }
  }
}
