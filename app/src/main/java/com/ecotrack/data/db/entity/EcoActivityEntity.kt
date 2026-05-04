package com.ecotrack.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ecotrack.data.model.ActivityType

/**
 * Room entity that persists a single eco-activity log entry.
 */
@Entity(tableName = "eco_activities")
data class EcoActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "activity_type")
    val activityType: ActivityType,

    /** Human-readable description (e.g. "Drove 15 km to work"). */
    val description: String,

    /** Carbon emitted / saved, in kg CO₂-equivalent. Negative means saving. */
    @ColumnInfo(name = "carbon_kg")
    val carbonKg: Double,

    /** UNIX epoch milliseconds when the activity was logged. */
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    /** Quantitative value supplied by the user (distance, kWh, servings, etc.). */
    val quantity: Double = 1.0,

    /** Unit label for [quantity] (e.g. "km", "kWh", "meals"). */
    val unit: String = ""
)
