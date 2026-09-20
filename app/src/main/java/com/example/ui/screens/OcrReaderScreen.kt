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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
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
fun OcrReaderScreen(
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
            viewModel.speak("Text reader active. Point camera at document, sign, or product label.")
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Zero-permission Photo Picker for testing document images from gallery
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
                        viewModel.readText(b64, mode = "full")
                    } else {
                        viewModel.speak("Could not decode selected document image.")
                    }
                } catch (e: Exception) {
                    viewModel.speak("Error opening image: ${e.message}")
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
                        "Read Text & Documents",
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
                            .testTag("btn_ocr_back")
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
                            .testTag("btn_ocr_repeat")
                            .semantics { contentDescription = "Repeat reading aloud" }
                    ) {
                        Icon(
                            Icons.Default.Replay,
                            contentDescription = null,
                            tint = HighContrastYellow
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
            // Viewfinder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color.Black)
                    .semantics { contentDescription = "Document camera preview" },
                contentAlignment = Alignment.Center
            ) {
                if (hasCameraPermission) {
                    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
                    AnimatedScanningFrame(isScanning = uiState.isOcrProcessing)
                }

                if (uiState.isOcrProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = HighContrastYellow,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Scanning text...",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Results Card
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
                            text = "EXTRACTED TEXT",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = HighContrastYellow
                        )
                        Row {
                            IconButton(onClick = { viewModel.repeatLast() }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Hearing, contentDescription = "Listen again", tint = HighContrastCyan)
                            }
                            IconButton(onClick = { viewModel.stopSpeaking() }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Stop, contentDescription = "Stop reading", tint = HighContrastRed)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.activeOcrResult?.fullText ?: "Point camera at text and tap 'Read All' or 'Read Summary'.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            lineHeight = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Document Testing and Photo Picker Section
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
                        text = "DOCUMENT PRESETS & PICKER",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = HighContrastYellow
                    )
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_ocr_pick_photo")
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = HighContrastCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pick Image", fontSize = 12.sp, color = HighContrastCyan, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.setDirectOcrResult(
                                fullText = "Rx #492819 - Amoxicillin 500mg Capsules\nDosage: Take 1 capsule by mouth twice daily with meals.\nQuantity: 20 capsules. Refills: 0.\nWarning: Finish all medication unless directed otherwise by physician.\nDr. Robert Chang, M.D. - City Health Pharmacy",
                                summary = "Amoxicillin 500mg. Take 1 capsule twice daily with meals. 20 capsules total."
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_ocr_preset_med"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Medication, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                            Text("Medicine", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.setDirectOcrResult(
                                fullText = "SUITE 302\nVISION & LOW VISION REHABILITATION\nDR. ELIZABETH CHEN, OD\nHOURS: MONDAY - FRIDAY 8:30 AM - 5:00 PM\nPLEASE RING BELL AND ENTER",
                                summary = "Suite 302, Vision Rehabilitation Clinic. Dr. Elizabeth Chen. Open weekdays 8:30 AM to 5 PM."
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_ocr_preset_sign"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HighContrastYellow)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = HighContrastYellow, modifier = Modifier.size(20.dp))
                            Text("Door Sign", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.setDirectOcrResult(
                                fullText = "CAUTION\nWET FLOOR\nWATCH YOUR STEP\nCLEANING IN PROGRESS",
                                summary = "Caution: Wet floor sign. Cleaning in progress. Watch your step."
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_ocr_preset_warning"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                            Text("Warning", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val b64 = cameraManager.captureImageBase64()
                                viewModel.readText(b64, mode = "full")
                            } catch (e: Exception) {
                                viewModel.readText("", mode = "full")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .testTag("btn_ocr_read_all")
                        .semantics { contentDescription = "Read full text aloud. Double tap to capture." },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HighContrastYellow)
                ) {
                    Icon(Icons.Default.TextSnippet, contentDescription = null, tint = Color.Black, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("READ ALL TEXT", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val b64 = cameraManager.captureImageBase64()
                                viewModel.readText(b64, mode = "summary")
                            } catch (e: Exception) {
                                viewModel.readText("", mode = "summary")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .testTag("btn_ocr_read_summary")
                        .semantics { contentDescription = "Read concise summary. Double tap to capture." },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, HighContrastCyan)
                ) {
                    Icon(Icons.Default.Summarize, contentDescription = null, tint = HighContrastCyan, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("READ SUMMARY", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = HighContrastCyan)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
