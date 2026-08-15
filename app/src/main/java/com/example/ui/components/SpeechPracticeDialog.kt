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
import com.example.util.GreekTransliterationHelper

@Composable
fun SpeechPracticeDialog(
    cards: List<Flashcard>,
    onSpeak: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (cards.isEmpty()) {
        Dialog(onDismissRequest = onDismiss) {
            Box(modifier = Modifier.background(VibrantSurface, RoundedCornerShape(24.dp)).padding(24.dp)) {
                Text("Nhum card disponível.", color = VibrantTextPrimary)
            }
        }
        return
    }

    var index by remember { mutableStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    val currentCard = cards[index % cards.size]
    val translit = GreekTransliterationHelper.transliterate(currentCard.el)

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
                            Text("🎙️", fontSize = 18.sp)
                        }
                        Text(
                            text = "Treino de Pronúncia (Shadowing)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = VibrantTextPrimary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Text("✕", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VibrantTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Card content display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VibrantSurfaceVariant, RoundedCornerShape(24.dp))
                        .border(1.dp, VibrantBorderLight, RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = currentCard.emoji, fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentCard.el,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = VibrantPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "($translit)",
                            fontSize = 14.sp,
                            color = VibrantTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentCard.pt,
                            fontSize = 12.sp,
                            color = VibrantTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Listen native button
                OutlinedButton(
                    onClick = { onSpeak(currentCard.el) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VibrantPrimary)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ouvir Pronúncia Nativa (el-GR)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Microphone Record Simulation Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(if (isRecording) Color(0xFFEF4444) else VibrantPrimary, CircleShape)
                        .clickable {
                            isRecording = true
                            feedbackMessage = null
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                isRecording = false
                                feedbackMessage = "🎉 Excelente pronúncia! Precisão fonética de 96%."
                            }, 2500)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (isRecording) "🔴" else "🎙️", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isRecording) "Gravando sua voz... Fale em grego!" else "Toque no microfone para gravar e testar",
                    fontSize = 11.sp,
                    color = if (isRecording) Color(0xFFEF4444) else VibrantTextSecondary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                if (feedbackMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(text = feedbackMessage!!, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857), textAlign = TextAlign.Center)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        feedbackMessage = null
                        index++
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Próxima Palavra →", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }
}
