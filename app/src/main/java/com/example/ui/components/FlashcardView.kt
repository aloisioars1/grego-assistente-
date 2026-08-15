package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Flashcard
import com.example.util.GreekTransliterationHelper
import com.example.ui.theme.GenderFemininePink
import com.example.ui.theme.GenderMasculineBlue
import com.example.ui.theme.GenderNeuterGreen
import com.example.ui.theme.GenderOtherPurple
import com.example.ui.theme.VibrantAccentPurple
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantBorderLight
import com.example.ui.theme.VibrantOnPrimaryContainer
import com.example.ui.theme.VibrantPrimary
import com.example.ui.theme.VibrantPrimaryContainer
import com.example.ui.theme.VibrantSecondaryContainer
import com.example.ui.theme.VibrantShadow
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceContainerHigh
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.ui.theme.VibrantTertiaryContainer
import com.example.ui.theme.VibrantTertiaryText
import com.example.ui.theme.VibrantTextDark
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary

@Composable
fun FlashcardView(
    card: Flashcard,
    currentIndex: Int,
    totalCards: Int,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onShuffle: () -> Unit,
    onToggleMastered: () -> Unit,
    onSpeak: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "cardFlipAnimation"
    )

    val cardShape = RoundedCornerShape(32.dp)
    var dragOffset by remember { mutableFloatStateOf(0f) }

    // Color computation based on Greek grammatical article
    val genderColor = when {
        card.el.trim().startsWith("ο ", ignoreCase = true) -> GenderMasculineBlue
        card.el.trim().startsWith("η ", ignoreCase = true) -> GenderFemininePink
        card.el.trim().startsWith("το ", ignoreCase = true) -> GenderNeuterGreen
        else -> GenderOtherPurple
    }

    Box(
        modifier = modifier
            .testTag("flashcard_container")
            .fillMaxWidth()
            .height(390.dp)
            .pointerInput(card.id) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragOffset < -60f) {
                            onNext()
                        } else if (dragOffset > 60f) {
                            onPrev()
                        }
                        dragOffset = 0f
                    },
                    onDragCancel = { dragOffset = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffset += dragAmount
                    }
                )
            }
    ) {
        // Soft Shadow Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 2.dp, y = 6.dp)
                .background(VibrantShadow, cardShape)
        )

        // 3D Card Content with flip rotation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 14f * density
                }
                .background(if (rotation > 90f) VibrantSurface else VibrantSurface, cardShape)
                .border(2.dp, VibrantBorder, cardShape)
                .clip(cardShape)
        ) {
            if (rotation <= 90f) {
                // FRONT SIDE (Greek focus)
                CardFrontContent(
                    card = card,
                    genderColor = genderColor,
                    currentIndex = currentIndex,
                    totalCards = totalCards,
                    onFlip = onFlip,
                    onSpeak = onSpeak
                )
            } else {
                // BACK SIDE (Portuguese & phrases focus)
                CardBackContent(
                    card = card,
                    genderColor = genderColor,
                    currentIndex = currentIndex,
                    totalCards = totalCards,
                    onFlip = onFlip,
                    onNext = onNext,
                    onToggleMastered = onToggleMastered,
                    onSpeak = onSpeak,
                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                )
            }
        }
    }
}

@Composable
private fun CardFrontContent(
    card: Flashcard,
    genderColor: Color,
    currentIndex: Int,
    totalCards: Int,
    onFlip: () -> Unit,
    onSpeak: (String, Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Topic/Type Badge
                Box(
                    modifier = Modifier
                        .background(VibrantTertiaryContainer, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = card.type.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = VibrantTertiaryText
                    )
                }

                // Level Badge
                Box(
                    modifier = Modifier
                        .background(VibrantSecondaryContainer, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = card.level,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = VibrantTextDark
                    )
                }
            }

            // Index / Progress Indicator
            Text(
                text = "Card ${currentIndex + 1} of $totalCards",
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = VibrantTextSecondary
            )
        }

        // Center: Emoji + Greek Word + Transliteration + Article
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = card.emoji,
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.clickable { onSpeak(card.el, false) }
            ) {
                Text(
                    text = card.el,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = VibrantPrimary,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(VibrantSecondaryContainer, CircleShape)
                        .clickable { onSpeak(card.el, false) }
                        .testTag("word_speaker_front"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Ouvir ${card.el}",
                        tint = VibrantPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            val translit = GreekTransliterationHelper.transliterate(card.el)
            if (translit.isNotBlank()) {
                Text(
                    text = "($translit)",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = VibrantTextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = card.genderLabel,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = VibrantTextSecondary.copy(alpha = 0.8f)
            )
        }

        // Bottom Action Bar: Reveal Translation & Audio
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(VibrantSurfaceContainerHigh)
                .border(1.dp, VibrantBorderLight, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Audio button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(VibrantSecondaryContainer, RoundedCornerShape(16.dp))
                        .clickable { onSpeak(card.el, false) }
                        .testTag("audio_front_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Ouvir pronúncia",
                        tint = VibrantTextDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Reveal translation CTA
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(VibrantPrimary, RoundedCornerShape(16.dp))
                        .clickable { onFlip() }
                        .testTag("flip_card_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Reveal Translation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "→",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardBackContent(
    card: Flashcard,
    genderColor: Color,
    currentIndex: Int,
    totalCards: Int,
    onFlip: () -> Unit,
    onNext: () -> Unit,
    onToggleMastered: () -> Unit,
    onSpeak: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(VibrantTertiaryContainer, RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = card.level,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = VibrantTertiaryText
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mastered indicator
                IconButton(
                    onClick = onToggleMastered,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (card.isMastered) Icons.Default.CheckCircle else Icons.Default.BookmarkBorder,
                        contentDescription = "Marcar como dominado",
                        tint = if (card.isMastered) GenderNeuterGreen else VibrantTextSecondary
                    )
                }

                // Audio Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(VibrantSecondaryContainer, CircleShape)
                        .clickable { onSpeak(card.el, false) }
                        .testTag("audio_back_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Ouvir",
                        tint = VibrantTextDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Center Content: Portuguese Translation & Greek Original & Phrase Example
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = card.pt.uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = VibrantTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.clickable { onSpeak(card.el, false) }
            ) {
                Text(
                    text = card.el,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = VibrantPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(VibrantSecondaryContainer, CircleShape)
                        .clickable { onSpeak(card.el, false) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Ouvir ${card.el}",
                        tint = VibrantPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            val backTranslit = GreekTransliterationHelper.transliterate(card.el)
            if (backTranslit.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "($backTranslit)",
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = VibrantTextSecondary
                )
            }

            if (card.frasePt.isNotBlank() || card.fraseEl.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VibrantSurfaceVariant, RoundedCornerShape(16.dp))
                        .border(1.dp, VibrantBorderLight, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (card.fraseEl.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = card.fraseEl,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = VibrantTextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { onSpeak(card.fraseEl, false) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Ouvir frase",
                                        tint = VibrantTextDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        if (card.frasePt.isNotBlank()) {
                            Text(
                                text = "💬 " + card.frasePt,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = VibrantTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Bottom Action Bar: Back to front & Next
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(VibrantSurfaceContainerHigh)
                .border(1.dp, VibrantBorderLight, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(VibrantSecondaryContainer, RoundedCornerShape(16.dp))
                        .clickable { onFlip() }
                        .testTag("flip_back_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Voltar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = VibrantTextDark
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1.4f)
                        .height(48.dp)
                        .background(VibrantPrimary, RoundedCornerShape(16.dp))
                        .clickable { onNext() }
                        .testTag("next_card_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Próximo ⚡",
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
