package com.example.ui.screens

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: VisionGuardViewModel,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "splash_radar")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    LaunchedEffect(Unit) {
        viewModel.speak("Welcome to VisionGuard AI. Intelligent visual and mobility assistant.")
        delay(2200)
        if (!uiState.onboardingCompleted) {
            onNavigateToOnboarding()
        } else if (!uiState.isLoggedIn) {
            onNavigateToAuth()
        } else {
            onNavigateToHome()
        }
    }

    fun handleManualProceed() {
        if (!uiState.onboardingCompleted) {
            onNavigateToOnboarding()
        } else if (!uiState.isLoggedIn) {
            onNavigateToAuth()
        } else {
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (uiState.isDarkMode) {
                    Brush.verticalGradient(listOf(Color(0xFF070B14), Color(0xFF03070E)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0)))
                }
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Radar Icon Rings
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .semantics { contentDescription = "VisionGuard AI Animated Shield Logo" }
            ) {
                // Outer glowing pulse ring
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(pulseScale)
                        .border(
                            2.5.dp,
                            HighContrastCyan.copy(alpha = pulseAlpha),
                            CircleShape
                        )
                )

                // Mid ring
                Box(
                    modifier = Modifier
                        .size(118.dp)
                        .border(
                            2.dp,
                            if (uiState.isDarkMode) Color(0xFF1E3A5F) else Color(0xFF93C5FD),
                            CircleShape
                        )
                )

                // Inner core
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    HighContrastCyan,
                                    if (uiState.isDarkMode) Color(0xFF0F172A) else Color(0xFF1D4ED8)
                                )
                            ),
                            CircleShape
                        )
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "VISIONGUARD AI",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.5.sp,
                color = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tactile Vision & Mobility Assistant",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Spatial Neural Guidance • One-Touch SOS • Optical OCR",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (uiState.isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Quick bypass button for accessibility
            Button(
                onClick = { handleManualProceed() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("btn_splash_proceed")
                    .semantics { contentDescription = "Enter VisionGuard AI Now" },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isDarkMode) HighContrastYellow else Color(0xFF0284C7)
                ),
                border = BorderStroke(1.5.dp, Color.White)
            ) {
                Text(
                    text = "ENTER VISIONGUARD",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}
