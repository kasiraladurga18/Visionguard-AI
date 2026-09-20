package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HighContrastCyan
import com.example.ui.theme.HighContrastYellow
import com.example.viewmodel.VisionGuardViewModel

data class OnboardingStep(
    val stepNumber: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun OnboardingScreen(
    viewModel: VisionGuardViewModel,
    onNavigateToAuth: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentStepIndex by remember { mutableIntStateOf(0) }

    val steps = remember {
        listOf(
            OnboardingStep(
                stepNumber = 1,
                title = "AI Visual Assistance",
                subtitle = "Real-time spatial perception",
                description = "VisionGuard AI scans the environment using your camera to identify humans, doorways, furniture, and walking hazards, speaking aloud to guide every step safely.",
                icon = Icons.Default.Visibility,
                accentColor = HighContrastCyan
            ),
            OnboardingStep(
                stepNumber = 2,
                title = "Document & Sign Reader",
                subtitle = "Instant Optical OCR reading",
                description = "Point your camera at medicine prescription bottles, door labels, street signs, and documents. The AI reads text aloud instantly and provides clear audio summaries.",
                icon = Icons.Default.TextSnippet,
                accentColor = HighContrastYellow
            ),
            OnboardingStep(
                stepNumber = 3,
                title = "One-Touch Emergency SOS",
                subtitle = "Automatic contact calling",
                description = "Add your single designated emergency contact. Touching the red SOS button automatically dials your contact immediately and sends your GPS coordinates.",
                icon = Icons.Default.Phone,
                accentColor = Color(0xFFFF4B6E)
            ),
            OnboardingStep(
                stepNumber = 4,
                title = "Voice & High Contrast",
                subtitle = "Complete accessibility control",
                description = "Navigate hands-free with voice commands. Choose between High Contrast Dark OLED Mode or High Contrast Light Mode in Settings anytime.",
                icon = Icons.Default.Mic,
                accentColor = Color(0xFF6EE7B7)
            )
        )
    }

    val currentStep = steps[currentStepIndex]

    // Announce page title and description whenever step changes
    LaunchedEffect(currentStepIndex) {
        viewModel.speak("Step ${currentStep.stepNumber} of 4: ${currentStep.title}. ${currentStep.description}")
    }

    fun handleNext() {
        if (currentStepIndex < steps.size - 1) {
            currentStepIndex++
        } else {
            viewModel.completeOnboarding()
            onNavigateToAuth()
        }
    }

    fun handleSkip() {
        viewModel.completeOnboarding()
        onNavigateToAuth()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (uiState.isDarkMode) {
                    Brush.verticalGradient(listOf(Color(0xFF090D1A), Color(0xFF03070E)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0)))
                }
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Step Indicator & Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (uiState.isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                    border = BorderStroke(1.dp, currentStep.accentColor)
                ) {
                    Text(
                        text = "STEP ${currentStep.stepNumber} OF 4",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isDarkMode) currentStep.accentColor else Color(0xFF0F172A),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            viewModel.speak("${currentStep.title}. ${currentStep.description}")
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "Read this slide again" }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A)
                        )
                    }

                    TextButton(
                        onClick = { handleSkip() },
                        modifier = Modifier
                            .testTag("btn_onboarding_skip")
                            .semantics { contentDescription = "Skip onboarding and go to sign in" }
                    ) {
                        Text(
                            text = "SKIP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (uiState.isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569)
                        )
                    }
                }
            }

            // Middle Content: Visual Card & Explanation
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                // Central 3D Visual Bubble
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    currentStep.accentColor.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                        .border(2.5.dp, currentStep.accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = currentStep.icon,
                        contentDescription = null,
                        tint = currentStep.accentColor,
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = currentStep.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = currentStep.subtitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = currentStep.accentColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (uiState.isDarkMode) Color(0xFF131C2E) else Color.White,
                    border = BorderStroke(
                        1.5.dp,
                        if (uiState.isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currentStep.description,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 23.sp,
                        color = if (uiState.isDarkMode) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Step Dots Indicator
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.indices.forEach { index ->
                        val isSelected = index == currentStepIndex
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(if (isSelected) 28.dp else 10.dp)
                                .background(
                                    if (isSelected) currentStep.accentColor
                                    else if (uiState.isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }

            // Bottom Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStepIndex > 0) {
                    OutlinedButton(
                        onClick = { currentStepIndex-- },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("btn_onboarding_back"),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.5.dp,
                            if (uiState.isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8)
                        )
                    ) {
                        Text(
                            text = "BACK",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A)
                        )
                    }
                }

                Button(
                    onClick = { handleNext() },
                    modifier = Modifier
                        .weight(if (currentStepIndex > 0) 1.5f else 1f)
                        .height(56.dp)
                        .testTag("btn_onboarding_next"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = currentStep.accentColor
                    )
                ) {
                    Text(
                        text = if (currentStepIndex == steps.size - 1) "GET STARTED" else "NEXT",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
