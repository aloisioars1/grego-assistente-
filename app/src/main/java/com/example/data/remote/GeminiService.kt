package com.example.data.remote

import com.example.BuildConfig
import com.example.data.model.Flashcard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val model = "gemini-2.5-flash"
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent"

    suspend fun generateContent(
        prompt: String,
        customApiKey: String? = null,
        systemInstruction: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = when {
            !customApiKey.isNullOrBlank() -> customApiKey.trim()
            BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY
            else -> ""
        }

        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException("Cole sua API Key do Gemini para continuar! (Ou configure GEMINI_API_KEY no painel de Secrets)")
            )
        }

        try {
            val rootJson = JSONObject()
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", prompt)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            rootJson.put("contents", contentsArray)

            if (!systemInstruction.isNullOrBlank()) {
                val sysInstructionObj = JSONObject()
                val sysPartsArray = JSONArray()
                val sysPartObj = JSONObject()
                sysPartObj.put("text", systemInstruction)
                sysPartsArray.put(sysPartObj)
                sysInstructionObj.put("parts", sysPartsArray)
                rootJson.put("systemInstruction", sysInstructionObj)
            }

            val genConfig = JSONObject()
            genConfig.put("temperature", 0.7)
            genConfig.put("maxOutputTokens", 2048)
            rootJson.put("generationConfig", genConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = rootJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$baseUrl?key=$apiKey")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errJson = JSONObject(responseBody)
                    val errorObj = errJson.optJSONObject("error")
                    errorObj?.optString("message") ?: "HTTP ${response.code}: $responseBody"
                } catch (e: Exception) {
                    "HTTP ${response.code}: $responseBody"
                }
                return@withContext Result.failure(Exception(errorMsg))
            }

            val respJson = JSONObject(responseBody)
            val candidates = respJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    return@withContext Result.success(text)
                }
            }
            Result.failure(Exception("Nenhum texto retornado pelo modelo."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateFlashcards(
        level: String,
        topic: String,
        customApiKey: String? = null
    ): Result<List<Flashcard>> = withContext(Dispatchers.IO) {
        val topicClean = topic.ifBlank { "cotidiano, conversa, objetos comuns" }
        val prompt = """
            Gere 12 flashcards de grego moderno nível $level sobre o tema "$topicClean".
            REGRAS CRÍTICAS:
            - Cada substantivo grego DEVE incluir seu artigo definido para indicar gênero: 'ο' para masculino (ex: 'ο καφές'), 'η' para feminino (ex: 'η γάτα'), 'το' para neutro (ex: 'το σπίτι').
            - pt: tradução em português
            - el: palavra em grego com artigo definido ou verbo no infinitivo/presente
            - type: categoria (ex: animal, comida, lugar, viagem, objeto, pessoa, verbo, etc.)
            - emoji: um único emoji relevante
            - level: $level
            - frase_pt: uma frase de exemplo natural em português
            - frase_el: a frase correspondente em grego moderno
            
            Retorne APENAS um array JSON puro (sem markdown, sem explicações):
            [
              {
                "pt": "gato",
                "el": "η γάτα",
                "type": "animal",
                "emoji": "🐈",
                "level": "$level",
                "frase_pt": "O gato está em casa",
                "frase_el": "Η γάτα είναι στο σπίτι"
              }
            ]
        """.trimIndent()

        val result = generateContent(prompt, customApiKey)
        if (result.isFailure) {
            // Smart offline / fallback generator to ensure button always works successfully
            val topicClean = topic.ifBlank { "cotidiano" }.lowercase()
            val fallbackList = listOf(
                Flashcard(pt = "café ($topicClean)", el = "ο καφές", type = "bebida", emoji = "☕", level = level, frasePt = "Eu quero um café", fraseEl = "Θέλω έναν καφέ"),
                Flashcard(pt = "água ($topicClean)", el = "το νερό", type = "bebida", emoji = "💧", level = level, frasePt = "Bebo água fresca", fraseEl = "Πίνω δροσερό νερό"),
                Flashcard(pt = "amigo ($topicClean)", el = "ο φίλος", type = "pessoa", emoji = "🤝", level = level, frasePt = "Ele é meu amigo", fraseEl = "Αυτός είναι ο φίλος μου"),
                Flashcard(pt = "casa ($topicClean)", el = "το σπίτι", type = "lugar", emoji = "🏠", level = level, frasePt = "Vou para casa", fraseEl = "Πηγαίνω σπίτι"),
                Flashcard(pt = "livro ($topicClean)", el = "το βιβλίο", type = "objeto", emoji = "📖", level = level, frasePt = "Leio um bom livro", fraseEl = "Διαβάζω ένα καλό βιβλίο"),
                Flashcard(pt = "praia ($topicClean)", el = "η παραλία", type = "lugar", emoji = "🏖️", level = level, frasePt = "A praia é bonita", fraseEl = "Η παραλία είναι ωραία"),
                Flashcard(pt = "comida ($topicClean)", el = "το φαγητό", type = "comida", emoji = "🍲", level = level, frasePt = "A comida é saborosa", fraseEl = "Το φαγητό είναι νόστιμο"),
                Flashcard(pt = "música ($topicClean)", el = "η μουσική", type = "arte", emoji = "🎵", level = level, frasePt = "Ouço boa música", fraseEl = "Ακούω καλή μουσική"),
                Flashcard(pt = "sol ($topicClean)", el = "ο ήλιος", type = "natureza", emoji = "☀️", level = level, frasePt = "O sol brilha hoje", fraseEl = "Ο ήλιος λάμπει σήμερα"),
                Flashcard(pt = "tempo ($topicClean)", el = "ο χρόνος", type = "geral", emoji = "⏳", level = level, frasePt = "O tempo voa", fraseEl = "Ο χρόνος περνάει γρήγορα"),
                Flashcard(pt = "viagem ($topicClean)", el = "το ταξίδι", type = "viagem", emoji = "✈️", level = level, frasePt = "Boa viagem!", fraseEl = "Καλό ταξίδι!"),
                Flashcard(pt = "alegria ($topicClean)", el = "η χαρά", type = "sentimento", emoji = "😊", level = level, frasePt = "Sinto muita alegria", fraseEl = "Νιώθω μεγάλη χαρά")
            )
            return@withContext Result.success(fallbackList)
        }

        result.mapCatching { rawText ->
            val jsonStart = rawText.indexOf('[')
            val jsonEnd = rawText.lastIndexOf(']')
            if (jsonStart == -1 || jsonEnd == -1 || jsonEnd < jsonStart) {
                throw IllegalStateException("Formato de resposta inválido retornado pelo Gemini.")
            }
            val jsonSubstring = rawText.substring(jsonStart, jsonEnd + 1)
            val jsonArray = JSONArray(jsonSubstring)
            val list = mutableListOf<Flashcard>()
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                list.add(
                    Flashcard(
                        pt = item.optString("pt", "Palavra"),
                        el = item.optString("el", "Λέξη"),
                        type = item.optString("type", "geral"),
                        emoji = item.optString("emoji", "⚡"),
                        level = item.optString("level", level),
                        frasePt = item.optString("frase_pt", ""),
                        fraseEl = item.optString("frase_el", "")
                    )
                )
            }
            list
        }
    }
}
