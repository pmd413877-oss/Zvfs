package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CropOriginal
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MusicCyan
import com.example.ui.theme.PhotoTeal
import com.example.ui.theme.StudyMagenta
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VentYellow
import com.example.viewmodel.ActiveModal

@Composable
fun QuickActionButtons(
  onSelectAction: (ActiveModal) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("quick_action_buttons_row"),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    QuickActionButtonItem(
      label = "Music",
      icon = Icons.Default.MusicNote,
      iconColor = MusicCyan,
      borderColor = MusicCyan.copy(alpha = 0.35f),
      testTag = "action_music",
      modifier = Modifier.weight(1f),
      onClick = { onSelectAction(ActiveModal.MUSIC) }
    )

    QuickActionButtonItem(
      label = "Study",
      icon = Icons.AutoMirrored.Filled.MenuBook,
      iconColor = StudyMagenta,
      borderColor = StudyMagenta.copy(alpha = 0.35f),
      testTag = "action_study",
      modifier = Modifier.weight(1f),
      onClick = { onSelectAction(ActiveModal.STUDY) }
    )

    QuickActionButtonItem(
      label = "Vent",
      icon = Icons.Default.Forum,
      iconColor = VentYellow,
      borderColor = VentYellow.copy(alpha = 0.35f),
      testTag = "action_vent",
      modifier = Modifier.weight(1f),
      onClick = { onSelectAction(ActiveModal.VENT) }
    )

    QuickActionButtonItem(
      label = "Photo",
      icon = Icons.Default.CropOriginal,
      iconColor = PhotoTeal,
      borderColor = PhotoTeal.copy(alpha = 0.35f),
      testTag = "action_photo",
      modifier = Modifier.weight(1f),
      onClick = { onSelectAction(ActiveModal.PHOTO) }
    )
  }
}

@Composable
private fun QuickActionButtonItem(
  label: String,
  icon: ImageVector,
  iconColor: Color,
  borderColor: Color,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(18.dp))
      .background(Color(0x800D1527))
      .border(1.dp, borderColor, RoundedCornerShape(18.dp))
      .clickable { onClick() }
      .padding(vertical = 10.dp, horizontal = 4.dp)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = iconColor,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = label,
        color = TextWhite,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
