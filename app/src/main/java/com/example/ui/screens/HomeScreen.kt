package com.example.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.Interactive3DHolographicOrb
import com.example.ui.components.Interactive3DSpatialRadar
import com.example.ui.components.Tactile3DActionButton
import com.example.ui.components.Tactile3DThemeSelector
import com.example.ui.theme.Theme3DMode
import com.example.ui.theme.getTheme3DSpec
import com.example.viewmodel.VisionGuardViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: VisionGuardViewModel,
    onNavigateToCamera: () -> Unit,
    onNavigateToOcr: () -> Unit,
    onNavigateToAskAi: () -> Unit,
    onNavigateToLocation: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val themeSpec = getTheme3DSpec(uiState.themeMode, uiState.isDarkMode)

    val speechDialogLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenMatches?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.handleVoiceCommand(spokenText)
            }
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak command to VisionGuard...")
            }
            try {
                speechDialogLauncher.launch(intent)
            } catch (_: Exception) {
                viewModel.startListening()
            }
        } else {
            viewModel.speak("Microphone permission required for voice commands.")
        }
    }

    fun triggerVoiceCommand() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            if (uiState.isListening) {
                viewModel.stopListening()
            } else {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak command to VisionGuard...")
                }
                try {
                    speechDialogLauncher.launch(intent)
                } catch (_: Exception) {
                    viewModel.startListening()
                }
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "hud_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hud_pulse_alpha"
    )

    Scaffold(
        containerColor = themeSpec.backgroundBase,
        bottomBar = {
            // Modern 3D Floating Control Dock for Audio & Mic
            Surface(
                color = themeSpec.surfaceBase,
                border = BorderStroke(1.5.dp, themeSpec.borderStroke),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Audio playback and voice controls dock" },
                tonalElevation = 12.dp,
                shadowElevation = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Replay Button (Tactile 3D Circle)
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                if (uiState.isDarkMode) {
                                    Brush.verticalGradient(
                                        listOf(themeSpec.surfaceVariant, Color.Black)
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(Color.White, themeSpec.surfaceVariant)
                                    )
                                }
                            )
                            .border(1.5.dp, themeSpec.primaryAccent.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { viewModel.repeatLast() },
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("btn_repeat_speech")
                                .semantics { contentDescription = "Repeat last spoken response" }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = null,
                                tint = themeSpec.primaryAccent,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    // Center 3D Voice Assistant Sphere Button
                    Box(contentAlignment = Alignment.Center) {
                        // Outer Glowing Ring
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(themeSpec.primaryAccent.copy(alpha = if (uiState.isListening) 0.35f else 0.15f))
                        )
                        Button(
                            onClick = { triggerVoiceCommand() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.isListening) themeSpec.secondaryAccent else themeSpec.primaryAccent
                            ),
                            shape = CircleShape,
                            modifier = Modifier
                                .size(66.dp)
                                .shadow(8.dp, CircleShape)
                                .testTag("btn_mic_listen")
                                .semantics {
                                    contentDescription = if (uiState.isListening) "Listening. Tap to stop." else "Voice command. Double tap to speak."
                                }
                        ) {
                            Icon(
                                imageVector = if (uiState.isListening) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    // Stop Speech Button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                if (uiState.isDarkMode) {
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF2A0D10), Color.Black)
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(Color(0xFFFEE2E2), Color(0xFFFECDD3))
                                    )
                                }
                            )
                            .border(1.5.dp, Color(0xFFEF4444).copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { viewModel.stopSpeaking() },
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("btn_stop_speech")
                                .semantics { contentDescription = "Stop speaking" }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(themeSpec.backgroundGradient)
                .padding(padding)
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(top = 18.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Futuristic 3D HUD Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.FiberManualRecord,
                                contentDescription = null,
                                tint = themeSpec.primaryAccent.copy(alpha = pulseAlpha),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "VISIONGUARD AI • 3D SPATIAL",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp,
                                    fontSize = 13.sp
                                ),
                                color = themeSpec.primaryAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tactile Vision System",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 23.sp
                            ),
                            color = themeSpec.textPrimary
                        )
                    }

                    // 3D Settings Button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(themeSpec.surfaceBase)
                            .border(1.5.dp, themeSpec.borderStroke, RoundedCornerShape(14.dp))
                    ) {
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("btn_settings")
                                .semantics { contentDescription = "Open Settings, 3D Theme & Voice Preferences" }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = themeSpec.primaryAccent,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }

            // 2. Interactive 3D Theme Switcher Carousel
            item {
                Tactile3DThemeSelector(
                    currentMode = uiState.themeMode,
                    isDarkMode = uiState.isDarkMode,
                    onSelectMode = { newMode ->
                        viewModel.setThemeMode(newMode)
                    }
                )
            }

            // 3. Interactive 3D Holographic AI Assistant Orb Card
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = themeSpec.surfaceBase,
                    border = BorderStroke(1.5.dp, themeSpec.borderStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Interactive3DHolographicOrb(
                            themeSpec = themeSpec,
                            isProcessing = uiState.isAnalyzing || uiState.isOcrProcessing || uiState.isChatProcessing,
                            isListening = uiState.isListening
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            val statusTitle = when {
                                uiState.isListening -> "Listening for voice..."
                                uiState.isAnalyzing -> "Scanning 3D scene..."
                                uiState.isOcrProcessing -> "Extracting text..."
                                uiState.isChatProcessing -> "Thinking..."
                                else -> "3D Assistant Ready"
                            }
                            Text(
                                text = statusTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp
                                ),
                                color = if (uiState.isListening) themeSpec.secondaryAccent else themeSpec.primaryAccent
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (uiState.lastSpokenText.isNotBlank()) {
                                    uiState.lastSpokenText.take(70) + "..."
                                } else {
                                    "Drag orb in 3D. Tap cards or speak for guidance."
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                color = themeSpec.textSecondary
                            )
                        }
                    }
                }
            }

            // 4. Interactive 3D Spatial LiDAR / Radar Visualizer
            item {
                Interactive3DSpatialRadar(
                    themeSpec = themeSpec,
                    onProbePoint = { distance, bearing ->
                        viewModel.probeRadarPoint(distance, bearing)
                    }
                )
            }

            // 5. Tactile 3D Action Cards (with physical push-down extrusion mechanics)
            item {
                Tactile3DActionButton(
                    title = "CAMERA ASSIST",
                    subtitle = "Describe surroundings & obstacle depth",
                    icon = Icons.Default.CameraAlt,
                    themeSpec = themeSpec,
                    accentColor = themeSpec.primaryAccent,
                    containerGradient = Brush.verticalGradient(
                        listOf(themeSpec.cardTopColor, themeSpec.cardBottomColor)
                    ),
                    badgeText = "3D Li-DAR",
                    testTag = "btn_camera_assist",
                    onClick = onNavigateToCamera
                )
            }

            item {
                Tactile3DActionButton(
                    title = "READ TEXT",
                    subtitle = "Optical signs, documents & labels",
                    icon = Icons.Default.TextSnippet,
                    themeSpec = themeSpec,
                    accentColor = themeSpec.secondaryAccent,
                    containerGradient = if (uiState.isDarkMode) {
                        Brush.verticalGradient(listOf(Color(0xFF221F10), Color(0xFF141208)))
                    } else {
                        themeSpec.cardGradient
                    },
                    badgeText = "OCR AI",
                    testTag = "btn_read_text",
                    onClick = onNavigateToOcr
                )
            }

            item {
                Tactile3DActionButton(
                    title = "ASK AI ASSISTANT",
                    subtitle = "Voice conversational guidance & questions",
                    icon = Icons.Default.QuestionAnswer,
                    themeSpec = themeSpec,
                    accentColor = if (uiState.isDarkMode) Color(0xFFA5B4FC) else Color(0xFF4F46E5),
                    containerGradient = if (uiState.isDarkMode) {
                        Brush.verticalGradient(listOf(Color(0xFF1C1938), Color(0xFF0F0E20)))
                    } else {
                        themeSpec.cardGradient
                    },
                    badgeText = "VOICE",
                    testTag = "btn_ask_ai",
                    onClick = onNavigateToAskAi
                )
            }

            item {
                Tactile3DActionButton(
                    title = "WHERE AM I?",
                    subtitle = "Current street, city & spatial bearing",
                    icon = Icons.Default.MyLocation,
                    themeSpec = themeSpec,
                    accentColor = if (uiState.isDarkMode) Color(0xFF34D399) else Color(0xFF059669),
                    containerGradient = if (uiState.isDarkMode) {
                        Brush.verticalGradient(listOf(Color(0xFF0D2E1F), Color(0xFF071911)))
                    } else {
                        themeSpec.cardGradient
                    },
                    badgeText = "GPS 3D",
                    testTag = "btn_where_am_i",
                    onClick = onNavigateToLocation
                )
            }

            item {
                val designated = uiState.designatedContact
                Tactile3DActionButton(
                    title = "EMERGENCY SOS",
                    subtitle = if (designated != null) "Auto-call ${designated.name} (${designated.phoneNumber})" else "1-touch automatic emergency calling",
                    icon = Icons.Default.Warning,
                    themeSpec = themeSpec,
                    accentColor = Color(0xFFFF2A4A),
                    containerGradient = if (uiState.isDarkMode) {
                        Brush.verticalGradient(listOf(Color(0xFF3B0B11), Color(0xFF200508)))
                    } else {
                        Brush.verticalGradient(listOf(Color(0xFFFFF1F2), Color(0xFFFFE4E6)))
                    },
                    badgeText = "AUTO-CALL",
                    testTag = "btn_emergency_sos",
                    onClick = onNavigateToEmergency
                )
            }

            item {
                Tactile3DActionButton(
                    title = "VISION HISTORY",
                    subtitle = "Past scene inspections & audio logs",
                    icon = Icons.Default.History,
                    themeSpec = themeSpec,
                    accentColor = if (uiState.isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                    containerGradient = if (uiState.isDarkMode) {
                        Brush.verticalGradient(listOf(Color(0xFF151D2A), Color(0xFF0A0F16)))
                    } else {
                        themeSpec.cardGradient
                    },
                    testTag = "btn_history",
                    onClick = onNavigateToHistory
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
