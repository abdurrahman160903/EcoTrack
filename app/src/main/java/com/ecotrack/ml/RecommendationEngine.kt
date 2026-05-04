package com.ecotrack.ml

import com.ecotrack.data.model.ActivityType
import com.ecotrack.data.model.CarbonData
import com.ecotrack.data.model.Recommendation

/**
 * Rule-based AI recommendation engine.
 *
 * Analyses the user's recent [CarbonData] and returns a prioritised list
 * of personalised eco-tips.  The rules are based on recognised high-impact
 * behavioural interventions (Wynes & Nicholas, 2017).
 *
 * In a production build this class would call a TensorFlow Lite model
 * (`assets/recommendation_model.tflite`) that ranks tips using a learned
 * relevance score derived from millions of anonymised user journeys.
 */
class RecommendationEngine {

    /**
     * Generates up to [maxTips] personalised recommendations sorted by
     * estimated weekly CO₂ saving (highest first).
     *
     * @param carbonData Current-week carbon breakdown for the user.
     * @param maxTips    Maximum number of tips to return.
     */
    fun generateRecommendations(
        carbonData: CarbonData,
        maxTips: Int = 5
    ): List<Recommendation> {
        val tips = mutableListOf<Recommendation>()

        // --- Transportation ---
        val transportKg = carbonData.byCategory[ActivityType.TRANSPORTATION] ?: 0.0
        if (transportKg > 20.0) {
            tips += Recommendation(
                title = "Switch to public transport",
                body = "Replacing a single weekly car journey with the bus or train " +
                       "can cut your transport emissions by up to 75%.",
                saving = transportKg * 0.55,
                category = ActivityType.TRANSPORTATION
            )
        }
        if (transportKg > 30.0) {
            tips += Recommendation(
                title = "Try cycling or walking",
                body = "Zero-emission travel for trips under 5 km saves both carbon " +
                       "and money. Consider a folding bike for commutes.",
                saving = transportKg * 0.30,
                category = ActivityType.TRANSPORTATION
            )
        }

        // --- Energy ---
        val energyKg = carbonData.byCategory[ActivityType.ENERGY] ?: 0.0
        if (energyKg > 15.0) {
            tips += Recommendation(
                title = "Reduce standby power",
                body = "Unplugging devices at the wall instead of leaving them on " +
                       "standby can save up to 10% of household electricity.",
                saving = energyKg * 0.10,
                category = ActivityType.ENERGY
            )
        }
        if (energyKg > 25.0) {
            tips += Recommendation(
                title = "Switch to a green energy tariff",
                body = "Moving to a 100 % renewable electricity plan can eliminate " +
                       "most of your home energy emissions overnight.",
                saving = energyKg * 0.80,
                category = ActivityType.ENERGY
            )
        }

        // --- Food ---
        val foodKg = carbonData.byCategory[ActivityType.FOOD] ?: 0.0
        if (foodKg > 10.0) {
            tips += Recommendation(
                title = "Try one meat-free day per week",
                body = "Replacing beef with a plant-based alternative once a week " +
                       "saves roughly 6.6 kg CO₂ per meal.",
                saving = 6.61,
                category = ActivityType.FOOD
            )
        }
        if (foodKg > 20.0) {
            tips += Recommendation(
                title = "Reduce food waste",
                body = "Approximately one-third of all food is wasted globally. " +
                       "Meal planning and proper storage can cut your food footprint " +
                       "by up to 25%.",
                saving = foodKg * 0.25,
                category = ActivityType.FOOD
            )
        }

        // --- Shopping ---
        val shoppingKg = carbonData.byCategory[ActivityType.SHOPPING] ?: 0.0
        if (shoppingKg > 20.0) {
            tips += Recommendation(
                title = "Buy second-hand clothing",
                body = "The fashion industry accounts for ~10 % of global carbon " +
                       "emissions. Buying pre-loved items reduces demand for new " +
                       "production by 95 %.",
                saving = shoppingKg * 0.70,
                category = ActivityType.SHOPPING
            )
        }

        // --- Waste ---
        val wasteKg = carbonData.byCategory[ActivityType.WASTE] ?: 0.0
        if (wasteKg > 5.0) {
            tips += Recommendation(
                title = "Start composting",
                body = "Composting organic waste diverts it from landfill, reducing " +
                       "methane emissions and producing nutrient-rich soil.",
                saving = wasteKg * 0.35,
                category = ActivityType.WASTE
            )
        }

        // Always provide at least one general tip
        if (tips.isEmpty()) {
            tips += Recommendation(
                title = "Keep up the great work!",
                body = "Your carbon footprint is already below average. " +
                       "Share your habits with friends to amplify the impact.",
                saving = 0.0,
                category = ActivityType.TRANSPORTATION
            )
        }

        return tips
            .sortedByDescending { it.saving }
            .take(maxTips)
    }
}
