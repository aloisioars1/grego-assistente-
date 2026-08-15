package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.Flashcard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Flashcard::class, ChatMessage::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flashcardDao(): FlashcardDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chiste_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.flashcardDao(), database.chatMessageDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(flashcardDao: FlashcardDao, chatMessageDao: ChatMessageDao) {
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
