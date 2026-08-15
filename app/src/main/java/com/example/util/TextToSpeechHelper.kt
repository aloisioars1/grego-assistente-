package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechHelper(context: Context, onInitResult: ((Boolean) -> Unit)? = null) {
    private var tts: TextToSpeech? = null
    var isReady: Boolean = false
        private set

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val greekLocale = Locale.Builder().setLanguage("el").setRegion("GR").build()
                val result = tts?.setLanguage(greekLocale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    val fallbackLocale = Locale.Builder().setLanguage("el").build()
                    val fallbackResult = tts?.setLanguage(fallbackLocale)
                    isReady = fallbackResult != TextToSpeech.LANG_MISSING_DATA && fallbackResult != TextToSpeech.LANG_NOT_SUPPORTED
                } else {
                    isReady = true
                }
                tts?.setSpeechRate(0.88f)
                tts?.setPitch(1.0f)
            } else {
                isReady = false
            }
            onInitResult?.invoke(isReady)
        }
    }

    fun speak(text: String, stripArticle: Boolean = false) {
        if (!isReady || tts == null) {
            Log.w("TTS", "TextToSpeech not ready yet")
            return
        }
        val cleanText = if (stripArticle) {
            text.replace(Regex("^(ο|η|το|Ο|Η|ΤΟ)\\s+"), "")
        } else {
            text
        }
        tts?.stop()
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "chiste_tts_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
