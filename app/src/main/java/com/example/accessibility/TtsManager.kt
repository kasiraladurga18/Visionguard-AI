package com.example.accessibility

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private val TAG = "VisionGuard.TTS"
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var lastSpokenText: String = ""

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w(TAG, "Default locale not fully supported, falling back to default")
            }
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
            isInitialized = true
            // Friendly accessible startup greeting
            speak("VisionGuard AI ready. How can I assist you?")
        } else {
            Log.e(TAG, "TextToSpeech initialization failed")
        }
    }

    fun speak(text: String, flush: Boolean = true, rate: Float = 1.0f, pitch: Float = 1.0f) {
        if (!isInitialized || text.isBlank()) return
        lastSpokenText = text
        tts?.setSpeechRate(rate)
        tts?.setPitch(pitch)
        val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts?.speak(text, queueMode, null, "utterance_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun repeatLast() {
        if (lastSpokenText.isNotBlank()) {
            speak(lastSpokenText)
        } else {
            speak("Nothing to repeat.")
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
