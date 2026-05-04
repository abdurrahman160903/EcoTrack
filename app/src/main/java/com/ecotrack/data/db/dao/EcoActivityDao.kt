package com.ecotrack.data.db.dao

import androidx.room.*
import com.ecotrack.data.db.entity.EcoActivityEntity
import com.ecotrack.data.model.ActivityType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [EcoActivityEntity].
 */
@Dao
interface EcoActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: EcoActivityEntity): Long

    @Update
    suspend fun update(activity: EcoActivityEntity)

    @Delete
    suspend fun delete(activity: EcoActivityEntity)

    @Query("SELECT * FROM eco_activities ORDER BY timestamp DESC")
    fun getAllActivities(): Flow<List<EcoActivityEntity>>

    @Query("SELECT * FROM eco_activities WHERE timestamp >= :fromMs ORDER BY timestamp DESC")
    fun getActivitiesSince(fromMs: Long): Flow<List<EcoActivityEntity>>

    @Query("SELECT * FROM eco_activities WHERE activity_type = :type ORDER BY timestamp DESC")
    fun getActivitiesByType(type: ActivityType): Flow<List<EcoActivityEntity>>

    @Query("SELECT SUM(carbon_kg) FROM eco_activities WHERE timestamp >= :fromMs")
    suspend fun getTotalCarbonSince(fromMs: Long): Double?

    @Query("SELECT SUM(carbon_kg) FROM eco_activities WHERE activity_type = :type AND timestamp >= :fromMs")
    suspend fun getCarbonByTypeSince(type: ActivityType, fromMs: Long): Double?

    @Query("SELECT SUM(carbon_kg) FROM eco_activities WHERE activity_type = :type AND timestamp >= :fromMs AND timestamp < :toMs")
    suspend fun getCarbonBetween(type: ActivityType, fromMs: Long, toMs: Long): Double?

    @Query("SELECT * FROM eco_activities ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentActivities(limit: Int = 5): Flow<List<EcoActivityEntity>>
}
