package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val role: String, // "user" or "assistant"
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val mood: String = "Excited",
  val isAudio: Boolean = false,
  val imageUrl: String? = null
)

@Entity(tableName = "memories")
data class MemoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val content: String,
  val category: String = "General", // "Preference", "Fact", "Study", "Emotion"
  val timestamp: Long = System.currentTimeMillis(),
  val isPinned: Boolean = false
)

@Dao
interface ChatDao {
  @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
  fun getAllMessages(): Flow<List<ChatMessageEntity>>

  @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT 30")
  suspend fun getRecentMessages(): List<ChatMessageEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: ChatMessageEntity): Long

  @Query("DELETE FROM chat_messages")
  suspend fun clearAll()
}

@Dao
interface MemoryDao {
  @Query("SELECT * FROM memories ORDER BY isPinned DESC, timestamp DESC")
  fun getAllMemories(): Flow<List<MemoryEntity>>

  @Query("SELECT * FROM memories WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
  fun searchMemories(query: String): Flow<List<MemoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMemory(memory: MemoryEntity): Long

  @Delete
  suspend fun deleteMemory(memory: MemoryEntity)

  @Query("DELETE FROM memories")
  suspend fun clearAll()
}

@Database(entities = [ChatMessageEntity::class, MemoryEntity::class], version = 1, exportSchema = false)
abstract class HayatDatabase : RoomDatabase() {
  abstract fun chatDao(): ChatDao
  abstract fun memoryDao(): MemoryDao
}
