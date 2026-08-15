package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ApiKeyDialog
import com.example.ui.components.CardDeckSheet
import com.example.ui.components.FlashcardView
import com.example.ui.components.LinaAssistantModal
import com.example.ui.components.StatsDialog
import com.example.ui.components.ListeningQuizDialog
import com.example.ui.components.SpeechPracticeDialog
import com.example.ui.components.NeoButton
import com.example.ui.components.NeoChip
import com.example.ui.components.NeoInputField
import com.example.ui.theme.GenderFemininePink
import com.example.ui.theme.GenderMasculineBlue
import com.example.ui.theme.GenderNeuterGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VibrantAccentPurple
import com.example.ui.theme.VibrantBackground
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantBorderLight
import com.example.ui.theme.VibrantBorderOutline
import com.example.ui.theme.VibrantOnPrimaryContainer
import com.example.ui.theme.VibrantPrimary
import com.example.ui.theme.VibrantPrimaryContainer
import com.example.ui.theme.VibrantSecondaryContainer
import com.example.ui.theme.VibrantShadow
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.ui.theme.VibrantTertiaryContainer
import com.example.ui.theme.VibrantTertiaryText
import com.example.ui.theme.VibrantTextDark
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import com.example.viewmodel.ChisteViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: ChisteViewModel = viewModel()
                ChisteMainScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ChisteMainScreen(viewModel: ChisteViewModel) {
    val allCards by viewModel.allCards.collectAsStateWithLifecycle()
    val filteredCards by viewModel.filteredCards.collectAsStateWithLifecycle()
    val currentCard by viewModel.currentCard.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentIndex.collectAsStateWithLifecycle()
    val isFlipped by viewModel.isFlipped.collectAsStateWithLifecycle()

    val generatorLevel by viewModel.generatorLevel.collectAsStateWithLifecycle()
    val topicInput by viewModel.topicInput.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val customApiKey by viewModel.customApiKey.collectAsStateWithLifecycle()
    val showApiKeyDialog by viewModel.showApiKeyDialog.collectAsStateWithLifecycle()
    val apiTestStatus by viewModel.apiTestStatus.collectAsStateWithLifecycle()
    val isTestingApi by viewModel.isTestingApi.collectAsStateWithLifecycle()

    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val showAssistant by viewModel.showAssistant.collectAsStateWithLifecycle()
    val chatInput by viewModel.chatInput.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()

    val showDeckSheet by viewModel.showDeckSheet.collectAsStateWithLifecycle()
    val showStatsDialog by viewModel.showStatsDialog.collectAsStateWithLifecycle()
    val showQuizDialog by viewModel.showQuizDialog.collectAsStateWithLifecycle()
    val showSpeechDialog by viewModel.showSpeechDialog.collectAsStateWithLifecycle()
    val filterLevel by viewModel.filterLevel.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    // Auto dismiss toast after 3.5 seconds
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(3500)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = VibrantBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top App Header
                TopHeaderBar(
                    hasKey = customApiKey.isNotBlank(),
                    onOpenAssistant = { viewModel.setShowAssistant(true) },
                    onOpenDeck = { viewModel.setShowDeckSheet(true) },
                    onOpenKey = { viewModel.setShowApiKeyDialog(true) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Advanced Features Row (Stats, Quiz, Speech)
                AdvancedFeaturesRow(
                    onOpenStats = { viewModel.setShowStatsDialog(true) },
                    onOpenQuiz = { viewModel.setShowQuizDialog(true) },
                    onOpenSpeech = { viewModel.setShowSpeechDialog(true) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // AI Flashcard Generator Card
                AIGeneratorCard(
                    level = generatorLevel,
                    topic = topicInput,
                    isGenerating = isGenerating,
                    onLevelChange = { viewModel.setGeneratorLevel(it) },
                    onTopicChange = { viewModel.setTopicInput(it) },
                    onGenerate = { viewModel.generateCards() }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar for filtering flashcards by word or definition
                NeoInputField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = "🔍 Buscar por palavra ou definição...",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Level Filter & Gender Legend Chips
                FilterAndLegendRow(
                    activeFilter = filterLevel,
                    onFilterChange = { viewModel.setFilterLevel(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Active Flashcard Card
                if (currentCard != null) {
                    FlashcardView(
                        card = currentCard!!,
                        currentIndex = currentIndex,
                        totalCards = filteredCards.size,
                        isFlipped = isFlipped,
                        onFlip = { viewModel.flipCard() },
                        onNext = { viewModel.nextCard() },
                        onPrev = { viewModel.prevCard() },
                        onShuffle = { viewModel.shuffleCards() },
                        onToggleMastered = { viewModel.toggleMastered(currentCard!!) },
                        onSpeak = { text, strip -> viewModel.speak(text, strip) }
                    )
                } else {
                    EmptyDeckPlaceholder(
                        onReset = { viewModel.resetToDefaults() }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Navigation & Controls Row
                BottomControlsRow(
                    hasCards = filteredCards.isNotEmpty(),
                    onPrev = { viewModel.prevCard() },
                    onSpeak = { currentCard?.let { viewModel.speak(it.el, false) } },
                    onShuffle = { viewModel.shuffleCards() },
                    onNext = { viewModel.nextCard() }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Floating Toast Notification
            AnimatedVisibility(
                visible = toastMessage != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                toastMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .background(VibrantTextDark, RoundedCornerShape(999.dp))
                            .border(1.5.dp, VibrantBorderLight, RoundedCornerShape(999.dp))
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .testTag("toast_message")
                    ) {
                        Text(
                            text = msg,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Lina AI Tutor Assistant Modal
    if (showAssistant) {
        LinaAssistantModal(
            level = generatorLevel,
            messages = chatMessages,
            chatInput = chatInput,
            isLoading = isChatLoading,
            onInputChange = { viewModel.setChatInput(it) },
            onSendMessage = { viewModel.sendChat(it) },
            onClearChat = { viewModel.clearChat() },
            onSpeak = { viewModel.speak(it) },
            onDismiss = { viewModel.setShowAssistant(false) }
        )
    }

    // Deck Browser & Add Card Sheet
    if (showDeckSheet) {
        CardDeckSheet(
            cards = allCards,
            selectedFilterLevel = filterLevel,
            onFilterLevelChange = { viewModel.setFilterLevel(it) },
            onSelectCard = { viewModel.selectCardIndex(it) },
            onDeleteCard = { viewModel.deleteCard(it) },
            onToggleMastered = { viewModel.toggleMastered(it) },
            onAddCard = { pt, el, lvl, typ, em, fPt, fEl ->
                viewModel.addCustomCard(pt, el, lvl, typ, em, fPt, fEl)
            },
            onResetDefaults = { viewModel.resetToDefaults() },
            onSpeak = { viewModel.speak(it) },
            onDismiss = { viewModel.setShowDeckSheet(false) }
        )
    }

    // API Key Dialog
    if (showApiKeyDialog) {
        ApiKeyDialog(
            currentKey = customApiKey,
            onSaveKey = { viewModel.setCustomApiKey(it) },
            testStatus = apiTestStatus,
            isTesting = isTestingApi,
            onTestKey = { viewModel.testApiKey() },
            onDismiss = { viewModel.setShowApiKeyDialog(false) }
        )
    }

    // Stats Dialog
    if (showStatsDialog) {
        StatsDialog(
            cards = allCards,
            onDismiss = { viewModel.setShowStatsDialog(false) }
        )
    }

    // Listening Quiz Dialog
    if (showQuizDialog) {
        ListeningQuizDialog(
            cards = allCards,
            onSpeak = { viewModel.speak(it, true) },
            onDismiss = { viewModel.setShowQuizDialog(false) }
        )
    }

    // Speech Practice Dialog
    if (showSpeechDialog) {
        SpeechPracticeDialog(
            cards = allCards,
            onSpeak = { viewModel.speak(it, true) },
            onDismiss = { viewModel.setShowSpeechDialog(false) }
        )
    }
}

@Composable
private fun AdvancedFeaturesRow(
    onOpenStats: () -> Unit,
    onOpenQuiz: () -> Unit,
    onOpenSpeech: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onOpenStats,
            modifier = Modifier.weight(1f).height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VibrantSurfaceVariant),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("📊 Estatísticas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
        }

        Button(
            onClick = onOpenQuiz,
            modifier = Modifier.weight(1f).height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VibrantSurfaceVariant),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("🎧 Quiz Auditivo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
        }

        Button(
            onClick = onOpenSpeech,
            modifier = Modifier.weight(1f).height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VibrantSurfaceVariant),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("🎙️ Treino Fala", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
        }
    }
}

@Composable
private fun TopHeaderBar(
    hasKey: Boolean,
    onOpenAssistant: () -> Unit,
    onOpenDeck: () -> Unit,
    onOpenKey: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title & Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(VibrantPrimary, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⚡", fontSize = 20.sp, color = Color.White)
            }

            Column {
                Text(
                    text = "Chiste Greek",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = VibrantTextPrimary,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = "GEMINI 3.5 FLASH • A1→C2",
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = VibrantTextSecondary,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Action Buttons: Baralho, Lina, Key
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(VibrantSecondaryContainer, CircleShape)
                    .clickable { onOpenDeck() }
                    .testTag("open_deck_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📚",
                    fontSize = 17.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(VibrantSecondaryContainer, CircleShape)
                    .clickable { onOpenAssistant() }
                    .testTag("open_assistant_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🤖",
                    fontSize = 18.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (hasKey) VibrantPrimaryContainer else VibrantTertiaryContainer,
                        CircleShape
                    )
                    .clickable { onOpenKey() }
                    .testTag("open_key_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔑",
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
private fun AIGeneratorCard(
    level: String,
    topic: String,
    isGenerating: Boolean,
    onLevelChange: (String) -> Unit,
    onTopicChange: (String) -> Unit,
    onGenerate: () -> Unit
) {
    val shape = RoundedCornerShape(28.dp)
    var levelMenuExpanded by remember { mutableStateOf(false) }
    val levelOptions = listOf("A1", "A2", "B1", "B2", "C1", "C2")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_generator_card")
    ) {
        // Card Face
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(VibrantSurfaceVariant, shape)
                .border(1.dp, VibrantBorderLight, shape)
                .clip(shape)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Magic Generator",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = VibrantTextSecondary
                )

                // Level Selector Dropdown Badge
                Box {
                    Box(
                        modifier = Modifier
                            .background(VibrantPrimary, RoundedCornerShape(999.dp))
                            .clickable { levelMenuExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Level $level ▼",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = levelMenuExpanded,
                        onDismissRequest = { levelMenuExpanded = false }
                    ) {
                        levelOptions.forEach { lvl ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "$lvl (${when(lvl) {
                                            "A1" -> "Iniciante"
                                            "A2" -> "Básico"
                                            "B1" -> "Intermediário"
                                            "B2" -> "Independente"
                                            "C1" -> "Avançado"
                                            else -> "Fluente"
                                        }})",
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                onClick = {
                                    onLevelChange(lvl)
                                    levelMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Topic Input + Generate Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoInputField(
                    value = topic,
                    onValueChange = onTopicChange,
                    placeholder = "Topic: Travel, Food, Animals...",
                    modifier = Modifier.weight(1f),
                    testTag = "topic_input_field"
                )

                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .background(
                            if (isGenerating) VibrantSecondaryContainer else VibrantAccentPurple,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable(enabled = !isGenerating) { onGenerate() }
                        .padding(horizontal = 16.dp)
                        .testTag("generate_cards_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = VibrantPrimary
                        )
                    } else {
                        Text(
                            text = "Generate",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = VibrantOnPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterAndLegendRow(
    activeFilter: String,
    onFilterChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Gender Color Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Artigos & Gênero:",
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = VibrantTextSecondary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                LegendBadge(label = "ο (Masc)", color = GenderMasculineBlue)
                LegendBadge(label = "η (Fem)", color = GenderFemininePink)
                LegendBadge(label = "το (Neutro)", color = GenderNeuterGreen)
            }
        }

        // Level Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val filterOptions = listOf("TODOS", "A1", "A2", "B1", "B2", "C1", "C2")
            filterOptions.forEach { opt ->
                NeoChip(
                    text = opt,
                    isSelected = activeFilter == opt,
                    onClick = { onFilterChange(opt) },
                    selectedBg = VibrantPrimary,
                    unselectedBg = VibrantSecondaryContainer,
                    testTag = "main_filter_$opt"
                )
            }
        }
    }
}

@Composable
private fun LegendBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = color
        )
    }
}

@Composable
private fun BottomControlsRow(
    hasCards: Boolean,
    onPrev: () -> Unit,
    onSpeak: () -> Unit,
    onShuffle: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Prev button (#EADDFF)
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(VibrantPrimaryContainer, CircleShape)
                .clickable(enabled = hasCards) { onPrev() }
                .testTag("bottom_prev_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Anterior",
                tint = VibrantOnPrimaryContainer,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Center Speaker Audio Playback button (#E8DEF8)
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(VibrantSecondaryContainer, CircleShape)
                .clickable(enabled = hasCards) { onSpeak() }
                .testTag("bottom_audio_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Ouvir pronúncia da palavra grega",
                tint = VibrantTextDark,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Shuffle button
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(VibrantSurfaceVariant, CircleShape)
                .border(1.dp, VibrantBorderLight, CircleShape)
                .clickable(enabled = hasCards) { onShuffle() }
                .testTag("bottom_shuffle_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "Embaralhar cartas",
                tint = VibrantTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Next button (#6750A4)
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(VibrantPrimary, CircleShape)
                .clickable(enabled = hasCards) { onNext() }
                .testTag("bottom_next_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Próximo",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun EmptyDeckPlaceholder(onReset: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(VibrantSurface, RoundedCornerShape(32.dp))
            .border(2.dp, VibrantBorder, RoundedCornerShape(32.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "📭", fontSize = 48.sp)
            Text(
                text = "Nenhum flashcard neste filtro.",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = VibrantTextPrimary
            )
            Text(
                text = "Gere novos cards com o Gemini Flash acima ou restaure o deck padrão!",
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = VibrantTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .background(VibrantPrimary, RoundedCornerShape(16.dp))
                    .clickable { onReset() }
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .testTag("empty_state_reset_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Restaurar Deck Padrão",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}
