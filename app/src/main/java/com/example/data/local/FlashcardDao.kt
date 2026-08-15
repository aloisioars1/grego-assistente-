package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChatMessage
import com.example.data.model.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards ORDER BY id DESC")
    fun getAllCards(): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE level = :level ORDER BY id DESC")
    fun getCardsByLevel(level: String): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Flashcard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<Flashcard>)

    @Update
    suspend fun updateCard(card: Flashcard)

    @Delete
    suspend fun deleteCard(card: Flashcard)

    @Query("DELETE FROM flashcards WHERE id = :id")
    suspend fun deleteCardById(id: Long)

    @Query("SELECT COUNT(*) FROM flashcards")
    suspend fun getCount(): Int
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}
