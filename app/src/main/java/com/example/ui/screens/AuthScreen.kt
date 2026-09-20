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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HighContrastCyan
import com.example.viewmodel.VisionGuardViewModel

@Composable
fun AuthScreen(
    viewModel: VisionGuardViewModel,
    onAuthSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    LaunchedEffect(isRegisterMode) {
        if (isRegisterMode) {
            viewModel.speak("Create your VisionGuard account. Enter your name, email, and password.")
        } else {
            viewModel.speak("Sign in to VisionGuard. Enter your email and password.")
        }
    }

    fun submit() {
        errorMessage = null
        if (email.isBlank()) {
            errorMessage = "Please enter an email or phone number."
            viewModel.speak("Please enter an email or phone number.")
            return
        }
        if (password.length < 6) {
            errorMessage = "Password should be at least 6 characters."
            viewModel.speak("Password should be at least 6 characters.")
            return
        }

        if (isRegisterMode) {
            val finalName = if (name.isNotBlank()) name.trim() else "VisionGuard User"
            viewModel.registerWithSupabase(finalName, email.trim(), password) { success, msg ->
                if (success) {
                    onAuthSuccess()
                } else {
                    errorMessage = msg
                }
            }
        } else {
            viewModel.loginWithSupabase(email.trim(), password) { success, msg ->
                if (success) {
                    onAuthSuccess()
                } else {
                    errorMessage = msg
                }
            }
        }
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
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Header Shield Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        if (uiState.isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "VISIONGUARD AI",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A)
            )

            Text(
                text = if (isRegisterMode) "Create a new account" else "Sign in to continue",
                fontSize = 14.sp,
                color = if (uiState.isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Auth Mode Toggle (Sign In vs Register)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (uiState.isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Button(
                        onClick = { isRegisterMode = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("tab_sign_in"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isRegisterMode) {
                                if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7)
                            } else Color.Transparent
                        ),
                        elevation = null
                    ) {
                        Text(
                            text = "SIGN IN",
                            fontWeight = FontWeight.Bold,
                            color = if (!isRegisterMode) Color.Black else if (uiState.isDarkMode) Color.White else Color(0xFF0F172A)
                        )
                    }

                    Button(
                        onClick = { isRegisterMode = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("tab_register"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRegisterMode) {
                                if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7)
                            } else Color.Transparent
                        ),
                        elevation = null
                    ) {
                        Text(
                            text = "REGISTER",
                            fontWeight = FontWeight.Bold,
                            color = if (isRegisterMode) Color.Black else if (uiState.isDarkMode) Color.White else Color(0xFF0F172A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Input Fields
            if (isRegisterMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_auth_name"),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A),
                        unfocusedTextColor = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A),
                        focusedContainerColor = if (uiState.isDarkMode) Color(0xFF131C2E) else Color.White,
                        unfocusedContainerColor = if (uiState.isDarkMode) Color(0xFF131C2E) else Color.White,
                        focusedBorderColor = if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7),
                        unfocusedBorderColor = if (uiState.isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email or Phone") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_auth_email"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A),
                    unfocusedTextColor = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A),
                    focusedContainerColor = if (uiState.isDarkMode) Color(0xFF131C2E) else Color.White,
                    unfocusedContainerColor = if (uiState.isDarkMode) Color(0xFF131C2E) else Color.White,
                    focusedBorderColor = if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7),
                    unfocusedBorderColor = if (uiState.isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7))
                },
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.semantics {
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        }
                    ) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = if (uiState.isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_auth_password"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A),
                    unfocusedTextColor = if (uiState.isDarkMode) Color.White else Color(0xFF0F172A),
                    focusedContainerColor = if (uiState.isDarkMode) Color(0xFF131C2E) else Color.White,
                    unfocusedContainerColor = if (uiState.isDarkMode) Color(0xFF131C2E) else Color.White,
                    focusedBorderColor = if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7),
                    unfocusedBorderColor = if (uiState.isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = Color(0xFFEF4444),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Action Button
            Button(
                onClick = { submit() },
                enabled = !uiState.isAuthLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("btn_auth_submit"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isDarkMode) HighContrastCyan else Color(0xFF0284C7)
                )
            ) {
                if (uiState.isAuthLoading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = if (isRegisterMode) "CREATE ACCOUNT" else "SIGN IN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
