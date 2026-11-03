package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextGrayLight
import com.example.ui.theme.TextGrayMuted
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  currentNickname: String,
  currentPersonality: String,
  currentLanguage: String,
  speechPitch: Float,
  speechRate: Float,
  hasApiKey: Boolean,
  customApiKey: String,
  isTestingApiKey: Boolean,
  apiKeyTestMessage: String?,
  isApiKeyTestSuccess: Boolean,
  currentVoiceName: String,
  availableVoices: List<String>,
  onUpdateSettings: (String, String, String, Float, Float) -> Unit,
  onSaveApiKey: (String) -> Unit,
  onTestApiKey: (String) -> Unit,
  onSelectVoice: (String) -> Unit,
  onTestVoice: (String) -> Unit,
  onClearHistory: () -> Unit,
  modifier: Modifier = Modifier
) {
  var nickname by remember(currentNickname) { mutableStateOf(currentNickname) }
  var personality by remember(currentPersonality) { mutableStateOf(currentPersonality) }
  var language by remember(currentLanguage) { mutableStateOf(currentLanguage) }
  var pitch by remember(speechPitch) { mutableFloatStateOf(speechPitch) }
  var rate by remember(speechRate) { mutableFloatStateOf(speechRate) }
  var apiKeyInput by remember(customApiKey) { mutableStateOf(customApiKey) }
  var isApiKeyVisible by remember { mutableStateOf(false) }
  var voiceDropdownExpanded by remember { mutableStateOf(false) }

  val personalityOptions = listOf(
    "Loving & Empathetic",
    "Intellectual Tutor",
    "Cheerful Companion",
    "Cyber Specialist"
  )

  val languageOptions = listOf(
    "Auto (English + Bengali)",
    "English",
    "Bengali (বাংলা)"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp, vertical = 10.dp)
      .verticalScroll(rememberScrollState())
      .testTag("settings_screen")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Assistant Settings",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = TextWhite
        )
        Text(
          text = "Gemini API, Human Voice, and Personality",
          fontSize = 12.sp,
          color = CyanNeon
        )
      }

      Icon(Icons.Default.Settings, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(28.dp))
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 1. GEMINI API KEY CONFIGURATION (User Requested)
    SectionCard(title = "Gemini API Key Configuration", icon = Icons.Default.Key) {
      Column {
        Text(
          text = "Enter your Google Gemini API key to activate real AI intelligence for chat, vision, voice responses, and study tutoring.",
          fontSize = 12.sp,
          lineHeight = 17.sp,
          color = TextGrayLight
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = apiKeyInput,
          onValueChange = {
            apiKeyInput = it
            onSaveApiKey(it)
          },
          placeholder = { Text("AIzaSy... (Paste your Gemini API Key)", fontSize = 12.sp, color = TextGrayMuted) },
          trailingIcon = {
            IconButton(onClick = { isApiKeyVisible = !isApiKeyVisible }) {
              Icon(
                imageVector = if (isApiKeyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Toggle Key Visibility",
                tint = CyanNeon
              )
            }
          },
          visualTransformation = if (isApiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = CyanNeon,
            unfocusedBorderColor = Color(0x3300F5FF),
            cursorColor = CyanNeon
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth().testTag("api_key_input_field"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = { onTestApiKey(apiKeyInput) },
            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).testTag("test_api_key_button")
          ) {
            if (isTestingApiKey) {
              CircularProgressIndicator(color = DeepBlack, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Testing...", color = DeepBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            } else {
              Text("Save & Test Key", color = DeepBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }

        // Test status notification banner
        if (apiKeyTestMessage != null) {
          Spacer(modifier = Modifier.height(10.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(if (isApiKeyTestSuccess) Color(0x3300E676) else Color(0x33FF5252))
              .border(1.dp, if (isApiKeyTestSuccess) Color(0xFF00E676) else Color(0xFFFF5252), RoundedCornerShape(12.dp))
              .padding(10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (isApiKeyTestSuccess) Icons.Default.CheckCircle else Icons.Default.Info,
                contentDescription = null,
                tint = if (isApiKeyTestSuccess) Color(0xFF00E676) else Color(0xFFFF5252),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = apiKeyTestMessage,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhite
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2. NATURAL HUMAN VOICE ENGINE
    SectionCard(title = "Natural Human Voice Engine", icon = Icons.Default.RecordVoiceOver) {
      Column {
        Text(
          text = "Hayat is configured with a warm, natural human female voice without robotic distortion. You can fine-tune voice pitch, speech pacing, and choose installed neural voices.",
          fontSize = 12.sp,
          lineHeight = 17.sp,
          color = TextGrayLight
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Voice Selector Dropdown
        if (availableVoices.isNotEmpty()) {
          Text(text = "Active Voice Model", fontSize = 11.sp, color = CyanNeon, fontWeight = FontWeight.SemiBold)
          Spacer(modifier = Modifier.height(4.dp))

          ExposedDropdownMenuBox(
            expanded = voiceDropdownExpanded,
            onExpandedChange = { voiceDropdownExpanded = !voiceDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = currentVoiceName,
              onValueChange = {},
              readOnly = true,
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = voiceDropdownExpanded) },
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = CyanNeon,
                unfocusedBorderColor = Color(0x3300F5FF)
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )

            ExposedDropdownMenu(
              expanded = voiceDropdownExpanded,
              onDismissRequest = { voiceDropdownExpanded = false },
              modifier = Modifier.background(Color(0xFF0D1528))
            ) {
              availableVoices.forEach { voice ->
                DropdownMenuItem(
                  text = { Text(text = voice, color = TextWhite, fontSize = 12.sp) },
                  onClick = {
                    onSelectVoice(voice)
                    voiceDropdownExpanded = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
        }

        // Pitch Slider
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Voice Tone / Pitch (${String.format("%.2f", pitch)}x)", fontSize = 12.sp, color = TextWhite)
          Text(if (pitch in 0.98f..1.08f) "Natural Human" else "Custom", fontSize = 11.sp, color = CyanNeon)
        }
        Slider(
          value = pitch,
          onValueChange = {
            pitch = it
            onUpdateSettings(nickname, personality, language, it, rate)
          },
          valueRange = 0.85f..1.35f,
          colors = SliderDefaults.colors(thumbColor = CyanNeon, activeTrackColor = CyanNeon),
          modifier = Modifier.testTag("pitch_slider")
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Rate Slider
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Speech Pacing (${String.format("%.2f", rate)}x)", fontSize = 12.sp, color = TextWhite)
          Text(if (rate in 0.95f..1.05f) "Conversational" else "Custom", fontSize = 11.sp, color = CyanNeon)
        }
        Slider(
          value = rate,
          onValueChange = {
            rate = it
            onUpdateSettings(nickname, personality, language, pitch, it)
          },
          valueRange = 0.80f..1.30f,
          colors = SliderDefaults.colors(thumbColor = PurpleNeon, activeTrackColor = PurpleNeon),
          modifier = Modifier.testTag("rate_slider")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Test Voice Button
        Button(
          onClick = {
            val sample = if (language.contains("Bengali")) {
              "হ্যাঁ জানু! আমি হায়াত, তোমার মিষ্টি বন্ধু। বলো, আমি তোমাকে কিভাবে সাহায্য করতে পারি?"
            } else {
              "Hello $nickname! I'm Hayat. My voice is natural, clear, and ready to assist you anytime."
            }
            onTestVoice(sample)
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0x3300F5FF)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .testTag("test_voice_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Preview Natural Voice", color = TextWhite, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 3. User Profile & Nickname Setting
    SectionCard(title = "User Profile & Nickname", icon = Icons.Default.Person) {
      Text(
        text = "What Hayat should call you in greetings and voice conversations:",
        fontSize = 11.sp,
        color = TextGrayMuted
      )
      Spacer(modifier = Modifier.height(8.dp))
      OutlinedTextField(
        value = nickname,
        onValueChange = {
          nickname = it
          onUpdateSettings(it, personality, language, pitch, rate)
        },
        placeholder = { Text("Jaanu") },
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = TextWhite,
          unfocusedTextColor = TextWhite,
          focusedBorderColor = CyanNeon,
          unfocusedBorderColor = Color(0x3300F5FF)
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().testTag("nickname_input"),
        singleLine = true
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 4. Personality Mode
    SectionCard(title = "AI Personality Mode", icon = Icons.Default.Tune) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        personalityOptions.forEach { opt ->
          val isSelected = personality == opt
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) Color(0x3300F5FF) else Color(0x55091122))
              .border(1.dp, if (isSelected) CyanNeon else Color(0x2200F5FF), RoundedCornerShape(12.dp))
              .clickable {
                personality = opt
                onUpdateSettings(nickname, opt, language, pitch, rate)
              }
              .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = if (isSelected) CyanNeon else TextGrayMuted,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = opt,
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) CyanNeon else TextWhite
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 5. Language Selection
    SectionCard(title = "Conversation Language", icon = Icons.Default.Translate) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        languageOptions.forEach { lang ->
          val isSelected = language == lang
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) Color(0x33B54CFF) else Color(0x55091122))
              .border(1.dp, if (isSelected) PurpleNeon else Color(0x22B54CFF), RoundedCornerShape(12.dp))
              .clickable {
                language = lang
                onUpdateSettings(nickname, personality, lang, pitch, rate)
              }
              .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = if (isSelected) PurpleNeon else TextGrayMuted,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = lang,
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) PurpleNeon else TextWhite
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 6. Reset & Data Management
    SectionCard(title = "Data & Conversation History", icon = Icons.Default.Info) {
      Button(
        onClick = onClearHistory,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FF5252)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().testTag("reset_chat_history_button")
      ) {
        Text("Clear All Chat History", color = Color(0xFFFF5252), fontWeight = FontWeight.SemiBold)
      }
    }

    Spacer(modifier = Modifier.height(28.dp))
  }
}

@Composable
private fun SectionCard(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  content: @Composable () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(Color(0x990C1428))
      .border(1.dp, Color(0x2200F5FF), RoundedCornerShape(18.dp))
      .padding(16.dp)
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
      }
      Spacer(modifier = Modifier.height(10.dp))
      content()
    }
  }
}
