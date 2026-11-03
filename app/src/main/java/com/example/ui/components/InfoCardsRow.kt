package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
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
import com.example.ui.theme.MoodPink
import com.example.ui.theme.TextGrayMuted
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun InfoCardsRow(
  currentMood: String = "Excited",
  modifier: Modifier = Modifier
) {
  var weatherChecked by remember { mutableStateOf(false) }

  val calendar = Calendar.getInstance()
  val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString()
  val monthDayFormat = SimpleDateFormat("MMM EEE", Locale.ENGLISH)
  val formattedDate = monthDayFormat.format(calendar.time)

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("info_cards_row"),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // CARD 1: WEATHER
    Box(
      modifier = Modifier
        .weight(1f)
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0x990D1526))
        .border(1.dp, Color(0x1A00F5FF), RoundedCornerShape(16.dp))
        .clickable { weatherChecked = !weatherChecked }
        .padding(horizontal = 10.dp, vertical = 10.dp)
        .testTag("card_weather")
    ) {
      Column {
        Text(
          text = "WEATHER",
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = TextGrayMuted,
          letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = if (weatherChecked) "28°" else "—°",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = TextWhite
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.WbSunny,
            contentDescription = "Weather",
            tint = Color(0xFFFFD54F),
            modifier = Modifier.size(11.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = if (weatherChecked) "Sunny" else "On Request",
            fontSize = 9.sp,
            color = Color(0xFFA0B0C8),
            maxLines = 1
          )
        }
      }
    }

    // CARD 2: TODAY
    Box(
      modifier = Modifier
        .weight(1f)
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0x990D1526))
        .border(1.dp, Color(0x1A00F5FF), RoundedCornerShape(16.dp))
        .padding(horizontal = 10.dp, vertical = 10.dp)
        .testTag("card_today")
    ) {
      Column {
        Text(
          text = "TODAY",
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = TextGrayMuted,
          letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = dayOfMonth,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = TextWhite
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "Date",
            tint = Color(0xFF64B5F6),
            modifier = Modifier.size(11.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = formattedDate,
            fontSize = 9.sp,
            color = Color(0xFFA0B0C8),
            maxLines = 1
          )
        }
      }
    }

    // CARD 3: MOOD
    Box(
      modifier = Modifier
        .weight(1f)
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0x990D1526))
        .border(1.dp, Color(0x1A00F5FF), RoundedCornerShape(16.dp))
        .padding(horizontal = 10.dp, vertical = 10.dp)
        .testTag("card_mood")
    ) {
      Column {
        Text(
          text = "MOOD",
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = TextGrayMuted,
          letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Icon(
          imageVector = Icons.Default.Favorite,
          contentDescription = "Mood Heart",
          tint = MoodPink,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = MoodPink.copy(alpha = 0.7f),
            modifier = Modifier.size(10.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = currentMood,
            fontSize = 9.sp,
            color = Color(0xFFA0B0C8),
            maxLines = 1
          )
        }
      }
    }
  }
}
