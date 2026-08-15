package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DefaultDeck
import com.example.data.model.ChatMessage
import com.example.data.model.Flashcard
import com.example.data.remote.GeminiService
import com.example.util.TextToSpeechHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChisteViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val flashcardDao = database.flashcardDao()
    private val chatMessageDao = database.chatMessageDao()
    private val geminiService = GeminiService()

    private val prefs = application.getSharedPreferences("chiste_prefs", Context.MODE_PRIVATE)

    var ttsHelper: TextToSpeechHelper? = null
        private set

    // Flashcard Deck State
    val allCards: StateFlow<List<Flashcard>> = flashcardDao.getAllCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _filterLevel = MutableStateFlow("TODOS")
    val filterLevel: StateFlow<String> = _filterLevel.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _currentIndex.value = 0
        _isFlipped.value = false
    }

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isFlipped = MutableStateFlow(false)
    val isFlipped: StateFlow<Boolean> = _isFlipped.asStateFlow()

    // Filtered Cards according to filterLevel and searchQuery
    val filteredCards: StateFlow<List<Flashcard>> = combine(allCards, _filterLevel, _searchQuery) { cards, level, query ->
        var list = if (level == "TODOS") cards else cards.filter { it.level.equals(level, ignoreCase = true) }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter { it.pt.lowercase().contains(q) || it.el.lowercase().contains(q) || it.type.lowercase().contains(q) }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    data class LevelThemeStats(val count: Int, val themes: List<String>)

    fun getLevelThemeBreakdown(cards: List<Flashcard>): Map<String, LevelThemeStats> {
        val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
        val result = mutableMapOf<String, LevelThemeStats>()
        for (lvl in levels) {
            val lvlCards = cards.filter { it.level.equals(lvl, ignoreCase = true) }
            val count = lvlCards.size
            val themes = lvlCards.map { it.type.ifBlank { "geral" }.lowercase() }.distinct()
            result[lvl] = LevelThemeStats(count = count, themes = themes)
        }
        return result
    }

    // Active Card
    val currentCard: StateFlow<Flashcard?> = combine(filteredCards, _currentIndex) { cards, idx ->
        if (cards.isEmpty()) null
        else cards[idx.coerceIn(0, cards.size - 1).coerceAtLeast(0)]
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Generator State
    private val _generatorLevel = MutableStateFlow("A1")
    val generatorLevel: StateFlow<String> = _generatorLevel.asStateFlow()

    private val _topicInput = MutableStateFlow("")
    val topicInput: StateFlow<String> = _topicInput.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Notification / Toast Message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // API Key State
    private val _customApiKey = MutableStateFlow(prefs.getString("gemini_key", "") ?: "")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _showApiKeyDialog = MutableStateFlow(false)
    val showApiKeyDialog: StateFlow<Boolean> = _showApiKeyDialog.asStateFlow()

    private val _apiTestStatus = MutableStateFlow<String?>(null)
    val apiTestStatus: StateFlow<String?> = _apiTestStatus.asStateFlow()

    private val _isTestingApi = MutableStateFlow(false)
    val isTestingApi: StateFlow<Boolean> = _isTestingApi.asStateFlow()

    // Lina Chat State
    val chatMessages: StateFlow<List<ChatMessage>> = chatMessageDao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showAssistant = MutableStateFlow(false)
    val showAssistant: StateFlow<Boolean> = _showAssistant.asStateFlow()

    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Deck List Sheet
    private val _showDeckSheet = MutableStateFlow(false)
    val showDeckSheet: StateFlow<Boolean> = _showDeckSheet.asStateFlow()

    private val _showStatsDialog = MutableStateFlow(false)
    val showStatsDialog: StateFlow<Boolean> = _showStatsDialog.asStateFlow()

    private val _showQuizDialog = MutableStateFlow(false)
    val showQuizDialog: StateFlow<Boolean> = _showQuizDialog.asStateFlow()

    private val _showSpeechDialog = MutableStateFlow(false)
    val showSpeechDialog: StateFlow<Boolean> = _showSpeechDialog.asStateFlow()

    fun setShowStatsDialog(show: Boolean) { _showStatsDialog.value = show }
    fun setShowQuizDialog(show: Boolean) { _showQuizDialog.value = show }
    fun setShowSpeechDialog(show: Boolean) { _showSpeechDialog.value = show }

    init {
        ttsHelper = TextToSpeechHelper(application)
        ensureDefaultDeckIfEmpty()
    }

    private fun ensureDefaultDeckIfEmpty() {
        viewModelScope.launch {
            val count = flashcardDao.getCount()
            if (count == 0) {
                flashcardDao.insertCards(DefaultDeck.initialCards)
                chatMessageDao.insertMessage(
                    ChatMessage(
                        role = "ai",
                        text = "Γειά σου! Sou Lina 🧠 Só Flash 3.5\n\nMe diz nível A1-C2 ou escolha uma ação:\n📍 Frases\n💬 Diálogos\n📖 História\n🧠 Quiz\n🗓️ Plano 7 dias"
                    )
                )
            }
        }
    }

    // Card Navigation & Interaction
    fun flipCard() {
        _isFlipped.value = !_isFlipped.value
    }

    fun setFlipped(flipped: Boolean) {
        _isFlipped.value = flipped
    }

    fun nextCard() {
        _isFlipped.value = false
        val total = filteredCards.value.size
        if (total > 0) {
            _currentIndex.value = (_currentIndex.value + 1) % total
        }
    }

    fun prevCard() {
        _isFlipped.value = false
        val total = filteredCards.value.size
        if (total > 0) {
            _currentIndex.value = if (_currentIndex.value - 1 < 0) total - 1 else _currentIndex.value - 1
        }
    }

    fun shuffleCards() {
        _isFlipped.value = false
        val total = filteredCards.value.size
        if (total > 1) {
            _currentIndex.value = (0 until total).random()
        }
        showToast("🔀 Cartões misturados!")
    }

    fun selectCardIndex(index: Int) {
        _isFlipped.value = false
        val total = filteredCards.value.size
        if (index in 0 until total) {
            _currentIndex.value = index
        }
    }

    fun toggleMastered(card: Flashcard) {
        viewModelScope.launch {
            val updated = card.copy(isMastered = !card.isMastered)
            flashcardDao.updateCard(updated)
            showToast(if (updated.isMastered) "⭐ Marcado como aprendido!" else "Marcado para revisar")
        }
    }

    fun toggleFavorite(card: Flashcard) {
        viewModelScope.launch {
            val updated = card.copy(isFavorite = !card.isFavorite)
            flashcardDao.updateCard(updated)
        }
    }

    fun deleteCard(card: Flashcard) {
        viewModelScope.launch {
            flashcardDao.deleteCard(card)
            showToast("🗑️ Cartão excluído")
        }
    }

    fun addCustomCard(
        pt: String,
        el: String,
        level: String,
        type: String,
        emoji: String,
        frasePt: String,
        fraseEl: String
    ) {
        viewModelScope.launch {
            val newCard = Flashcard(
                pt = pt.trim(),
                el = el.trim(),
                level = level,
                type = type.ifBlank { "geral" },
                emoji = emoji.ifBlank { "⚡" },
                frasePt = frasePt.trim(),
                fraseEl = fraseEl.trim()
            )
            flashcardDao.insertCard(newCard)
            showToast("✅ Cartão adicionado!")
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            flashcardDao.insertCards(DefaultDeck.initialCards)
            showToast("🔄 Cartões padrão restaurados!")
        }
    }

    // Generator Controls
    fun setGeneratorLevel(level: String) {
        _generatorLevel.value = level
    }

    fun setTopicInput(topic: String) {
        _topicInput.value = topic
    }

    fun setFilterLevel(level: String) {
        _filterLevel.value = level
        _currentIndex.value = 0
        _isFlipped.value = false
    }

    fun generateCards() {
        if (_isGenerating.value) return
        val level = _generatorLevel.value
        val topic = _topicInput.value
        val key = _customApiKey.value

        _isGenerating.value = true
        showToast("⚡ Gerando 12 cards $level com Flash 3.5...")

        viewModelScope.launch {
            val result = geminiService.generateFlashcards(level, topic, key)
            result.onSuccess { newCards ->
                if (newCards.isNotEmpty()) {
                    flashcardDao.insertCards(newCards)
                    _currentIndex.value = 0
                    _isFlipped.value = false
                    showToast("✅ ${newCards.size} cards $level criados com sucesso!")
                } else {
                    showToast("Nenhum card foi gerado. Tente outro tema.")
                }
            }.onFailure { err ->
                showToast("❌ Erro: ${err.localizedMessage ?: err.message}")
            }
            _isGenerating.value = false
        }
    }

    // Audio Playback
    fun speak(text: String, stripArticle: Boolean = false) {
        ttsHelper?.speak(text, stripArticle)
    }

    // Lina Chat Actions
    fun setShowAssistant(show: Boolean) {
        _showAssistant.value = show
    }

    fun setChatInput(text: String) {
        _chatInput.value = text
    }

    fun sendChat(presetAction: String? = null) {
        val userText = presetAction ?: _chatInput.value.trim()
        if (userText.isBlank() || _isChatLoading.value) return

        _chatInput.value = ""
        val currentLvl = _generatorLevel.value
        val currentTopic = _topicInput.value

        viewModelScope.launch {
            val userMsg = ChatMessage(role = "user", text = userText)
            chatMessageDao.insertMessage(userMsg)

            _isChatLoading.value = true

            // Sample vocabulary for context
            val vocabSample = allCards.value.take(15).joinToString(", ") { "${it.el} (${it.pt})" }

            val taskDescription = when {
                userText.contains("Frases", ignoreCase = true) ->
                    "5 FRASES práticas em grego moderno nível $currentLvl com o vocabulário: $vocabSample. Inclua tradução em português e dica de pronúncia (Shadowing)."
                userText.contains("Diálogo", ignoreCase = true) || userText.contains("Dialogo", ignoreCase = true) ->
                    "DIÁLOGO natural de 6 falas em grego moderno nível $currentLvl sobre o tema '${currentTopic.ifBlank { "café / dia a dia" }}' com o vocabulário: $vocabSample. Inclua tradução PT logo abaixo de cada fala."
                userText.contains("História", ignoreCase = true) || userText.contains("Historia", ignoreCase = true) ->
                    "MICRO-HISTÓRIA em grego moderno de 5 frases nível $currentLvl utilizando as palavras: $vocabSample. Tradução PT parágrafo a parágrafo."
                userText.contains("Quiz", ignoreCase = true) ->
                    "QUIZ interativo de 3 perguntas em grego nível $currentLvl usando o vocabulário $vocabSample. Apresente múltipla escolha (A, B, C) e gabarito com explicação ao final."
                userText.contains("Plano", ignoreCase = true) ->
                    "PLANO DE ESTUDO DE 7 DIAS autodidata para nível $currentLvl focado em repetição espaçada, chunks e shadowing de 15 minutos por dia."
                else -> userText
            }

            val systemInstruction = """
                Você é Lina, uma poliglota autodidata e tutora experiente de Grego Moderno.
                Sua metodologia é baseada em:
                1. Input Compreensível (Input+1)
                2. Shadowing (repetição de áudio e ritmo)
                3. Chunks de linguagem (frases prontas do cotidiano)
                4. Princípio 80/20 (as palavras e estruturas mais úteis primeiro).
                Responda com entusiasmo, clareza e objetividade, combinando Grego (EL) e Português (PT).
            """.trimIndent()

            val fullPrompt = """
                Nível do Aluno: $currentLvl
                Vocabulário recente: $vocabSample
                Solicitação do Aluno: $taskDescription
            """.trimIndent()

            val result = geminiService.generateContent(fullPrompt, _customApiKey.value, systemInstruction)
            result.onSuccess { responseText ->
                val aiMsg = ChatMessage(role = "ai", text = responseText)
                chatMessageDao.insertMessage(aiMsg)
            }.onFailure { err ->
                val errorMsg = ChatMessage(role = "ai", text = "❌ Erro ao consultar Lina: ${err.localizedMessage ?: err.message}")
                chatMessageDao.insertMessage(errorMsg)
            }

            _isChatLoading.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            chatMessageDao.clearHistory()
            chatMessageDao.insertMessage(
                ChatMessage(
                    role = "ai",
                    text = "Γειά σου! Chat limpo. Escolha uma ação rápida acima ou faça sua pergunta sobre grego!"
                )
            )
        }
    }

    // API Key Dialog & Testing
    fun setShowApiKeyDialog(show: Boolean) {
        _showApiKeyDialog.value = show
        _apiTestStatus.value = null
    }

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key
        prefs.edit().putString("gemini_key", key).apply()
    }

    fun testApiKey() {
        if (_isTestingApi.value) return
        _isTestingApi.value = true
        _apiTestStatus.value = "Testando conexão com Flash 3.5..."

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val result = geminiService.generateContent(
                "Diga apenas '⚡ Flash 3.5 Conectado com Sucesso!' em português e grego.",
                _customApiKey.value
            )
            val elapsed = System.currentTimeMillis() - startTime
            result.onSuccess { text ->
                _apiTestStatus.value = "✅ OK (${elapsed}ms): $text"
                showToast("✅ Conexão Flash 3.5 confirmada!")
            }.onFailure { err ->
                _apiTestStatus.value = "❌ Falha: ${err.localizedMessage ?: err.message}"
            }
            _isTestingApi.value = false
        }
    }

    fun setShowDeckSheet(show: Boolean) {
        _showDeckSheet.value = show
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper?.shutdown()
    }
}
