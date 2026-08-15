package com.example.ui.components

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Flashcard
import com.example.util.GreekTransliterationHelper
import com.example.ui.theme.GenderFemininePink
import com.example.ui.theme.GenderMasculineBlue
import com.example.ui.theme.GenderNeuterGreen
import com.example.ui.theme.GenderOtherPurple
import com.example.ui.theme.VibrantAccentPurple
import com.example.ui.theme.VibrantBackground
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantBorderLight
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

@Composable
fun CardDeckSheet(
    cards: List<Flashcard>,
    selectedFilterLevel: String,
    onFilterLevelChange: (String) -> Unit,
    onSelectCard: (Int) -> Unit,
    onDeleteCard: (Flashcard) -> Unit,
    onToggleMastered: (Flashcard) -> Unit,
    onAddCard: (pt: String, el: String, level: String, type: String, emoji: String, frasePt: String, fraseEl: String) -> Unit,
    onResetDefaults: () -> Unit,
    onSpeak: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val levels = listOf("TODOS", "A1", "A2", "B1", "B2", "C1", "C2")

    val displayedCards = cards.filter { card ->
        val matchesLevel = selectedFilterLevel == "TODOS" || card.level.equals(selectedFilterLevel, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                card.pt.contains(searchQuery, ignoreCase = true) ||
                card.el.contains(searchQuery, ignoreCase = true) ||
                card.type.contains(searchQuery, ignoreCase = true)
        matchesLevel && matchesSearch
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(VibrantBackground)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .testTag("deck_sheet_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VibrantSurface)
                        .border(1.dp, VibrantBorderLight)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                            Text(text = "📚", fontSize = 20.sp)
                        }
                        Column {
                            Text(
                                text = "Baralho de Grego",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = VibrantTextPrimary
                            )
                            Text(
                                text = "${displayedCards.size} de ${cards.size} cartões",
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                color = VibrantTextSecondary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(VibrantPrimary, CircleShape)
                                .clickable { showAddDialog = true }
                                .testTag("add_card_icon_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Novo Card",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(VibrantSecondaryContainer, CircleShape)
                                .clickable { onDismiss() }
                                .testTag("close_deck_sheet_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar",
                                tint = VibrantTextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Search & Levels Row
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VibrantSurface)
                        .border(1.dp, VibrantBorderLight)
                        .padding(12.dp)
                ) {
                    NeoInputField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Buscar em grego, português ou tema...",
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = VibrantTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        testTag = "deck_search_input"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        levels.forEach { lvl ->
                            NeoChip(
                                text = lvl,
                                isSelected = selectedFilterLevel == lvl,
                                onClick = { onFilterLevelChange(lvl) },
                                selectedBg = VibrantPrimary,
                                unselectedBg = VibrantSecondaryContainer,
                                testTag = "filter_$lvl"
                            )
                        }
                    }
                }

                // Cards List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(displayedCards, key = { _, card -> card.id }) { index, card ->
                        DeckListItem(
                            card = card,
                            onClick = {
                                val originalIndex = cards.indexOfFirst { it.id == card.id }
                                if (originalIndex != -1) {
                                    onSelectCard(originalIndex)
                                    onDismiss()
                                }
                            },
                            onSpeak = { onSpeak(card.el) },
                            onToggleMastered = { onToggleMastered(card) },
                            onDelete = { onDeleteCard(card) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(VibrantSurface, RoundedCornerShape(16.dp))
                                .border(1.5.dp, VibrantBorder, RoundedCornerShape(16.dp))
                                .clickable { onResetDefaults() }
                                .testTag("reset_deck_defaults_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Restaurar",
                                    tint = VibrantPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Restaurar Cartões Padrão",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = VibrantPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCustomCardDialog(
            onAdd = { pt, el, lvl, typ, em, fPt, fEl ->
                onAddCard(pt, el, lvl, typ, em, fPt, fEl)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun DeckListItem(
    card: Flashcard,
    onClick: () -> Unit,
    onSpeak: () -> Unit,
    onToggleMastered: () -> Unit,
    onDelete: () -> Unit
) {
    val genderColor = when {
        card.el.trim().startsWith("ο ", ignoreCase = true) -> GenderMasculineBlue
        card.el.trim().startsWith("η ", ignoreCase = true) -> GenderFemininePink
        card.el.trim().startsWith("το ", ignoreCase = true) -> GenderNeuterGreen
        else -> GenderOtherPurple
    }

    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // Card Container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VibrantSurface, shape)
                .border(1.5.dp, VibrantBorder, shape)
                .clip(shape)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Emoji Box
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(VibrantSecondaryContainer, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = card.emoji, fontSize = 22.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = card.el,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = VibrantPrimary
                        )

                        val translit = GreekTransliterationHelper.transliterate(card.el)
                        if (translit.isNotBlank()) {
                            Text(
                                text = "($translit)",
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp,
                                color = VibrantTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(VibrantTertiaryContainer, RoundedCornerShape(999.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = card.level,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = VibrantTertiaryText
                            )
                        }
                    }

                    Text(
                        text = card.pt,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = VibrantTextSecondary
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onSpeak, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Ouvir",
                        tint = VibrantTextDark,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(onClick = onToggleMastered, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Dominado",
                        tint = if (card.isMastered) GenderNeuterGreen else VibrantBorder,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = VibrantTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCustomCardDialog(
    onAdd: (pt: String, el: String, level: String, type: String, emoji: String, frasePt: String, fraseEl: String) -> Unit,
    onDismiss: () -> Unit
) {
    var pt by remember { mutableStateOf("") }
    var el by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("A1") }
    var type by remember { mutableStateOf("geral") }
    var emoji by remember { mutableStateOf("💡") }
    var frasePt by remember { mutableStateOf("") }
    var fraseEl by remember { mutableStateOf("") }

    val shape = RoundedCornerShape(28.dp)

    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .background(VibrantSurface, shape)
                    .border(1.5.dp, VibrantBorder, shape)
                    .clip(shape)
                    .padding(20.dp)
            ) {
                Text(
                    text = "➕ Criar Flashcard",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = VibrantTextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                NeoInputField(
                    value = el,
                    onValueChange = { el = it },
                    placeholder = "Grego (ex: η γάτα, ο καφές)",
                    testTag = "add_el_input"
                )

                Spacer(modifier = Modifier.height(8.dp))

                NeoInputField(
                    value = pt,
                    onValueChange = { pt = it },
                    placeholder = "Português (ex: gato, café)",
                    testTag = "add_pt_input"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeoInputField(
                        value = emoji,
                        onValueChange = { emoji = it },
                        placeholder = "Emoji (ex: ☕)",
                        modifier = Modifier.weight(1f),
                        testTag = "add_emoji_input"
                    )

                    NeoInputField(
                        value = level,
                        onValueChange = { level = it },
                        placeholder = "Nível (A1-C2)",
                        modifier = Modifier.weight(1f),
                        testTag = "add_level_input"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                NeoInputField(
                    value = fraseEl,
                    onValueChange = { fraseEl = it },
                    placeholder = "Frase Grego (opcional)",
                    testTag = "add_frase_el_input"
                )

                Spacer(modifier = Modifier.height(8.dp))

                NeoInputField(
                    value = frasePt,
                    onValueChange = { frasePt = it },
                    placeholder = "Frase Português (opcional)",
                    testTag = "add_frase_pt_input"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(VibrantSecondaryContainer, RoundedCornerShape(16.dp))
                            .clickable { onDismiss() }
                            .testTag("cancel_add_card_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Cancelar", fontWeight = FontWeight.Bold, color = VibrantTextDark)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .background(
                                if (pt.isNotBlank() && el.isNotBlank()) VibrantPrimary else Color.LightGray,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable(enabled = pt.isNotBlank() && el.isNotBlank()) {
                                onAdd(pt, el, level.uppercase(), type, emoji, frasePt, fraseEl)
                            }
                            .testTag("confirm_add_card_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Salvar Card", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
