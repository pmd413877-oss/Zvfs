package com.example

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.BottomNavBar
import com.example.ui.components.HayatCharacterView
import com.example.ui.components.HeaderView
import com.example.ui.components.InfoCardsRow
import com.example.ui.components.QuickActionButtons
import com.example.ui.components.VoiceAssistantPanel
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VisionScreen
import com.example.ui.sheets.MusicSheet
import com.example.ui.sheets.StudySheet
import com.example.ui.sheets.TranscriptSheet
import com.example.ui.sheets.VentSheet
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PurpleNeon
import com.example.viewmodel.ActiveModal
import com.example.viewmodel.HayatViewModel
import com.example.viewmodel.ScreenTab

class MainActivity : ComponentActivity() {
  private val viewModel: HayatViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        HayatAppRoot(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun HayatAppRoot(viewModel: HayatViewModel) {
  val uiState by viewModel.uiState.collectAsState()
  val chatMessages by viewModel.chatMessages.collectAsState()
  val memories by viewModel.memories.collectAsState()

  // Audio permission launcher for speech input
  val audioPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      viewModel.onVoiceToggle()
    }
  }

  // Handle system back navigation
  BackHandler(enabled = uiState.activeModal != ActiveModal.NONE || uiState.currentTab != ScreenTab.HOME) {
    if (uiState.activeModal != ActiveModal.NONE) {
      viewModel.setModal(ActiveModal.NONE)
    } else if (uiState.currentTab != ScreenTab.HOME) {
      viewModel.selectTab(ScreenTab.HOME)
    }
  }

  // Outer Screen with subtle glowing neon border line matching the reference screenshot
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            DeepNavy,
            Color(0xFF070C1B),
            DeepBlack
          )
        )
      )
      .border(
        width = 1.dp,
        brush = Brush.linearGradient(
          colors = listOf(
            CyanNeon.copy(alpha = 0.4f),
            PurpleNeon.copy(alpha = 0.3f),
            CyanNeon.copy(alpha = 0.2f)
          )
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
      )
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("app_root_container")
  ) {
    Scaffold(
      containerColor = Color.Transparent,
      bottomBar = {
        BottomNavBar(
          currentTab = uiState.currentTab,
          isListening = uiState.isListening,
          onSelectTab = { tab ->
            viewModel.setModal(ActiveModal.NONE)
            viewModel.selectTab(tab)
          },
          onCentralVoiceClick = {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
          }
        )
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        // Tab Content
        when (uiState.currentTab) {
          ScreenTab.HOME -> {
            HomeScreenContent(
              uiState = uiState,
              onMenuClick = { viewModel.setModal(ActiveModal.TRANSCRIPT) },
              onProfileClick = { viewModel.selectTab(ScreenTab.SETTINGS) },
              onCharacterTap = {
                val prompt = "Hey ${uiState.userNickname}! 😍 I'm right here with you. What would you like to explore?"
                viewModel.speakText(prompt)
              },
              onSelectAction = { modal ->
                if (modal == ActiveModal.PHOTO) {
                  viewModel.selectTab(ScreenTab.VISION)
                } else {
                  viewModel.setModal(modal)
                }
              },
              onMicClick = {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
              },
              onExpandTranscript = {
                viewModel.setModal(ActiveModal.TRANSCRIPT)
              },
              onSpeakerClick = {
                if (uiState.isSpeaking) {
                  viewModel.stopSpeech()
                } else {
                  viewModel.speakText(uiState.lastSpokenResponse)
                }
              }
            )
          }

          ScreenTab.VISION -> {
            VisionScreen(
              currentBitmap = uiState.visionBitmap,
              analysisResult = uiState.visionAnalysisText,
              isAnalyzing = uiState.isVisionAnalyzing,
              onImageSelected = { bitmap -> viewModel.setVisionImage(bitmap) },
              onClearImage = { viewModel.clearVision() },
              onSpeak = { text -> viewModel.speakText(text) }
            )
          }

          ScreenTab.MEMORY -> {
            MemoryScreen(
              memories = memories,
              onAddMemory = { title, content, cat ->
                viewModel.addManualMemory(title, content, cat)
              },
              onDeleteMemory = { memory ->
                viewModel.deleteMemory(memory)
              },
              onClearAll = {
                viewModel.clearAllMemories()
              }
            )
          }

          ScreenTab.SETTINGS -> {
            SettingsScreen(
              currentNickname = uiState.userNickname,
              currentPersonality = uiState.personalityMode,
              currentLanguage = uiState.languagePreference,
              speechPitch = uiState.speechPitch,
              speechRate = uiState.speechRate,
              hasApiKey = uiState.hasApiKey,
              customApiKey = uiState.customApiKey,
              isTestingApiKey = uiState.isTestingApiKey,
              apiKeyTestMessage = uiState.apiKeyTestMessage,
              isApiKeyTestSuccess = uiState.isApiKeyTestSuccess,
              currentVoiceName = uiState.currentVoiceName,
              availableVoices = uiState.availableVoices,
              onUpdateSettings = { nick, pers, lang, pitch, rate ->
                viewModel.updateSettings(nick, pers, lang, pitch, rate)
              },
              onSaveApiKey = { key ->
                viewModel.saveCustomApiKey(key)
              },
              onTestApiKey = { key ->
                viewModel.testCustomApiKey(key)
              },
              onSelectVoice = { voice ->
                viewModel.selectTtsVoice(voice)
              },
              onTestVoice = { sample ->
                viewModel.speakText(sample)
              },
              onClearHistory = {
                viewModel.clearChatHistory()
              }
            )
          }
        }

        // Active Modals & Sheets
        when (uiState.activeModal) {
          ActiveModal.MUSIC -> {
            MusicSheet(
              onClose = { viewModel.setModal(ActiveModal.NONE) },
              onHayatSay = { text -> viewModel.speakText(text) },
              modifier = Modifier.align(Alignment.BottomCenter)
            )
          }

          ActiveModal.STUDY -> {
            StudySheet(
              onClose = { viewModel.setModal(ActiveModal.NONE) },
              onAskStudyQuestion = { q ->
                viewModel.processUserPrompt(q, isVoice = false)
              },
              onSpeak = { text -> viewModel.speakText(text) },
              isThinking = uiState.isThinking,
              lastAnswer = uiState.lastSpokenResponse,
              modifier = Modifier.align(Alignment.BottomCenter)
            )
          }

          ActiveModal.VENT -> {
            VentSheet(
              onClose = { viewModel.setModal(ActiveModal.NONE) },
              onSendVent = { msg ->
                viewModel.processUserPrompt(msg, isVoice = false)
              },
              onSpeak = { text -> viewModel.speakText(text) },
              isThinking = uiState.isThinking,
              lastComfortResponse = uiState.lastSpokenResponse,
              userName = uiState.userNickname,
              modifier = Modifier.align(Alignment.BottomCenter)
            )
          }

          ActiveModal.TRANSCRIPT -> {
            TranscriptSheet(
              messages = chatMessages,
              isThinking = uiState.isThinking,
              onClose = { viewModel.setModal(ActiveModal.NONE) },
              onSendMessage = { text ->
                viewModel.processUserPrompt(text, isVoice = false)
              },
              onVoiceClick = {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
              },
              onReplayVoice = { text ->
                viewModel.speakText(text)
              },
              onClearChat = {
                viewModel.clearChatHistory()
              },
              modifier = Modifier.align(Alignment.BottomCenter)
            )
          }

          else -> {}
        }
      }
    }
  }
}

@Composable
fun HomeScreenContent(
  uiState: com.example.viewmodel.HayatUiState,
  onMenuClick: () -> Unit,
  onProfileClick: () -> Unit,
  onCharacterTap: () -> Unit,
  onSelectAction: (ActiveModal) -> Unit,
  onMicClick: () -> Unit,
  onExpandTranscript: () -> Unit,
  onSpeakerClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .testTag("home_screen_content")
  ) {
    // 1. Header Section
    HeaderView(
      greetingText = "Good evening, ${uiState.userNickname}😍 ✨",
      statusText = uiState.assistantStatusText,
      streakCount = uiState.streakCount,
      onMenuClick = onMenuClick,
      onProfileClick = onProfileClick
    )

    // 2. Main 3D AI Female Character View
    HayatCharacterView(
      mood = uiState.hayatMood,
      isSpeaking = uiState.isSpeaking,
      isListening = uiState.isListening,
      isThinking = uiState.isThinking,
      lastSpeechText = uiState.lastSpokenResponse,
      onCharacterTap = onCharacterTap
    )

    Spacer(modifier = Modifier.height(4.dp))

    // 3. Quick Action Buttons (Music, Study, Vent, Photo)
    QuickActionButtons(onSelectAction = onSelectAction)

    Spacer(modifier = Modifier.height(6.dp))

    // 4. Information Cards (Weather, Today, Mood)
    InfoCardsRow(currentMood = uiState.hayatMood)

    Spacer(modifier = Modifier.height(6.dp))

    // 5. Voice Assistant Control Panel (Talk to Hayat...)
    VoiceAssistantPanel(
      title = uiState.speechPromptText,
      subtitle = uiState.speechSubText,
      isListening = uiState.isListening,
      isSpeaking = uiState.isSpeaking,
      onMicClick = onMicClick,
      onExpandClick = onExpandTranscript,
      onSpeakerClick = onSpeakerClick
    )

    Spacer(modifier = Modifier.height(16.dp))
  }
}
