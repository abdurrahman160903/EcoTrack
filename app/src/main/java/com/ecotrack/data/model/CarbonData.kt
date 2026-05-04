package com.ecotrack.data.model

/**
 * Aggregated carbon footprint data used by the analytics screen.
 *
 * @property totalKgCo2 Total carbon dioxide equivalent (kg) in the period.
 * @property byCategory Breakdown of emissions by [ActivityType].
 * @property weeklyTrend List of daily totals for the last 7 days (index 0 = oldest).
 */
data class CarbonData(
    val totalKgCo2: Double,
    val byCategory: Map<ActivityType, Double>,
    val weeklyTrend: List<Double>
)
