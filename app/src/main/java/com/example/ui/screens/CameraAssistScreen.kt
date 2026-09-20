package com.example.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.camera.CameraXManager
import com.example.ui.components.AnimatedScanningFrame
import com.example.ui.theme.HighContrastCyan
import com.example.ui.theme.HighContrastRed
import com.example.ui.theme.HighContrastYellow
import com.example.viewmodel.VisionGuardViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraAssistScreen(
    viewModel: VisionGuardViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var hasCameraPermission by remember { mutableStateOf(false) }
    val cameraManager = remember { CameraXManager(context) }
    val previewView = remember { PreviewView(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) {
            cameraManager.startCamera(lifecycleOwner, previewView)
            viewModel.speak("Camera opened. Point your device forward and tap describe or obstacle scan.")
        } else {
            viewModel.speak("Camera permission denied. Camera Assist cannot function without permission.")
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Zero-permission photo picker for testing photos from gallery/downloads
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    if (bitmap != null) {
                        val outputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                        val b64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                        viewModel.analyzeScene(
                            b64,
                            prompt = "Describe all humans, people, furniture, obstacles, and surroundings in this photo for a visually impaired user.",
                            task = "describe"
                        )
                    } else {
                        viewModel.speak("Could not decode selected image.")
                    }
                } catch (e: Exception) {
                    viewModel.speak("Error loading photo: ${e.message}")
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AI Camera Assistant",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_camera_back")
                            .semantics { contentDescription = "Go back to Home" }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.repeatLast() },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_camera_repeat")
                            .semantics { contentDescription = "Repeat spoken description" }
                    ) {
                        Icon(
                            Icons.Default.Replay,
                            contentDescription = null,
                            tint = HighContrastCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Camera Viewfinder Box with Scanning Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color.Black)
                    .semantics { contentDescription = "Live camera preview viewfinder" },
                contentAlignment = Alignment.Center
            ) {
                if (hasCameraPermission) {
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier.fillMaxSize()
                    )
                    AnimatedScanningFrame(isScanning = uiState.isAnalyzing)
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Camera Permission Needed",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(containerColor = HighContrastCyan)
                        ) {
                            Text("Grant Permission", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (uiState.isAnalyzing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = HighContrastYellow,
                                modifier = Modifier.size(54.dp),
                                strokeWidth = 5.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Analyzing view with AI...",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Spoken Output Display Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI DESCRIPTION",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = HighContrastCyan
                        )
                        Row {
                            IconButton(
                                onClick = { viewModel.repeatLast() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Hearing, contentDescription = "Hear description again", tint = HighContrastYellow)
                            }
                            IconButton(
                                onClick = { viewModel.stopSpeaking() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = "Stop speech", tint = HighContrastRed)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = uiState.activeVisionResult?.conciseSpeech ?: "Tap 'Describe Scene' below to identify objects and obstacles.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            lineHeight = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Obstacle List if any detected
                    uiState.activeVisionResult?.let { result ->
                        if (result.obstacles.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Detected Spatial Obstacles:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = HighContrastYellow
                            )
                            result.obstacles.forEach { obs ->
                                Text(
                                    "• ${obs.name}: ${obs.estimatedDistance} (${obs.direction})",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        result.faceDetected?.let { faceNote ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                faceNote,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = HighContrastCyan
                            )
                        }
                    }
                }
            }

            // Test Scenarios and Photo Picker Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TESTING & INPUT MODES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = HighContrastCyan
                    )
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_pick_photo")
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = HighContrastYellow, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pick Photo", fontSize = 12.sp, color = HighContrastYellow, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.setDirectVisionResult(
                                description = "Person detected standing 1.5 meters directly ahead, facing you in blue shirt. A second person is seated at a table on your right. Pathway ahead is open and safe to proceed.",
                                detectedItems = listOf("Person standing 1.5m", "Person seated 2.5m", "Table", "Office Chair"),
                                hazards = emptyList()
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_scenario_humans"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                            Text("Humans", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.setDirectVisionResult(
                                description = "Warning: Trip hazard detected. A backpack is lying on the floor 1 meter ahead to your left. Office chair on your right. Step right to avoid obstacles.",
                                detectedItems = listOf("Backpack on floor", "Office chair", "Doorway"),
                                hazards = listOf("Backpack floor hazard (1m)")
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_scenario_obstacles"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HighContrastYellow)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = HighContrastYellow, modifier = Modifier.size(20.dp))
                            Text("Obstacles", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.setDirectVisionResult(
                                description = "Pedestrian crosswalk 3 meters ahead. Two pedestrians walking across to your right. Curb ramp aligned with your walking path. Safe to cross.",
                                detectedItems = listOf("Pedestrians crossing", "Curb ramp", "Crosswalk marking"),
                                hazards = emptyList()
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_scenario_street"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4ADE80))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(20.dp))
                            Text("Street", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Large Accessible Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primary: Describe Scene
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val b64 = cameraManager.captureImageBase64()
                                viewModel.analyzeScene(
                                    b64,
                                    prompt = "Describe all humans, people, furniture, obstacles, and surroundings in this scene concisely and accurately for a blind person.",
                                    task = "describe"
                                )
                            } catch (e: Exception) {
                                viewModel.analyzeScene("", prompt = "Describe surroundings", task = "describe")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .testTag("btn_capture_describe")
                        .semantics { contentDescription = "Describe full scene. Double tap to capture and speak." },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HighContrastCyan)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("DESCRIBE SCENE", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }

                // Obstacle Awareness Scan
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val b64 = cameraManager.captureImageBase64()
                                viewModel.analyzeScene(
                                    b64,
                                    prompt = "Check for any walking obstacles, humans, tripping hazards, or stairs ahead.",
                                    task = "obstacle_scan"
                                )
                            } catch (e: Exception) {
                                viewModel.analyzeScene("", prompt = "Check obstacles", task = "obstacle_scan")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .testTag("btn_capture_obstacles")
                        .semantics { contentDescription = "Obstacle awareness check. Double tap to scan for walking hazards." },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, HighContrastYellow)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = HighContrastYellow, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("CHECK OBSTACLES", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = HighContrastYellow)
                }

                // Optional Known Face Check (if opt-in is enabled)
                if (uiState.faceOptIn) {
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val b64 = cameraManager.captureImageBase64()
                                    viewModel.analyzeScene(b64, prompt = "Is there any known face in front of me?", task = "face_match")
                                } catch (e: Exception) {
                                    viewModel.analyzeScene("", prompt = "Face check", task = "face_match")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .testTag("btn_capture_face")
                            .semantics { contentDescription = "Check for registered faces. Double tap to scan." },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B4B)),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFA5B4FC))
                    ) {
                        Icon(Icons.Default.Face, contentDescription = null, tint = Color(0xFFA5B4FC), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("IDENTIFY KNOWN PERSON", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA5B4FC))
                    }
                }
            }

            // Safety Disclaimer
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1917)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF78350F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = HighContrastYellow, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Safety Note: Distance values are AI estimates. Do not use VisionGuard as your sole navigation aid.",
                        fontSize = 12.sp,
                        color = Color(0xFFFDE68A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
