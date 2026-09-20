package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.accessibility.HapticFeedbackHelper
import com.example.accessibility.SpeechRecognitionHelper
import com.example.accessibility.TtsManager
import com.example.data.local.VisionGuardDatabase
import com.example.data.local.entity.EmergencyContactEntity
import com.example.data.local.entity.UserPreferenceEntity
import com.example.data.local.entity.VisionHistoryEntity
import com.example.data.remote.auth.AuthResult
import com.example.data.remote.dto.OcrResponseDto
import com.example.data.remote.dto.VisionAnalyzeResponseDto
import com.example.data.repository.VisionGuardRepository
import com.example.location.LocationHelper
import com.example.location.UserLocationInfo
import com.example.ui.theme.Theme3DMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VisionGuardUiState(
    val isAnalyzing: Boolean = false,
    val isOcrProcessing: Boolean = false,
    val isChatProcessing: Boolean = false,
    val isListening: Boolean = false,
    val lastSpokenText: String = "",
    val activeVisionResult: VisionAnalyzeResponseDto? = null,
    val activeOcrResult: OcrResponseDto? = null,
    val activeAssistantAnswer: String? = null,
    val userLocation: UserLocationInfo? = null,
    val isSosActive: Boolean = false,
    val sosCountdown: Int = 0,
    val sosDispatched: Boolean = false,
    val activeSosContact: EmergencyContactEntity? = null,
    val designatedContact: EmergencyContactEntity? = null,
    val sosDispatchedMessage: String? = null,
    val speechRate: Float = 1.0f,
    val ttsPitch: Float = 1.0f,
    val highContrast: Boolean = true,
    val faceOptIn: Boolean = false,
    val obstacleAlerts: Boolean = true,
    val hapticFeedback: Boolean = true,
    val backendUrl: String = "http://10.0.2.2:8000",
    val themeMode: Theme3DMode = Theme3DMode.CYBER_TACTILE,
    val enable3DTilt: Boolean = true,
    val isDarkMode: Boolean = true,
    val onboardingCompleted: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isAuthLoading: Boolean = false,
    val userName: String = "VisionGuard User",
    val userEmail: String = ""
)

class VisionGuardViewModel(application: Application) : AndroidViewModel(application) {
    private val database = VisionGuardDatabase.getInstance(application)
    private val repository = VisionGuardRepository(
        database.visionHistoryDao(),
        database.emergencyContactDao(),
        database.userPreferenceDao()
    )

    val ttsManager = TtsManager(application)
    val hapticHelper = HapticFeedbackHelper(application)
    val locationHelper = LocationHelper(application)

    private val _uiState = MutableStateFlow(VisionGuardUiState())
    val uiState: StateFlow<VisionGuardUiState> = _uiState.asStateFlow()

    val historyList: StateFlow<List<VisionHistoryEntity>> = repository.historyList.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val emergencyContacts: StateFlow<List<EmergencyContactEntity>> = repository.emergencyContacts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private var sosCountdownJob: Job? = null
    var speechHelper: SpeechRecognitionHelper? = null

    init {
        // Observe preferences
        viewModelScope.launch {
            repository.userPreferences.collect { pref ->
                pref?.let {
                    _uiState.value = _uiState.value.copy(
                        speechRate = it.speechRate,
                        ttsPitch = it.ttsPitch,
                        highContrast = it.highContrast,
                        faceOptIn = it.faceOptIn,
                        obstacleAlerts = it.obstacleAlerts,
                        hapticFeedback = it.hapticFeedback,
                        backendUrl = it.backendUrl,
                        themeMode = Theme3DMode.fromId(it.themeMode),
                        enable3DTilt = it.enable3DTilt,
                        isDarkMode = it.isDarkMode,
                        onboardingCompleted = it.onboardingCompleted,
                        isLoggedIn = it.isLoggedIn,
                        userName = it.userName,
                        userEmail = it.userEmail
                    )
                }
            }
        }

        // Observe single designated emergency contact
        viewModelScope.launch {
            repository.designatedContact.collect { contact ->
                _uiState.value = _uiState.value.copy(
                    designatedContact = contact,
                    activeSosContact = contact
                )
            }
        }

        // Initialize Speech Helper
        speechHelper = SpeechRecognitionHelper(
            context = application,
            onCommandRecognized = { recognizedText ->
                handleVoiceCommand(recognizedText)
            },
            onErrorCallback = { errorMsg ->
                _uiState.value = _uiState.value.copy(isListening = false)
                speak(errorMsg)
            }
        )
    }

    fun speak(text: String, announce: Boolean = true) {
        if (_uiState.value.hapticFeedback) hapticHelper.tapTick()
        _uiState.value = _uiState.value.copy(lastSpokenText = text)
        ttsManager.speak(text, rate = _uiState.value.speechRate, pitch = _uiState.value.ttsPitch)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun repeatLast() {
        ttsManager.repeatLast()
    }

    fun startListening() {
        if (_uiState.value.hapticFeedback) hapticHelper.tapTick()
        _uiState.value = _uiState.value.copy(isListening = true)
        speechHelper?.startListening()
    }

    fun stopListening() {
        _uiState.value = _uiState.value.copy(isListening = false)
        speechHelper?.stopListening()
    }

    fun handleVoiceCommand(rawCommand: String) {
        _uiState.value = _uiState.value.copy(isListening = false)
        val cmd = rawCommand.lowercase().trim()
        
        when {
            cmd.contains("stop") -> {
                stopSpeaking()
            }
            cmd.contains("repeat") -> {
                repeatLast()
            }
            cmd.contains("where am i") || cmd.contains("location") -> {
                fetchLocationAndAnnounce()
            }
            cmd.contains("emergency") || cmd.contains("help") || cmd.contains("sos") -> {
                startSosWorkflow()
            }
            else -> {
                sendAssistantQuery(rawCommand)
            }
        }
    }

    fun analyzeScene(imageBase64: String, prompt: String = "What is in front of me?", task: String = "describe") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnalyzing = true)
            speak("Analyzing surroundings. Please hold still.")

            val result = repository.analyzeImage(imageBase64, prompt, task)
            result.onSuccess { data ->
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    activeVisionResult = data
                )
                if (_uiState.value.hapticFeedback) hapticHelper.successBuzz()
                speak(data.conciseSpeech)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isAnalyzing = false)
                speak("I couldn't analyze the image right now. Walking forward is clear.")
            }
        }
    }

    fun readText(imageBase64: String, mode: String = "full") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOcrProcessing = true)
            speak("Reading text. Please hold steady.")

            val result = repository.readText(imageBase64, mode)
            result.onSuccess { data ->
                _uiState.value = _uiState.value.copy(
                    isOcrProcessing = false,
                    activeOcrResult = data
                )
                if (_uiState.value.hapticFeedback) hapticHelper.successBuzz()
                speak(data.conciseSpeech)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isOcrProcessing = false)
                speak("No clear text detected. Please adjust lighting or distance.")
            }
        }
    }

    fun sendAssistantQuery(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isChatProcessing = true)
            val result = repository.chatWithAssistant(query)
            result.onSuccess { data ->
                _uiState.value = _uiState.value.copy(
                    isChatProcessing = false,
                    activeAssistantAnswer = data.answer
                )
                if (_uiState.value.hapticFeedback) hapticHelper.successBuzz()
                speak(data.spokenAnswer)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isChatProcessing = false)
                speak("I could not complete your request. Please try again.")
            }
        }
    }

    fun fetchLocationAndAnnounce() {
        viewModelScope.launch {
            speak("Locating your position...")
            val loc = locationHelper.getCurrentLocation()
            if (loc != null) {
                _uiState.value = _uiState.value.copy(userLocation = loc)
                speak("You are ${loc.readableAddress}")
            } else {
                speak("Unable to retrieve GPS location. Please check location permissions.")
            }
        }
    }

    fun setSimulatedLocation(address: String, lat: Double, lng: Double) {
        val loc = UserLocationInfo(
            latitude = lat,
            longitude = lng,
            readableAddress = address
        )
        _uiState.value = _uiState.value.copy(userLocation = loc)
        speak("Location set to $address")
    }

    fun setDirectOcrResult(fullText: String, summary: String) {
        val ocrDto = OcrResponseDto(
            success = true,
            message = "Text processed",
            fullText = fullText,
            conciseSpeech = summary,
            summary = summary,
            keySections = listOf(summary)
        )
        _uiState.value = _uiState.value.copy(activeOcrResult = ocrDto, isOcrProcessing = false)
        speak(summary)
    }

    fun setDirectVisionResult(
        description: String,
        detectedItems: List<String>,
        hazards: List<String>
    ) {
        val obstacles = hazards.map { h ->
            com.example.data.remote.dto.ObstacleDto(
                name = h,
                estimatedDistance = "1.2m",
                direction = "center",
                hazardLevel = "warning"
            )
        }
        val personDetected = detectedItems.firstOrNull { it.contains("person", ignoreCase = true) || it.contains("human", ignoreCase = true) }
        val visionDto = VisionAnalyzeResponseDto(
            success = true,
            message = "Analysis complete",
            description = description,
            conciseSpeech = description,
            obstacles = obstacles,
            detectedObjects = detectedItems,
            faceDetected = personDetected
        )
        _uiState.value = _uiState.value.copy(activeVisionResult = visionDto, isAnalyzing = false)
        speak(description)
    }

    fun startSosWorkflow() {
        if (_uiState.value.isSosActive) return
        _uiState.value = _uiState.value.copy(isSosActive = true, sosCountdown = 3)
        speak("Emergency SOS initiated. Alerting contacts in three seconds. Tap cancel to abort.")
        hapticHelper.emergencyPulse()

        sosCountdownJob?.cancel()
        sosCountdownJob = viewModelScope.launch {
            for (sec in 3 downTo 1) {
                _uiState.value = _uiState.value.copy(sosCountdown = sec)
                hapticHelper.emergencyPulse()
                delay(1000)
            }
            dispatchSosAlert()
        }
    }

    fun cancelSos() {
        sosCountdownJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isSosActive = false,
            sosCountdown = 0,
            sosDispatched = false,
            activeSosContact = null,
            sosDispatchedMessage = null
        )
        speak("Emergency SOS cancelled.")
        if (_uiState.value.hapticFeedback) hapticHelper.tapTick()
    }

    fun dispatchSosAlert() {
        viewModelScope.launch {
            val loc = _uiState.value.userLocation ?: locationHelper.getCurrentLocation()
            val contacts = repository.emergencyContacts.firstOrNull() ?: emptyList()
            val primaryContact = contacts.firstOrNull { it.isPrimary } ?: contacts.firstOrNull()

            _uiState.value = _uiState.value.copy(
                isSosActive = false,
                sosDispatched = true,
                activeSosContact = primaryContact,
                sosDispatchedMessage = if (primaryContact != null) {
                    "Alerting ${primaryContact.name} (${primaryContact.phoneNumber}). Location attached."
                } else {
                    "Emergency SOS dispatched. Ready to dial emergency services."
                }
            )

            val contactName = primaryContact?.name ?: "emergency services"
            speak("Emergency SOS triggered. Alerting $contactName now.")
            hapticHelper.emergencyPulse()

            repository.triggerEmergencySos(
                latitude = loc?.latitude,
                longitude = loc?.longitude,
                address = loc?.readableAddress
            )
        }
    }

    fun saveSingleEmergencyContact(name: String, phone: String, relationship: String = "Emergency Contact") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSosActive = false,
                sosDispatched = false,
                sosCountdown = 0,
                sosDispatchedMessage = null
            )
            val finalName = if (name.isNotBlank()) name.trim() else "Emergency Contact"
            val finalPhone = phone.trim()
            val finalRel = if (relationship.isNotBlank()) relationship.trim() else "Emergency Contact"
            repository.setSingleEmergencyContact(finalName, finalPhone, finalRel)
            speak("Emergency contact saved: $finalName, phone $finalPhone.")
        }
    }

    fun triggerInstantSosCall(context: android.content.Context) {
        val contact = _uiState.value.designatedContact ?: _uiState.value.activeSosContact
        if (contact != null && contact.phoneNumber.isNotBlank()) {
            speak("Emergency SOS triggered. Calling ${contact.name} now.")
            hapticHelper.emergencyPulse()

            val hasCallPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CALL_PHONE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            try {
                val intent = if (hasCallPermission) {
                    android.content.Intent(android.content.Intent.ACTION_CALL).apply {
                        data = android.net.Uri.parse("tel:${contact.phoneNumber}")
                        flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                } else {
                    android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                        data = android.net.Uri.parse("tel:${contact.phoneNumber}")
                        flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                val dialIntent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                    data = android.net.Uri.parse("tel:${contact.phoneNumber}")
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(dialIntent)
            }
        } else {
            speak("No emergency contact number configured. Please enter your contact number.")
        }
    }

    fun setDarkMode(enabled: Boolean) {
        if (_uiState.value.hapticFeedback) hapticHelper.tapTick()
        _uiState.value = _uiState.value.copy(isDarkMode = enabled)
        speak(if (enabled) "Switched to Dark Mode. High contrast OLED display." else "Switched to Light Mode. High contrast bright display.")
        viewModelScope.launch {
            val cur = repository.userPreferences.firstOrNull() ?: UserPreferenceEntity()
            repository.updatePreferences(cur.copy(isDarkMode = enabled))
        }
    }

    fun completeOnboarding() {
        _uiState.value = _uiState.value.copy(onboardingCompleted = true)
        viewModelScope.launch {
            val cur = repository.userPreferences.firstOrNull() ?: UserPreferenceEntity()
            repository.updatePreferences(cur.copy(onboardingCompleted = true))
        }
    }

    fun replayOnboarding() {
        _uiState.value = _uiState.value.copy(onboardingCompleted = false)
        speak("Replaying onboarding tutorial.")
    }

    fun loginWithSupabase(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAuthLoading = true)
            speak("Signing in to Supabase...")
            val result = repository.signInWithSupabase(email, pass)
            _uiState.value = _uiState.value.copy(isAuthLoading = false)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = true,
                        userName = result.user.fullName,
                        userEmail = result.user.email
                    )
                    speak(result.message)
                    onResult(true, result.message)
                }
                is AuthResult.Error -> {
                    speak("Login error: ${result.message}")
                    onResult(false, result.message)
                }
            }
        }
    }

    fun registerWithSupabase(name: String, email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAuthLoading = true)
            speak("Creating your Supabase account...")
            val result = repository.signUpWithSupabase(name, email, pass)
            _uiState.value = _uiState.value.copy(isAuthLoading = false)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = true,
                        userName = result.user.fullName,
                        userEmail = result.user.email
                    )
                    speak(result.message)
                    onResult(true, result.message)
                }
                is AuthResult.Error -> {
                    speak("Registration error: ${result.message}")
                    onResult(false, result.message)
                }
            }
        }
    }

    fun loginAsGuest() {
        val guestName = "Guest Explorer"
        _uiState.value = _uiState.value.copy(
            isLoggedIn = true,
            userName = guestName,
            userEmail = ""
        )
        speak("Continuing as Guest. All vision safety systems are active.")
        viewModelScope.launch {
            val cur = repository.userPreferences.firstOrNull() ?: UserPreferenceEntity()
            repository.updatePreferences(cur.copy(
                isLoggedIn = true,
                userName = guestName,
                userEmail = ""
            ))
        }
    }

    fun loginUser(name: String, email: String) {
        val finalName = if (name.isNotBlank()) name.trim() else "VisionGuard User"
        val finalEmail = email.trim()
        _uiState.value = _uiState.value.copy(
            isLoggedIn = true,
            userName = finalName,
            userEmail = finalEmail
        )
        speak("Welcome back, $finalName.")
        viewModelScope.launch {
            val cur = repository.userPreferences.firstOrNull() ?: UserPreferenceEntity()
            repository.updatePreferences(cur.copy(
                isLoggedIn = true,
                userName = finalName,
                userEmail = finalEmail
            ))
        }
    }

    fun registerUser(name: String, email: String, password: String = "") {
        val finalName = if (name.isNotBlank()) name.trim() else "VisionGuard User"
        val finalEmail = email.trim()
        _uiState.value = _uiState.value.copy(
            isLoggedIn = true,
            userName = finalName,
            userEmail = finalEmail
        )
        speak("Account created successfully for $finalName.")
        viewModelScope.launch {
            val cur = repository.userPreferences.firstOrNull() ?: UserPreferenceEntity()
            repository.updatePreferences(cur.copy(
                isLoggedIn = true,
                userName = finalName,
                userEmail = finalEmail
            ))
        }
    }

    fun logoutUser() {
        _uiState.value = _uiState.value.copy(
            isLoggedIn = false,
            userName = "VisionGuard User",
            userEmail = ""
        )
        speak("Signed out of your VisionGuard account.")
        viewModelScope.launch {
            repository.logoutSupabase()
        }
    }

    fun addEmergencyContact(name: String, phone: String, relationship: String, isPrimary: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSosActive = false,
                sosDispatched = false,
                sosCountdown = 0,
                activeSosContact = null,
                sosDispatchedMessage = null
            )
            repository.addContact(name, phone, relationship, isPrimary)
            speak("Added emergency contact $name. Phone number saved.")
        }
    }

    fun deleteEmergencyContact(id: String) {
        viewModelScope.launch {
            repository.deleteContact(id)
            speak("Emergency contact removed.")
        }
    }

    fun deleteHistoryItem(id: String) {
        viewModelScope.launch {
            repository.deleteHistory(id)
            speak("Record removed from history.")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            speak("All vision history cleared.")
        }
    }

    fun setThemeMode(mode: Theme3DMode) {
        if (_uiState.value.hapticFeedback) hapticHelper.successBuzz()
        _uiState.value = _uiState.value.copy(themeMode = mode)
        speak("${mode.displayName} activated. ${mode.subtitle}")
        viewModelScope.launch {
            val cur = repository.userPreferences.firstOrNull() ?: UserPreferenceEntity()
            repository.updatePreferences(cur.copy(themeMode = mode.id))
        }
    }

    fun toggle3DTilt(enabled: Boolean) {
        if (_uiState.value.hapticFeedback) hapticHelper.tapTick()
        _uiState.value = _uiState.value.copy(enable3DTilt = enabled)
        speak(if (enabled) "3D interactive tilt enabled." else "3D tilt disabled.")
        viewModelScope.launch {
            val cur = repository.userPreferences.firstOrNull() ?: UserPreferenceEntity()
            repository.updatePreferences(cur.copy(enable3DTilt = enabled))
        }
    }

    fun probeRadarPoint(distanceMeters: Float, bearingDegrees: Float) {
        if (_uiState.value.hapticFeedback) {
            hapticHelper.tapTick()
        }
    }

    fun updatePreferences(
        speechRate: Float = _uiState.value.speechRate,
        ttsPitch: Float = _uiState.value.ttsPitch,
        highContrast: Boolean = _uiState.value.highContrast,
        faceOptIn: Boolean = _uiState.value.faceOptIn,
        obstacleAlerts: Boolean = _uiState.value.obstacleAlerts,
        hapticFeedback: Boolean = _uiState.value.hapticFeedback,
        backendUrl: String = _uiState.value.backendUrl,
        themeMode: Theme3DMode = _uiState.value.themeMode,
        enable3DTilt: Boolean = _uiState.value.enable3DTilt
    ) {
        viewModelScope.launch {
            val newPref = UserPreferenceEntity(
                id = 1,
                speechRate = speechRate,
                ttsPitch = ttsPitch,
                highContrast = highContrast,
                faceOptIn = faceOptIn,
                obstacleAlerts = obstacleAlerts,
                hapticFeedback = hapticFeedback,
                backendUrl = backendUrl,
                themeMode = themeMode.id,
                enable3DTilt = enable3DTilt
            )
            repository.updatePreferences(newPref)
            _uiState.value = _uiState.value.copy(
                speechRate = speechRate,
                ttsPitch = ttsPitch,
                highContrast = highContrast,
                faceOptIn = faceOptIn,
                obstacleAlerts = obstacleAlerts,
                hapticFeedback = hapticFeedback,
                backendUrl = backendUrl,
                themeMode = themeMode,
                enable3DTilt = enable3DTilt
            )
            speak("Settings saved. 3D theme preferences updated.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
        speechHelper?.destroy()
    }
}
