package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pt: String,
    val el: String,
    val type: String = "geral",
    val emoji: String = "⚡",
    val level: String = "A1",
    val frasePt: String = "",
    val fraseEl: String = "",
    val isMastered: Boolean = false,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val genderColorHex: Long
        get() = when {
            el.trim().startsWith("ο ") || el.trim().startsWith("Ο ") -> 0xFF2563EB // Blue (Masculine)
            el.trim().startsWith("η ") || el.trim().startsWith("Η ") -> 0xFFEC4899 // Pink (Feminine)
            el.trim().startsWith("το ") || el.trim().startsWith("Το ") -> 0xFF16A34A // Green (Neuter)
            else -> 0xFF7C3AED // Purple (Verb/Other)
        }

    val genderLabel: String
        get() = when {
            el.trim().startsWith("ο ") || el.trim().startsWith("Ο ") -> "Masculino (ο)"
            el.trim().startsWith("η ") || el.trim().startsWith("Η ") -> "Feminino (η)"
            el.trim().startsWith("το ") || el.trim().startsWith("Το ") -> "Neutro (το)"
            else -> "Outro / Verbo"
        }
}

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val role: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
