package com.ecotrack.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity storing the single local user's profile information.
 * The primary key is fixed to 1 so there is always at most one row.
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,

    val name: String = "Eco Warrior",

    @ColumnInfo(name = "weekly_goal_kg")
    val weeklyGoalKg: Double = 50.0,        // Target total weekly CO₂ (kg)

    @ColumnInfo(name = "total_points")
    val totalPoints: Int = 0,

    @ColumnInfo(name = "streak_days")
    val streakDays: Int = 0,

    @ColumnInfo(name = "last_log_date")
    val lastLogDate: Long? = null
)
