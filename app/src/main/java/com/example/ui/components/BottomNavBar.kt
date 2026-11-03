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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.TextGrayMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoiceOrange
import com.example.ui.theme.VoiceOrangeGlow
import com.example.ui.theme.VoiceOrangeLight
import com.example.viewmodel.ScreenTab

@Composable
fun BottomNavBar(
  currentTab: ScreenTab,
  isListening: Boolean,
  onSelectTab: (ScreenTab) -> Unit,
  onCentralVoiceClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "VoiceButtonPulsing")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isListening) 1.22f else 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isListening) 450 else 1600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  val ringPulse by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.35f,
    animationSpec = infiniteRepeatable(
      animation = tween(1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "ringPulse"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(84.dp)
      .background(Color(0xE6050914))
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
          colors = listOf(Color(0x3300F5FF), Color.Transparent)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
      )
      .testTag("bottom_nav_bar"),
    contentAlignment = Alignment.BottomCenter
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Home
      NavItem(
        label = "Home",
        icon = Icons.Default.Home,
        isSelected = currentTab == ScreenTab.HOME,
        testTag = "nav_home",
        onClick = { onSelectTab(ScreenTab.HOME) }
      )

      // 2. Vision
      NavItem(
        label = "Vision",
        icon = Icons.Default.CenterFocusWeak,
        isSelected = currentTab == ScreenTab.VISION,
        testTag = "nav_vision",
        onClick = { onSelectTab(ScreenTab.VISION) }
      )

      // 3. Spacing for central button
      Spacer(modifier = Modifier.size(56.dp))

      // 4. Memory
      NavItem(
        label = "Memory",
        icon = Icons.Default.Psychology,
        isSelected = currentTab == ScreenTab.MEMORY,
        testTag = "nav_memory",
        onClick = { onSelectTab(ScreenTab.MEMORY) }
      )

      // 5. Settings
      NavItem(
        label = "Settings",
        icon = Icons.Default.Settings,
        isSelected = currentTab == ScreenTab.SETTINGS,
        testTag = "nav_settings",
        onClick = { onSelectTab(ScreenTab.SETTINGS) }
      )
    }

    // Large Elevated Central Orange-Red Glowing Voice Button (Matches Reference Image)
    Box(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .offset(y = (-14).dp)
        .size(72.dp),
      contentAlignment = Alignment.Center
    ) {
      // Outer ambient glowing aura
      Box(
        modifier = Modifier
          .size(68.dp)
          .scale(ringPulse)
          .blur(16.dp)
          .background(
            brush = Brush.radialGradient(
              colors = listOf(
                if (isListening) Color(0xFFFF1744) else VoiceOrangeGlow,
                Color.Transparent
              )
            ),
            shape = CircleShape
          )
      )

      // Outer concentric ring
      Box(
        modifier = Modifier
          .size(64.dp)
          .scale(pulseScale)
          .border(
            width = 1.5.dp,
            brush = Brush.radialGradient(
              colors = listOf(VoiceOrangeLight, Color(0x33FF5722))
            ),
            shape = CircleShape
          )
      )

      // Main glowing spherical button
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(CircleShape)
          .background(
            brush = Brush.radialGradient(
              colors = listOf(
                VoiceOrangeLight,
                VoiceOrange,
                Color(0xFFD84315)
              )
            )
          )
          .clickable { onCentralVoiceClick() }
          .testTag("central_voice_button"),
        contentAlignment = Alignment.Center
      ) {
        // Bright white center dot (Exact detail from reference image!)
        Box(
          modifier = Modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(TextWhite)
        )
      }
    }
  }
}

@Composable
private fun NavItem(
  label: String,
  icon: ImageVector,
  isSelected: Boolean,
  testTag: String,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .clickable { onClick() }
      .padding(horizontal = 8.dp, vertical = 6.dp)
      .testTag(testTag)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = if (isSelected) CyanNeon else TextGrayMuted,
      modifier = Modifier.size(24.dp)
    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
      text = label,
      fontSize = 10.sp,
      fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
      color = if (isSelected) CyanNeon else TextGrayMuted
    )
  }
}
