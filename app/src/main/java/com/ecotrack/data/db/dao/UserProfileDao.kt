package com.ecotrack.data.db.dao

import androidx.room.*
import com.ecotrack.data.db.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [UserProfileEntity].
 */
@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(profile: UserProfileEntity)

    @Update
    suspend fun update(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfileOnce(): UserProfileEntity?
}
