package com.ecotrack.data.model

/**
 * Represents the broad category of an eco-activity logged by the user.
 */
enum class ActivityType(val displayName: String, val iconRes: Int) {
    TRANSPORTATION("Transportation", 0),
    ENERGY("Energy Usage", 0),
    FOOD("Food & Diet", 0),
    SHOPPING("Shopping", 0),
    WASTE("Waste & Recycling", 0)
}
