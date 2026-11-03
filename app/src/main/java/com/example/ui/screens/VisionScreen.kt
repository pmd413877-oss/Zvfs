package com.example.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.PhotoTeal
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextGrayLight
import com.example.ui.theme.TextGrayMuted
import com.example.ui.theme.TextWhite

@Composable
fun VisionScreen(
  currentBitmap: Bitmap?,
  analysisResult: String,
  isAnalyzing: Boolean,
  onImageSelected: (Bitmap) -> Unit,
  onClearImage: () -> Unit,
  onSpeak: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Camera preview launcher
  val takePictureLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap ->
    bitmap?.let { onImageSelected(it) }
  }

  // Camera permission launcher
  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      takePictureLauncher.launch(null)
    }
  }

  // Gallery photo picker launcher
  val pickMediaLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    uri?.let {
      try {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it)) { decoder, _, _ ->
            decoder.isMutableRequired = true
          }
        } else {
          @Suppress("DEPRECATION")
          MediaStore.Images.Media.getBitmap(context.contentResolver, it)
        }
        onImageSelected(bitmap)
      } catch (_: Exception) {}
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp, vertical = 10.dp)
      .verticalScroll(rememberScrollState())
      .testTag("vision_screen")
  ) {
    // Title
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Vision Intelligence",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = TextWhite
        )
        Text(
          text = "Object detection, OCR, and scene analysis",
          fontSize = 12.sp,
          color = CyanNeon
        )
      }

      Icon(
        imageVector = Icons.Default.CenterFocusWeak,
        contentDescription = null,
        tint = CyanNeon,
        modifier = Modifier.size(28.dp)
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Action buttons row (Camera + Gallery)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Button(
        onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0x990D1C38)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .weight(1f)
          .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
          .testTag("vision_camera_button")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = CyanNeon, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Take Photo", fontSize = 13.sp, color = TextWhite)
        }
      }

      Button(
        onClick = {
          pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0x9917122C)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .weight(1f)
          .border(1.dp, PurpleNeon.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
          .testTag("vision_gallery_button")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Gallery", tint = PurpleNeon, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Pick Image", fontSize = 13.sp, color = TextWhite)
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Image preview box
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(240.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(Color(0x80080E1C))
        .border(1.5.dp, Brush.linearGradient(listOf(CyanNeon.copy(alpha = 0.4f), PurpleNeon.copy(alpha = 0.4f))), RoundedCornerShape(20.dp))
        .testTag("vision_preview_box"),
      contentAlignment = Alignment.Center
    ) {
      if (currentBitmap != null) {
        Image(
          bitmap = currentBitmap.asImageBitmap(),
          contentDescription = "Captured Image",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Clear image button overlay
        IconButton(
          onClick = onClearImage,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp)
            .background(Color(0xCC04060C), RoundedCornerShape(12.dp))
            .testTag("vision_clear_button")
        ) {
          Icon(Icons.Default.Delete, contentDescription = "Remove Photo", tint = Color(0xFFFF5252))
        }
      } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.CenterFocusWeak,
            contentDescription = null,
            tint = TextGrayMuted,
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "No photo selected yet",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextGrayLight
          )
          Text(
            text = "Tap 'Take Photo' or 'Pick Image' to analyze with Hayat",
            fontSize = 11.sp,
            color = TextGrayMuted
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Analysis results card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .background(Color(0x990D172E))
        .border(1.dp, Color(0x3300F5FF), RoundedCornerShape(20.dp))
        .padding(16.dp)
        .testTag("vision_analysis_card")
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Hayat's Visual Breakdown",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = CyanNeon
            )
          }

          if (analysisResult.isNotBlank() && !isAnalyzing) {
            IconButton(
              onClick = { onSpeak(analysisResult) },
              modifier = Modifier.size(30.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Speak Breakdown", tint = CyanNeon, modifier = Modifier.size(18.dp))
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isAnalyzing) {
          Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            CircularProgressIndicator(color = CyanNeon, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "Hayat is analyzing visual elements and text...",
              fontSize = 13.sp,
              color = TextGrayLight
            )
          }
        } else if (analysisResult.isNotBlank()) {
          Text(
            text = analysisResult,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            color = TextWhite
          )
        } else {
          Text(
            text = "Snap a photo of handwritten notes, math formulas, objects, or scenery. Hayat will inspect it thoroughly and explain everything to you!",
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = TextGrayMuted
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}
