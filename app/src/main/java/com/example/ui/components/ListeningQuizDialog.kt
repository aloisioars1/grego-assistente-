package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Flashcard
import com.example.ui.theme.*

@Composable
fun ListeningQuizDialog(
    cards: List<Flashcard>,
    onSpeak: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (cards.isEmpty()) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .background(VibrantSurface, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Nenhum card disponível para quiz.", color = VibrantTextPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = VibrantPrimary)) {
                        Text("Fechar", color = Color.White)
                    }
                }
            }
        }
        return
    }

    var currentIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }

    val currentCard = cards[currentIndex % cards.size]

    // Generate 4 options (1 correct, 3 random distractors)
    val options = remember(currentIndex, cards) {
        val correct = currentCard.pt
        val distractors = cards.filter { it.pt != correct }.shuffled().take(3).map { it.pt }
        (distractors + correct).shuffled()
    }

    LaunchedEffect(currentIndex) {
        // Auto play audio on card change
        onSpeak(currentCard.el)
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(VibrantSurface, RoundedCornerShape(32.dp))
                .border(1.5.dp, VibrantBorderLight, RoundedCornerShape(32.dp))
                .padding(24.dp)
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
                            Text("🎧", fontSize = 18.sp)
                        }
                        Text(
                            text = "Quiz Auditivo (${currentIndex + 1}/${cards.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = VibrantTextPrimary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Text("✕", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VibrantTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Audio Button
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .background(VibrantSecondaryContainer, CircleShape)
                        .border(2.dp, VibrantPrimary, CircleShape)
                        .clickable { onSpeak(currentCard.el) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Ouvir áudio",
                        tint = VibrantPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Toque para ouvir a pronúncia e escolha a tradução correta em português:",
                    fontSize = 12.sp,
                    color = VibrantTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Options list
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    options.forEach { option ->
                        val isSelected = selectedOption == option
                        val buttonColor = when {
                            selectedOption == null -> VibrantSurfaceVariant
                            option == currentCard.pt -> Color(0xFF10B981).copy(alpha = 0.2f) // Green correct
                            isSelected -> Color(0xFFEF4444).copy(alpha = 0.2f) // Red wrong
                            else -> VibrantSurfaceVariant
                        }

                        val borderColor = when {
                            selectedOption == null -> VibrantBorderLight
                            option == currentCard.pt -> Color(0xFF10B981)
                            isSelected -> Color(0xFFEF4444)
                            else -> VibrantBorderLight
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(buttonColor, RoundedCornerShape(16.dp))
                                .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                                .clickable(enabled = selectedOption == null) {
                                    selectedOption = option
                                    val correct = option == currentCard.pt
                                    isCorrect = correct
                                    if (correct) score++
                                }
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = VibrantTextPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (selectedOption != null) {
                    Button(
                        onClick = {
                            selectedOption = null
                            isCorrect = null
                            currentIndex++
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantPrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (currentIndex < cards.size - 1) "Próxima Pergunta →" else "Ver Resultado Final",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
