package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.Tactile3DThemeSelector
import com.example.ui.theme.Theme3DMode
import com.example.ui.theme.getTheme3DSpec
import com.example.viewmodel.VisionGuardViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: VisionGuardViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToOnboarding: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val themeSpec = getTheme3DSpec(uiState.themeMode, uiState.isDarkMode)

    var selectedThemeMode by remember(uiState.themeMode) { mutableStateOf(uiState.themeMode) }
    var enable3DTilt by remember(uiState.enable3DTilt) { mutableStateOf(uiState.enable3DTilt) }
    var speechRate by remember(uiState.speechRate) { mutableFloatStateOf(uiState.speechRate) }
    var ttsPitch by remember(uiState.ttsPitch) { mutableFloatStateOf(uiState.ttsPitch) }
    var highContrast by remember(uiState.highContrast) { mutableStateOf(uiState.highContrast) }
    var faceOptIn by remember(uiState.faceOptIn) { mutableStateOf(uiState.faceOptIn) }
    var obstacleAlerts by remember(uiState.obstacleAlerts) { mutableStateOf(uiState.obstacleAlerts) }
    var hapticFeedback by remember(uiState.hapticFeedback) { mutableStateOf(uiState.hapticFeedback) }
    var backendUrl by remember(uiState.backendUrl) { mutableStateOf(uiState.backendUrl) }

    Scaffold(
        containerColor = themeSpec.backgroundBase,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings & Display",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = themeSpec.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_settings_back")
                            .semantics { contentDescription = "Go back to Home" }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = themeSpec.primaryAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeSpec.surfaceBase
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(themeSpec.backgroundGradient)
                .padding(padding)
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 0. LIGHT MODE / DARK MODE SELECTOR
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = themeSpec.surfaceBase,
                border = BorderStroke(2.dp, themeSpec.primaryAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (uiState.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = themeSpec.primaryAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "DISPLAY THEME",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = themeSpec.primaryAccent
                                )
                                Text(
                                    text = if (uiState.isDarkMode) "Dark Mode (OLED Black)" else "Light Mode (High Contrast)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = themeSpec.textPrimary
                                )
                            }
                        }

                        Switch(
                            checked = uiState.isDarkMode,
                            onCheckedChange = { isDark ->
                                viewModel.setDarkMode(isDark)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = themeSpec.primaryAccent,
                                checkedTrackColor = themeSpec.surfaceVariant,
                                uncheckedThumbColor = Color(0xFFD97706),
                                uncheckedTrackColor = Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .testTag("switch_dark_light_mode")
                                .semantics {
                                    contentDescription = if (uiState.isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode"
                                }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (uiState.isDarkMode) {
                            "Dark Mode active: Conserves battery on OLED screens with pure black and vivid accent colors."
                        } else {
                            "Light Mode active: High daylight contrast for bright environments."
                        },
                        fontSize = 12.5.sp,
                        color = themeSpec.textSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // 0B. ACCOUNT & ONBOARDING ACTIONS
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = themeSpec.surfaceBase,
                border = BorderStroke(1.5.dp, themeSpec.borderStroke),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = themeSpec.primaryAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = uiState.userName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = themeSpec.textPrimary
                                )
                                Text(
                                    text = if (uiState.userEmail.isNotBlank()) uiState.userEmail else "VisionGuard Account",
                                    fontSize = 12.sp,
                                    color = themeSpec.textSecondary
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.logoutUser()
                                onNavigateToAuth()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_settings_logout")
                        ) {
                            Text(
                                "SIGN OUT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A)
                            )
                        }
                    }

                    // Replay Onboarding
                    Button(
                        onClick = {
                            viewModel.replayOnboarding()
                            onNavigateToOnboarding()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_settings_replay_onboarding"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeSpec.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            tint = themeSpec.primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "REPLAY ONBOARDING TUTORIAL",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeSpec.textPrimary
                        )
                    }
                }
            }

            // 1. Interactive 3D Theme Studio Section
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = themeSpec.surfaceBase,
                border = BorderStroke(1.5.dp, themeSpec.borderStroke),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Layers, contentDescription = null, tint = themeSpec.primaryAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Interactive 3D Visual Themes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = themeSpec.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Switch the tactile color system and high-contrast styling",
                        fontSize = 13.sp,
                        color = themeSpec.textSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Tactile3DThemeSelector(
                        currentMode = selectedThemeMode,
                        isDarkMode = uiState.isDarkMode,
                        onSelectMode = { mode ->
                            selectedThemeMode = mode
                            viewModel.setThemeMode(mode)
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3D Depth Physics Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "3D Tactile Push-Down Physics",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = themeSpec.textPrimary
                            )
                            Text(
                                "Physical extrusion depth & spring compression on tap",
                                fontSize = 12.5.sp,
                                color = themeSpec.textSecondary
                            )
                        }
                        Switch(
                            checked = enable3DTilt,
                            onCheckedChange = {
                                enable3DTilt = it
                                viewModel.toggle3DTilt(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = themeSpec.primaryAccent,
                                checkedTrackColor = themeSpec.surfaceVariant
                            )
                        )
                    }
                }
            }

            // 2. Speech Rate Section
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = themeSpec.surfaceBase,
                border = BorderStroke(1.5.dp, themeSpec.borderStroke),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Speech Speed: ${String.format(Locale.US, "%.2f", speechRate)}x",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = themeSpec.textPrimary
                        )
                        IconButton(
                            onClick = {
                                viewModel.ttsManager.speak(
                                    "Testing speech speed at ${String.format(Locale.US, "%.1f", speechRate)} times normal.",
                                    rate = speechRate,
                                    pitch = ttsPitch
                                )
                            },
                            modifier = Modifier.semantics { contentDescription = "Test speech speed sample" }
                        ) {
                            Icon(Icons.Default.Hearing, contentDescription = null, tint = themeSpec.primaryAccent)
                        }
                    }
                    Slider(
                        value = speechRate,
                        onValueChange = { speechRate = it },
                        valueRange = 0.5f..2.5f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = themeSpec.primaryAccent,
                            activeTrackColor = themeSpec.primaryAccent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Adjust speech speed from half to two and a half times" }
                    )
                }
            }

            // 3. Tactile & Sensory Accessibility Toggles
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = themeSpec.surfaceBase,
                border = BorderStroke(1.5.dp, themeSpec.borderStroke),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // High Contrast
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("High-Contrast Mode", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = themeSpec.textPrimary)
                            Text("Maximum contrast for low-vision readability", fontSize = 13.sp, color = themeSpec.textSecondary)
                        }
                        Switch(
                            checked = highContrast,
                            onCheckedChange = { highContrast = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = themeSpec.primaryAccent,
                                checkedTrackColor = themeSpec.surfaceVariant
                            )
                        )
                    }

                    // Obstacle Alerts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Obstacle Audio Warnings", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = themeSpec.textPrimary)
                            Text("Warn aloud when obstacles are detected ahead", fontSize = 13.sp, color = themeSpec.textSecondary)
                        }
                        Switch(
                            checked = obstacleAlerts,
                            onCheckedChange = { obstacleAlerts = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = themeSpec.primaryAccent,
                                checkedTrackColor = themeSpec.surfaceVariant
                            )
                        )
                    }

                    // Haptic Feedback
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tactile Haptic Feedback", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = themeSpec.textPrimary)
                            Text("Vibrate phone on tap, response, and SOS alerts", fontSize = 13.sp, color = themeSpec.textSecondary)
                        }
                        Switch(
                            checked = hapticFeedback,
                            onCheckedChange = { hapticFeedback = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = themeSpec.primaryAccent,
                                checkedTrackColor = themeSpec.surfaceVariant
                            )
                        )
                    }

                    // Face Opt-in
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Face Identification (Opt-In)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = themeSpec.textPrimary)
                            Text("Recognize registered family and caretakers", fontSize = 13.sp, color = themeSpec.textSecondary)
                        }
                        Switch(
                            checked = faceOptIn,
                            onCheckedChange = { faceOptIn = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = themeSpec.primaryAccent,
                                checkedTrackColor = themeSpec.surfaceVariant
                            )
                        )
                    }
                }
            }

            // 4. Backend Server Configuration
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = themeSpec.surfaceBase,
                border = BorderStroke(1.5.dp, themeSpec.borderStroke),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Backend Server URL", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = themeSpec.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("FastAPI host (use 10.0.2.2:8000 on Android Emulator)", fontSize = 13.sp, color = themeSpec.textSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = backendUrl,
                        onValueChange = { backendUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = themeSpec.textPrimary,
                            unfocusedTextColor = themeSpec.textPrimary,
                            focusedBorderColor = themeSpec.primaryAccent
                        )
                    )
                }
            }

            // 5. Save Settings Button (3D styled)
            Button(
                onClick = {
                    viewModel.updatePreferences(
                        speechRate = speechRate,
                        ttsPitch = ttsPitch,
                        highContrast = highContrast,
                        faceOptIn = faceOptIn,
                        obstacleAlerts = obstacleAlerts,
                        hapticFeedback = hapticFeedback,
                        backendUrl = backendUrl,
                        themeMode = selectedThemeMode,
                        enable3DTilt = enable3DTilt
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .testTag("btn_save_settings")
                    .semantics { contentDescription = "Save settings and apply preferences" },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeSpec.primaryAccent)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(26.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("SAVE PREFERENCES", fontWeight = FontWeight.Black, fontSize = 17.sp, color = Color.Black)
            }

            // 6. Project Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = themeSpec.surfaceVariant),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("VisionGuard AI • 3D Spatial Assist v1.1.0", fontWeight = FontWeight.Bold, color = themeSpec.primaryAccent)
                    Text("Assistive Navigation, 3D LiDAR Obstacle Probing & Multimodal Gemini AI", fontSize = 13.sp, color = themeSpec.textPrimary)
                    Text("Equipped with interactive theme modes, spatial depth telemetry & tactile physics.", fontSize = 12.sp, color = themeSpec.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
