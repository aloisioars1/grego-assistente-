package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Flashcard
import com.example.ui.theme.*

@Composable
fun StatsDialog(
    cards: List<Flashcard>,
    onDismiss: () -> Unit
) {
    val total = cards.size
    val mastered = cards.count { it.isMastered }
    val percentage = if (total > 0) (mastered * 100) / total else 0

    val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
    val breakdown = remember(cards) {
        levels.associateWith { lvl ->
            val lvlCards = cards.filter { it.level.equals(lvl, true) }
            val count = lvlCards.size
            val themes = lvlCards.map { it.type.ifBlank { "geral" }.lowercase() }.distinct()
            Pair(count, themes)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .background(VibrantSurface, RoundedCornerShape(32.dp))
                .border(1.5.dp, VibrantBorderLight, RoundedCornerShape(32.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(VibrantSecondaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📊", fontSize = 18.sp)
                        }
                        Text(
                            text = "Estatísticas & Temas por Nível",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = VibrantTextPrimary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Text("✕", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VibrantTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mastery Progress Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VibrantSurfaceVariant, RoundedCornerShape(20.dp))
                        .border(1.dp, VibrantBorderLight, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Taxa de Domínio do Vocabulário", fontSize = 11.sp, color = VibrantTextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "$percentage%", fontSize = 32.sp, fontWeight = FontWeight.Black, color = VibrantPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { if (total > 0) mastered.toFloat() / total else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(999.dp)),
                            color = VibrantPrimary,
                            trackColor = VibrantSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "$mastered de $total cartas dominadas", fontSize = 10.sp, color = VibrantTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Distribuição de Palavras e Temas (A1 ao C2)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantTextPrimary,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Level & Theme Breakdown
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    levels.forEach { lvl ->
                        val data = breakdown[lvl] ?: Pair(0, emptyList())
                        val count = data.first
                        val themes = data.second

                        LevelThemeRow(level = lvl, count = count, themes = themes)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Continuar Estudando", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun LevelThemeRow(level: String, count: Int, themes: List<String>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(VibrantSurfaceVariant, RoundedCornerShape(16.dp))
            .border(1.dp, VibrantBorderLight, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .background(VibrantPrimary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Nível $level", fontSize = 11.sp, fontWeight = FontWeight.Black, color = VibrantPrimary)
                    }
                    Text(text = "$count palavras", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibrantTextPrimary)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (themes.isNotEmpty()) "Temas: ${themes.joinToString(", ")}" else "Temas: Nenhum registrado",
                    fontSize = 10.sp,
                    color = VibrantTextSecondary
                )
            }
        }
    }
}
