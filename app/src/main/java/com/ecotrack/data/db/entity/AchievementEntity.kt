package com.ecotrack.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for gamification achievements unlocked by the user.
 */
@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,                         // e.g. "first_log", "week_streak"

    val title: String,
    val description: String,

    @ColumnInfo(name = "is_unlocked")
    val isUnlocked: Boolean = false,

    @ColumnInfo(name = "unlocked_at")
    val unlockedAt: Long? = null            // UNIX epoch ms
)
