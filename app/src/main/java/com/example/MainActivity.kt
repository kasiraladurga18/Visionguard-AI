package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navigation.VisionGuardRoutes
import com.example.ui.screens.AskAiScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CameraAssistScreen
import com.example.ui.screens.EmergencyScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LocationScreen
import com.example.ui.screens.OcrReaderScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.VisionGuardTheme
import com.example.viewmodel.VisionGuardViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: VisionGuardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            VisionGuardTheme(
                darkTheme = uiState.isDarkMode,
                themeMode = uiState.themeMode,
                highContrastMode = uiState.highContrast
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VisionGuardApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun VisionGuardApp(viewModel: VisionGuardViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = VisionGuardRoutes.SPLASH
    ) {
        composable(VisionGuardRoutes.SPLASH) {
            SplashScreen(
                viewModel = viewModel,
                onNavigateToOnboarding = {
                    navController.navigate(VisionGuardRoutes.ONBOARDING) {
                        popUpTo(VisionGuardRoutes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(VisionGuardRoutes.AUTH) {
                        popUpTo(VisionGuardRoutes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(VisionGuardRoutes.HOME) {
                        popUpTo(VisionGuardRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(VisionGuardRoutes.ONBOARDING) {
            OnboardingScreen(
                viewModel = viewModel,
                onNavigateToAuth = {
                    navController.navigate(VisionGuardRoutes.AUTH) {
                        popUpTo(VisionGuardRoutes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(VisionGuardRoutes.AUTH) {
            AuthScreen(
                viewModel = viewModel,
                onAuthSuccess = {
                    navController.navigate(VisionGuardRoutes.HOME) {
                        popUpTo(VisionGuardRoutes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(VisionGuardRoutes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToCamera = { navController.navigate(VisionGuardRoutes.CAMERA) },
                onNavigateToOcr = { navController.navigate(VisionGuardRoutes.OCR) },
                onNavigateToAskAi = { navController.navigate(VisionGuardRoutes.ASK_AI) },
                onNavigateToLocation = { navController.navigate(VisionGuardRoutes.LOCATION) },
                onNavigateToEmergency = { navController.navigate(VisionGuardRoutes.EMERGENCY) },
                onNavigateToHistory = { navController.navigate(VisionGuardRoutes.HISTORY) },
                onNavigateToSettings = { navController.navigate(VisionGuardRoutes.SETTINGS) }
            )
        }

        composable(VisionGuardRoutes.CAMERA) {
            CameraAssistScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(VisionGuardRoutes.OCR) {
            OcrReaderScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(VisionGuardRoutes.ASK_AI) {
            AskAiScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(VisionGuardRoutes.LOCATION) {
            LocationScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(VisionGuardRoutes.EMERGENCY) {
            EmergencyScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(VisionGuardRoutes.HISTORY) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(VisionGuardRoutes.SETTINGS) {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOnboarding = {
                    navController.navigate(VisionGuardRoutes.ONBOARDING)
                },
                onNavigateToAuth = {
                    navController.navigate(VisionGuardRoutes.AUTH) {
                        popUpTo(VisionGuardRoutes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
