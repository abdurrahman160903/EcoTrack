package com.ecotrack.data.db.dao

import androidx.room.*
import com.ecotrack.data.db.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [AchievementEntity].
 */
@Dao
interface AchievementDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Update
    suspend fun update(achievement: AchievementEntity)

    @Query("SELECT * FROM achievements ORDER BY is_unlocked DESC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE id = :id")
    suspend fun getById(id: String): AchievementEntity?

    @Query("SELECT COUNT(*) FROM achievements WHERE is_unlocked = 1")
    fun getUnlockedCount(): Flow<Int>
}
