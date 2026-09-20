package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.HighContrastCyan
import com.example.ui.theme.HighContrastRed
import com.example.ui.theme.HighContrastYellow
import com.example.viewmodel.VisionGuardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    viewModel: VisionGuardViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val designatedContact = uiState.designatedContact ?: uiState.activeSosContact

    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember(designatedContact) { mutableStateOf(designatedContact?.name ?: "Primary Caregiver") }
    var editPhone by remember(designatedContact) { mutableStateOf(designatedContact?.phoneNumber ?: "911") }
    var editRel by remember(designatedContact) { mutableStateOf(designatedContact?.relationship ?: "Family / Caregiver") }

    // Direct Call Permission Launcher
    val callPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && designatedContact != null && designatedContact.phoneNumber.isNotBlank()) {
            try {
                val callIntent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:${designatedContact.phoneNumber}")
                }
                context.startActivity(callIntent)
            } catch (_: Exception) {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${designatedContact.phoneNumber}")
                }
                context.startActivity(dialIntent)
            }
        } else if (designatedContact != null) {
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${designatedContact.phoneNumber}")
            }
            context.startActivity(dialIntent)
        }
    }

    fun handleSosTouch() {
        if (designatedContact == null || designatedContact.phoneNumber.isBlank()) {
            showEditDialog = true
            viewModel.speak("Please enter your emergency contact phone number.")
            return
        }

        viewModel.speak("Emergency SOS triggered. Calling ${designatedContact.name} now.")

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            try {
                val callIntent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:${designatedContact.phoneNumber}")
                }
                context.startActivity(callIntent)
            } catch (_: Exception) {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${designatedContact.phoneNumber}")
                }
                context.startActivity(dialIntent)
            }
        } else {
            // Request permission; if not granted immediately launches dialer
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    Scaffold(
        containerColor = if (uiState.isDarkMode) Color(0xFF090D1A) else Color(0xFFF1F5F9),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Emergency SOS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_emergency_back")
                            .semantics { contentDescription = "Go back to Home" }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (uiState.isDarkMode) Color(0xFF131C2E) else Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Designated Single Emergency Contact Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = if (uiState.isDarkMode) Color(0xFF131C2E) else Color.White,
                border = BorderStroke(
                    1.5.dp,
                    if (uiState.isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        if (uiState.isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "DESIGNATED CONTACT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7)
                                )
                                Text(
                                    text = designatedContact?.name ?: "No Contact Set",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                            }
                        }

                        Button(
                            onClick = { showEditDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_edit_contact")
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "CHANGE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (uiState.isDarkMode) Color(0xFF0B111E) else Color(0xFFF8FAFC),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Emergency Phone Number",
                                fontSize = 12.sp,
                                color = if (uiState.isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                            Text(
                                text = designatedContact?.phoneNumber ?: "Not configured",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black,
                                color = if (uiState.isDarkMode) HighContrastYellow else Color(0xFFD97706)
                            )
                        }

                        Text(
                            text = designatedContact?.relationship ?: "Emergency",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (uiState.isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. GIANT ONE-TOUCH SOS BUTTON - Automatically calls the designated person!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFDC2626), Color(0xFF7F1D1D))
                        ),
                        RoundedCornerShape(28.dp)
                    )
                    .border(3.dp, Color(0xFFFCA5A5), RoundedCornerShape(28.dp))
                    .clickable { handleSosTouch() }
                    .testTag("btn_sos_touch_call")
                    .semantics {
                        contentDescription = "Touch SOS. Automatically calls designated emergency contact ${designatedContact?.name ?: ""}"
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "TOUCH TO CALL SOS NOW",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Instantly dials ${designatedContact?.name ?: "emergency number"} (${designatedContact?.phoneNumber ?: "911"})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFEF08A),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Quick Action Buttons: Send SMS with GPS & Siren Alert
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // SMS Location
                Button(
                    onClick = {
                        val contact = designatedContact
                        if (contact != null && contact.phoneNumber.isNotBlank()) {
                            val loc = uiState.userLocation
                            val locText = if (loc != null) {
                                "${loc.readableAddress} (Lat: ${loc.latitude}, Lng: ${loc.longitude})"
                            } else "current GPS position"

                            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:${contact.phoneNumber}")
                                putExtra("sms_body", "EMERGENCY SOS: I need immediate assistance. My location is: $locText")
                            }
                            context.startActivity(smsIntent)
                            viewModel.speak("Opening message with GPS coordinates to ${contact.name}.")
                        } else {
                            viewModel.speak("Please set your emergency contact number first.")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .testTag("btn_sos_send_sms"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isDarkMode) Color(0xFF1E293B) else Color.White
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7)
                    )
                ) {
                    Icon(
                        Icons.Default.Message,
                        contentDescription = null,
                        tint = if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SMS GPS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A)
                    )
                }

                // Announce Location Button
                Button(
                    onClick = {
                        viewModel.fetchLocationAndAnnounce()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .testTag("btn_sos_speak_location"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isDarkMode) Color(0xFF1E293B) else Color.White
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (uiState.isDarkMode) HighContrastYellow else Color(0xFFD97706)
                    )
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (uiState.isDarkMode) HighContrastYellow else Color(0xFFD97706),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "READ GPS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Current GPS Position Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (uiState.isDarkMode) Color(0xFF131C2E) else Color.White,
                border = BorderStroke(1.dp, if (uiState.isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "YOUR CURRENT BROADCAST LOCATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.userLocation?.readableAddress ?: "Locating via GPS...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (uiState.isDarkMode) Color(0xFFE2E8F0) else Color(0xFF1E293B)
                    )
                }
            }
        }
    }

    // Dialog: Edit the Single Designated Contact Number
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    "Set Emergency Contact Number",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Configure the single emergency contact number that will be called automatically when you touch SOS.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Contact Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_contact_name")
                    )

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_contact_phone")
                    )

                    OutlinedTextField(
                        value = editRel,
                        onValueChange = { editRel = it },
                        label = { Text("Relationship (e.g. Caregiver, Family)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_contact_rel")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editPhone.isNotBlank()) {
                            viewModel.saveSingleEmergencyContact(
                                name = editName,
                                phone = editPhone,
                                relationship = editRel
                            )
                            showEditDialog = false
                        } else {
                            viewModel.speak("Please enter a valid phone number.")
                        }
                    },
                    modifier = Modifier.testTag("btn_save_contact")
                ) {
                    Text("SAVE CONTACT")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}
