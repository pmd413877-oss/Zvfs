package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.StreakOrange
import com.example.ui.theme.TextGrayMuted
import com.example.ui.theme.TextWhite

@Composable
fun HeaderView(
  greetingText: String,
  statusText: String,
  streakCount: Int = 6,
  onMenuClick: () -> Unit = {},
  onProfileClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .testTag("header_section")
  ) {
    // Top Bar: Menu, HAYAT logo, Profile Picture
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Hamburger Menu Icon
      IconButton(
        onClick = onMenuClick,
        modifier = Modifier
          .size(44.dp)
          .testTag("menu_button")
      ) {
        Icon(
          imageVector = Icons.Default.Menu,
          contentDescription = "Open Menu",
          tint = TextWhite,
          modifier = Modifier.size(24.dp)
        )
      }

      // Center Brand: HAYAT / PERSONAL AI
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.testTag("hayat_logo_group")
      ) {
        Text(
          text = "HAYAT",
          color = CyanNeon,
          fontSize = 17.sp,
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 3.sp
        )
        Text(
          text = "PERSONAL AI",
          color = Color(0xFFA5B4CB),
          fontSize = 9.sp,
          fontWeight = FontWeight.Medium,
          letterSpacing = 1.5.sp
        )
      }

      // Right Circular Profile Picture
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .border(1.5.dp, CyanNeon.copy(alpha = 0.6f), CircleShape)
          .clickable { onProfileClick() }
          .testTag("profile_avatar_button"),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.hayat_avatar),
          contentDescription = "Hayat Profile Avatar",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Greeting Row with Flame Streak Card
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = greetingText,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = TextWhite,
          modifier = Modifier.testTag("greeting_headline")
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = statusText,
          fontSize = 12.sp,
          fontWeight = FontWeight.Normal,
          color = Color(0xFF00E5FF).copy(alpha = 0.85f),
          modifier = Modifier.testTag("status_subtitle")
        )
      }

      // Flame Streak Counter Card (Right side)
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0x9911192C))
          .border(1.dp, Color(0x33FF6D00), RoundedCornerShape(16.dp))
          .padding(horizontal = 10.dp, vertical = 6.dp)
          .testTag("streak_counter_card")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Whatshot,
            contentDescription = "Streak",
            tint = StreakOrange,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "$streakCount",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
          )
        }
      }
    }
  }
}
