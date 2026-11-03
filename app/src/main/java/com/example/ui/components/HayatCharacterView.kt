package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.MoodPink
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextWhite
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HayatCharacterView(
  mood: String = "Excited",
  isSpeaking: Boolean = false,
  isListening: Boolean = false,
  isThinking: Boolean = false,
  lastSpeechText: String = "HAYAT hai!",
  onCharacterTap: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "HayatAnimation")

  // Natural breathing & floating oscillation
  val floatAnim by infiniteTransition.animateFloat(
    initialValue = -6f,
    targetValue = 6f,
    animationSpec = infiniteRepeatable(
      animation = tween(2800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "floatAnim"
  )

  // Gentle subtle breathing scale
  val breathScale by infiniteTransition.animateFloat(
    initialValue = 0.99f,
    targetValue = 1.015f,
    animationSpec = infiniteRepeatable(
      animation = tween(3400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breathScale"
  )

  // Orbital rotation
  val orbitRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(22000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "orbitRotation"
  )

  // Speaking pulse
  val speakingPulse by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isSpeaking) 1.25f else 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isSpeaking) 400 else 2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "speakingPulse"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(380.dp)
      .testTag("hayat_character_container"),
    contentAlignment = Alignment.Center
  ) {
    // 1. Soft radial energy glow behind character
    Box(
      modifier = Modifier
        .size(260.dp)
        .scale(speakingPulse)
        .blur(48.dp)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(
              if (isSpeaking) CyanNeon.copy(alpha = 0.35f)
              else if (isListening) PurpleNeon.copy(alpha = 0.40f)
              else Color(0xFF1A3868).copy(alpha = 0.45f),
              Color(0xFF0F1E40).copy(alpha = 0.15f),
              Color.Transparent
            )
          ),
          shape = CircleShape
        )
    )

    // 2. Cosmic Geometric Orbital Rings (Matches reference screenshot behind Hayat)
    Canvas(
      modifier = Modifier
        .size(310.dp)
        .testTag("orbital_rings_canvas")
    ) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val radiusX = size.width * 0.46f
      val radiusY = size.height * 0.42f

      // Outer delicate orbital ring
      drawCircle(
        color = Color(0x3300F5FF),
        radius = radiusX,
        center = center,
        style = Stroke(width = 1.2f)
      )

      // Secondary tilted elliptical orbit
      drawCircle(
        color = Color(0x22B54CFF),
        radius = radiusY * 0.85f,
        center = center,
        style = Stroke(width = 1.0f)
      )

      // Orbiting particles (cyan, purple, gold)
      val rad = Math.toRadians(orbitRotation.toDouble())
      val p1X = center.x + (radiusX * cos(rad)).toFloat()
      val p1Y = center.y + (radiusY * sin(rad)).toFloat()
      drawCircle(color = CyanNeon, radius = 3.5f, center = Offset(p1X, p1Y))

      val rad2 = Math.toRadians((orbitRotation + 140.0) % 360.0)
      val p2X = center.x + (radiusX * 0.78f * cos(rad2)).toFloat()
      val p2Y = center.y + (radiusY * 0.78f * sin(rad2)).toFloat()
      drawCircle(color = Color(0xFFFFD54F), radius = 2.8f, center = Offset(p2X, p2Y))

      val rad3 = Math.toRadians((orbitRotation + 250.0) % 360.0)
      val p3X = center.x + (radiusX * 0.9f * cos(rad3)).toFloat()
      val p3Y = center.y + (radiusY * 0.9f * sin(rad3)).toFloat()
      drawCircle(color = PurpleNeon, radius = 3.2f, center = Offset(p3X, p3Y))
    }

    // 3. The 3D AI Female Character (Full body visible from head to boots)
    Box(
      modifier = Modifier
        .fillMaxSize()
        .offset(y = floatAnim.dp)
        .scale(breathScale)
        .clickable { onCharacterTap() },
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.hayat_character),
        contentDescription = "HAYAT - 3D AI Assistant Character",
        contentScale = ContentScale.Fit,
        modifier = Modifier
          .fillMaxSize()
          .padding(top = 4.dp, bottom = 26.dp)
          .testTag("hayat_character_image")
      )
    }

    // 4. Floating Mood Indicator Badge to the right (Matches reference screenshot: "✨ Excited")
    Box(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 20.dp, top = 60.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(Color(0x990C1527))
        .border(1.dp, Color(0x3300F5FF), RoundedCornerShape(20.dp))
        .padding(horizontal = 12.dp, vertical = 6.dp)
        .testTag("mood_badge")
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = if (isSpeaking) "💬" else if (isListening) "🎙️" else "✨",
          fontSize = 12.sp,
          color = Color(0xFFFFD54F)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
          text = if (isSpeaking) "Speaking" else if (isListening) "Listening" else mood,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextWhite
        )
      }
    }

    // 5. Status Pill Below Character Feet (Matches reference: "HAYAT hai!")
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(y = (-4).dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xCC091122))
        .border(1.dp, Color(0x3300F5FF), RoundedCornerShape(16.dp))
        .padding(horizontal = 14.dp, vertical = 4.dp)
        .testTag("hayat_status_pill")
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "HAYAT",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = CyanNeon
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isSpeaking) "speaking..." else "hai!",
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = if (isSpeaking) Color(0xFFFFD54F) else TextWhite
        )
      }
    }
  }
}
