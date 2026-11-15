package com.partner.assistant.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TextToSpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isReady = true

                    // Try to find a male voice on the device
                    val voices = engine.voices
                    if (voices != null) {
                        // Look for a male voice in the US English locale
                        val maleVoice = voices.find {
                            it.locale == Locale.US && (it.name.contains("male", ignoreCase = true) || it.name.contains("iob", ignoreCase = true))
                        }
                        if (maleVoice != null) {
                            engine.voice = maleVoice
                        }
                    }
                    // Set pitch slightly lower for a deeper tone
                    engine.setPitch(0.9f)
                    engine.setSpeechRate(0.95f)
                }
            }
        }
    }

    fun speak(text: String) {
        if (isReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "PartnerUtterance")
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}