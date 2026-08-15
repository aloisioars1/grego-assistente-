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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ChatMessage
import com.example.ui.theme.VibrantAccentPurple
import com.example.ui.theme.VibrantBackground
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantBorderLight
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

@Composable
fun LinaAssistantModal(
    level: String,
    messages: List<ChatMessage>,
    chatInput: String,
    isLoading: Boolean,
    onInputChange: (String) -> Unit,
    onSendMessage: (String?) -> Unit,
    onClearChat: () -> Unit,
    onSpeak: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
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
                .testTag("lina_modal_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
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
                            Text(
                                text = "🤖",
                                fontSize = 20.sp
                            )
                        }
                        Column {
                            Text(
                                text = "Lina • Tutora Grego",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = VibrantTextPrimary
                            )
                            Text(
                                text = "FLASH 3.5 • NÍVEL $level",
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
                        IconButton(
                            onClick = onClearChat,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Limpar histórico",
                                tint = VibrantTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(VibrantSecondaryContainer, CircleShape)
                                .clickable { onDismiss() }
                                .testTag("close_lina_button"),
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

                // Quick Action Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VibrantSurface)
                        .border(width = 1.dp, color = VibrantBorderLight)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val presets = listOf(
                        "📍 Frases" to "Frases do dia a dia com vocabulário atual",
                        "💬 Diálogo" to "Diálogo prático em grego moderno",
                        "📖 História" to "Micro-história para leitura guiada",
                        "🧠 Quiz" to "Quiz com 3 perguntas",
                        "🗓️ Plano 7d" to "Plano de estudo de 7 dias"
                    )

                    presets.forEach { (title, _) ->
                        NeoChip(
                            text = title,
                            onClick = { onSendMessage(title) },
                            selectedBg = VibrantPrimary,
                            unselectedBg = VibrantSecondaryContainer,
                            testTag = "chip_${title.replace(" ", "_")}"
                        )
                    }
                }

                // Messages Container
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatBubble(
                            message = msg,
                            onSpeak = onSpeak,
                            onCopy = { clipboardManager.setText(AnnotatedString(msg.text)) }
                        )
                    }

                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .background(VibrantSurface, RoundedCornerShape(18.dp))
                                    .border(1.dp, VibrantBorderLight, RoundedCornerShape(18.dp))
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = VibrantPrimary
                                    )
                                    Text(
                                        text = "⚡ Gerando com Flash 3.5...",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = VibrantTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Input Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VibrantSurface)
                        .border(1.dp, VibrantBorderLight)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NeoInputField(
                            value = chatInput,
                            onValueChange = onInputChange,
                            placeholder = "Pergunte pra Lina em grego ou português...",
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { onSendMessage(null) }),
                            modifier = Modifier.weight(1f),
                            testTag = "lina_input"
                        )

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (chatInput.isNotBlank() && !isLoading) VibrantPrimary else Color.LightGray,
                                    CircleShape
                                )
                                .clickable(enabled = chatInput.isNotBlank() && !isLoading) { onSendMessage(null) }
                                .testTag("lina_send_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    onSpeak: (String) -> Unit,
    onCopy: () -> Unit
) {
    val isUser = message.role.equals("user", ignoreCase = true)
    val shape = RoundedCornerShape(20.dp)
    val bgColor = if (isUser) VibrantPrimaryContainer else VibrantSurface

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .offset(y = 2.dp)
        ) {
            // Bubble body
            Column(
                modifier = Modifier
                    .background(bgColor, shape)
                    .border(1.dp, if (isUser) VibrantPrimary.copy(alpha = 0.3f) else VibrantBorderLight, shape)
                    .clip(shape)
                    .padding(14.dp)
            ) {
                Text(
                    text = message.text,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = if (isUser) VibrantOnPrimaryContainer else VibrantTextPrimary
                )

                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copiar texto",
                                tint = VibrantTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = { onSpeak(message.text) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Ouvir texto grego",
                                tint = VibrantPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
