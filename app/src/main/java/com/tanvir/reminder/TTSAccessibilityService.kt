package com.tanvir.reminder

import android.accessibilityservice.AccessibilityService
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import java.util.*

class TTSAccessibilityService : AccessibilityService(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            val eventType = when (event.eventType) {
                AccessibilityEvent.TYPE_VIEW_CLICKED -> "Clicked"
                AccessibilityEvent.TYPE_VIEW_FOCUSED -> "Focused"
                else -> "Unknown Event"
            }

            val text = event.text?.joinToString(" ") ?: ""
            if (text.isNotEmpty()) {
                speak(text)
            }
        }
    }

    override fun onInterrupt() {
        textToSpeech.stop()
    }

    private fun speak(text: String) {
        if(!textToSpeech.isSpeaking && text.isNotEmpty() && text.contains("title")) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.US
        } else {
            Log.e("TTSAccessibilityService", "TTS Initialization failed")
        }
    }

    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }
}
