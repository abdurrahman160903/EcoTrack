package com.ecotrack.data.repository

import com.ecotrack.data.db.dao.AchievementDao
import com.ecotrack.data.db.dao.EcoActivityDao
import com.ecotrack.data.db.dao.UserProfileDao
import com.ecotrack.data.db.entity.AchievementEntity
import com.ecotrack.data.db.entity.EcoActivityEntity
import com.ecotrack.data.db.entity.UserProfileEntity
import com.ecotrack.data.model.ActivityType
import com.ecotrack.data.model.CarbonData
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

/**
 * Single source of truth for eco-activity and user-profile data.
 *
 * All database writes are suspend functions; reads are exposed as [Flow]
 * so the UI layer reacts automatically to data changes.
 */
class EcoRepository(
    private val activityDao: EcoActivityDao,
    private val achievementDao: AchievementDao,
    private val profileDao: UserProfileDao
) {

    // ── Activity ──────────────────────────────────────────────────────────

    fun getAllActivities(): Flow<List<EcoActivityEntity>> =
        activityDao.getAllActivities()

    fun getRecentActivities(limit: Int = 5): Flow<List<EcoActivityEntity>> =
        activityDao.getRecentActivities(limit)

    suspend fun logActivity(activity: EcoActivityEntity): Long =
        activityDao.insert(activity)

    suspend fun deleteActivity(activity: EcoActivityEntity) =
        activityDao.delete(activity)

    // ── Carbon aggregation ────────────────────────────────────────────────

    /**
     * Builds a [CarbonData] snapshot covering the last [days] days.
     */
    suspend fun getCarbonDataForPeriod(days: Int = 7): CarbonData {
        val fromMs = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days.toLong())

        val byCategory = ActivityType.entries.associateWith { type ->
            activityDao.getCarbonByTypeSince(type, fromMs) ?: 0.0
        }
        val total = byCategory.values.sum()

        // Build a 7-day trend (one bucket per day, newest last).
        // Sum all activity types for the 24-hour window of each day.
        val weeklyTrend = (6 downTo 0).map { daysAgo ->
            val dayStart = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(daysAgo.toLong())
            val dayEnd   = dayStart + TimeUnit.DAYS.toMillis(1)
            ActivityType.entries.sumOf { type ->
                activityDao.getCarbonBetween(type, dayStart, dayEnd) ?: 0.0
            }
        }

        return CarbonData(
            totalKgCo2 = total,
            byCategory = byCategory,
            weeklyTrend = weeklyTrend
        )
    }

    // ── Achievements ──────────────────────────────────────────────────────

    fun getAllAchievements(): Flow<List<AchievementEntity>> =
        achievementDao.getAllAchievements()

    suspend fun unlockAchievement(id: String) {
        val achievement = achievementDao.getById(id) ?: return
        if (!achievement.isUnlocked) {
            achievementDao.update(
                achievement.copy(
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun seedAchievements() {
        val defaults = listOf(
            AchievementEntity("first_log",    "First Step",      "Log your first eco-activity."),
            AchievementEntity("week_streak",  "Week Warrior",    "Log activities 7 days in a row."),
            AchievementEntity("carbon_saver", "Carbon Saver",    "Save 10 kg CO₂ in a week."),
            AchievementEntity("veggie_week",  "Plant Powered",   "Log 7 vegetarian meals in a week."),
            AchievementEntity("green_commute","Green Commuter",  "Use public transport 5 times."),
            AchievementEntity("recycler",     "Recycling Hero",  "Log recycling 10 times.")
        )
        achievementDao.insertAll(defaults)
    }

    // ── User profile ──────────────────────────────────────────────────────

    fun getProfile(): Flow<UserProfileEntity?> = profileDao.getProfile()

    suspend fun saveProfile(profile: UserProfileEntity) = profileDao.update(profile)

    suspend fun seedProfile() {
        if (profileDao.getProfileOnce() == null) {
            profileDao.insert(UserProfileEntity())
        }
    }
}
