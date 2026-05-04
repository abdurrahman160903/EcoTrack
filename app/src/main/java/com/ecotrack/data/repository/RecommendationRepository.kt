package com.ecotrack.data.repository

import com.ecotrack.ml.RecommendationEngine
import com.ecotrack.data.model.CarbonData
import com.ecotrack.data.model.Recommendation

/**
 * Repository that wraps [RecommendationEngine] and caches the last result
 * so that repeated UI observations do not re-run the scoring algorithm.
 */
class RecommendationRepository(
    private val engine: RecommendationEngine = RecommendationEngine()
) {

    private var cachedInput: CarbonData? = null
    private var cachedResult: List<Recommendation> = emptyList()

    /**
     * Returns recommendations for the given [carbonData], using a cached
     * result when the input has not changed.
     */
    fun getRecommendations(carbonData: CarbonData, maxTips: Int = 5): List<Recommendation> {
        if (carbonData == cachedInput) return cachedResult
        val result = engine.generateRecommendations(carbonData, maxTips)
        cachedInput = carbonData
        cachedResult = result
        return result
    }
}
