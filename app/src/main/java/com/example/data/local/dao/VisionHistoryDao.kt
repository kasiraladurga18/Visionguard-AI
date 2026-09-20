package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.VisionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VisionHistoryDao {
    @Query("SELECT * FROM vision_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<VisionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(entity: VisionHistoryEntity)

    @Query("DELETE FROM vision_history WHERE id = :id")
    suspend fun deleteHistory(id: String)

    @Query("DELETE FROM vision_history")
    suspend fun clearAll()
}
