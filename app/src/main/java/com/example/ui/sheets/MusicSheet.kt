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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.MusicCyan
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextGrayMuted
import com.example.ui.theme.TextWhite

data class TrackItem(val title: String, val artist: String, val duration: String)

@Composable
fun MusicSheet(
  onClose: () -> Unit,
  onHayatSay: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val tracks = remember {
    listOf(
      TrackItem("Cyber Lo-Fi Dreams", "HAYAT Soundscape", "2:45"),
      TrackItem("Tokyo Midnight Neon", "Synth Companion", "3:12"),
      TrackItem("Deep Space Serenity", "Aura Wave", "4:05"),
      TrackItem("Rainy Dhaka Evening", "Bengal Lo-Fi", "3:30"),
      TrackItem("Galactic Starlight", "Future Echo", "2:58")
    )
  }

  var selectedIndex by remember { mutableIntStateOf(0) }
  var isPlaying by remember { mutableStateOf(true) }

  val infiniteTransition = rememberInfiniteTransition(label = "MusicVisualizer")
  val bar1 by infiniteTransition.animateFloat(
    initialValue = 0.2f, targetValue = 1.0f,
    animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "bar1"
  )
  val bar2 by infiniteTransition.animateFloat(
    initialValue = 0.4f, targetValue = 0.9f,
    animationSpec = infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "bar2"
  )
  val bar3 by infiniteTransition.animateFloat(
    initialValue = 0.3f, targetValue = 1.0f,
    animationSpec = infiniteRepeatable(tween(280, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "bar3"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
      .background(Color(0xF00A0F1E))
      .border(1.dp, Color(0x3300F5FF), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
      .padding(20.dp)
      .testTag("music_sheet")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.MusicNote,
          contentDescription = null,
          tint = MusicCyan,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "HAYAT Music Lounge",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = TextWhite
        )
      }
      IconButton(onClick = onClose, modifier = Modifier.testTag("close_music_sheet")) {
        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextWhite)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Now Playing Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .background(
          brush = Brush.horizontalGradient(
            colors = listOf(Color(0xFF0F1E38), Color(0xFF1B1638))
          )
        )
        .border(1.dp, Color(0x4D00F5FF), RoundedCornerShape(18.dp))
        .padding(16.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = tracks[selectedIndex].title,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = TextWhite
            )
            Text(
              text = tracks[selectedIndex].artist,
              fontSize = 12.sp,
              color = TextGrayMuted
            )
          }

          // Live animated visualizer
          if (isPlaying) {
            Row(
              verticalAlignment = Alignment.Bottom,
              horizontalArrangement = Arrangement.spacedBy(3.dp),
              modifier = Modifier.height(24.dp)
            ) {
              Box(modifier = Modifier.width(4.dp).height((24 * bar1).dp).background(CyanNeon, RoundedCornerShape(2.dp)))
              Box(modifier = Modifier.width(4.dp).height((24 * bar2).dp).background(PurpleNeon, RoundedCornerShape(2.dp)))
              Box(modifier = Modifier.width(4.dp).height((24 * bar3).dp).background(CyanNeon, RoundedCornerShape(2.dp)))
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Playback Controls
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = {
              selectedIndex = if (selectedIndex > 0) selectedIndex - 1 else tracks.size - 1
            }
          ) {
            Icon(Icons.Default.SkipPrevious, contentDescription = "Prev", tint = TextWhite)
          }

          Spacer(modifier = Modifier.width(16.dp))

          Box(
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(MusicCyan)
              .clickable {
                isPlaying = !isPlaying
                if (isPlaying) {
                  onHayatSay("Now playing ${tracks[selectedIndex].title} for you!")
                }
              },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (isPlaying) "Pause" else "Play",
              tint = Color(0xFF04060C),
              modifier = Modifier.size(28.dp)
            )
          }

          Spacer(modifier = Modifier.width(16.dp))

          IconButton(
            onClick = {
              selectedIndex = (selectedIndex + 1) % tracks.size
            }
          ) {
            Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = TextWhite)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))
    Text(
      text = "Recommended Playlist",
      fontSize = 13.sp,
      fontWeight = FontWeight.SemiBold,
      color = Color(0xFFA0B0C8)
    )
    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth().height(180.dp)
    ) {
      itemsIndexed(tracks) { index, track ->
        val isSelected = index == selectedIndex
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0x3300F5FF) else Color(0x660B1222))
            .clickable {
              selectedIndex = index
              isPlaying = true
              onHayatSay("Playing ${track.title}!")
            }
            .padding(horizontal = 12.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isSelected) Icons.Default.GraphicEq else Icons.Default.MusicNote,
              contentDescription = null,
              tint = if (isSelected) CyanNeon else TextGrayMuted,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = track.title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) CyanNeon else TextWhite
              )
              Text(text = track.artist, fontSize = 11.sp, color = TextGrayMuted)
            }
          }
          Text(text = track.duration, fontSize = 11.sp, color = TextGrayMuted)
        }
      }
    }
  }
}
