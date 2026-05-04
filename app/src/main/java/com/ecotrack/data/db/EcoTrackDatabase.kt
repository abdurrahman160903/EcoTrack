package com.ecotrack.data.db

import android.content.Context
import androidx.room.*
import com.ecotrack.data.db.dao.AchievementDao
import com.ecotrack.data.db.dao.EcoActivityDao
import com.ecotrack.data.db.dao.UserProfileDao
import com.ecotrack.data.db.entity.AchievementEntity
import com.ecotrack.data.db.entity.EcoActivityEntity
import com.ecotrack.data.db.entity.UserProfileEntity
import com.ecotrack.data.model.ActivityType

/**
 * Central Room database for EcoTrack.
 *
 * Stores eco-activity logs, achievements, and the local user profile.
 * A type converter is registered so that [ActivityType] enum values are
 * persisted as their string names.
 */
@Database(
    entities = [EcoActivityEntity::class, AchievementEntity::class, UserProfileEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(EcoTrackDatabase.Converters::class)
abstract class EcoTrackDatabase : RoomDatabase() {

    abstract fun ecoActivityDao(): EcoActivityDao
    abstract fun achievementDao(): AchievementDao
    abstract fun userProfileDao(): UserProfileDao

    class Converters {
        @TypeConverter
        fun fromActivityType(type: ActivityType): String = type.name

        @TypeConverter
        fun toActivityType(name: String): ActivityType = ActivityType.valueOf(name)
    }

    companion object {
        @Volatile
        private var INSTANCE: EcoTrackDatabase? = null

        fun getInstance(context: Context): EcoTrackDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    EcoTrackDatabase::class.java,
                    "ecotrack.db"
                ).build().also { INSTANCE = it }
            }
    }
}
