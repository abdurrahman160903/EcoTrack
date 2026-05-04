package com.ecotrack

import com.ecotrack.data.model.ActivityType
import com.ecotrack.data.model.CarbonData
import com.ecotrack.ml.RecommendationEngine
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [RecommendationEngine].
 *
 * Covers rule triggering, ordering by saving, and edge cases such as an
 * empty footprint or a very high footprint in one category.
 */
class RecommendationEngineTest {

    private lateinit var engine: RecommendationEngine

    @Before
    fun setUp() {
        engine = RecommendationEngine()
    }

    private fun carbonData(
        transport: Double = 0.0,
        energy: Double    = 0.0,
        food: Double      = 0.0,
        shopping: Double  = 0.0,
        waste: Double     = 0.0
    ) = CarbonData(
        totalKgCo2 = transport + energy + food + shopping + waste,
        byCategory = mapOf(
            ActivityType.TRANSPORTATION to transport,
            ActivityType.ENERGY          to energy,
            ActivityType.FOOD            to food,
            ActivityType.SHOPPING        to shopping,
            ActivityType.WASTE           to waste
        ),
        weeklyTrend = List(7) { 0.0 }
    )

    @Test
    fun `zero footprint returns at least one positive tip`() {
        val tips = engine.generateRecommendations(carbonData())
        assertTrue("Should always return at least one tip", tips.isNotEmpty())
    }

    @Test
    fun `high transport footprint triggers public-transport tip`() {
        val tips = engine.generateRecommendations(carbonData(transport = 50.0))
        val titles = tips.map { it.title }
        assertTrue(titles.any { it.contains("public transport", ignoreCase = true) })
    }

    @Test
    fun `high energy footprint triggers energy tip`() {
        val tips = engine.generateRecommendations(carbonData(energy = 30.0))
        assertTrue(tips.any { it.category == ActivityType.ENERGY })
    }

    @Test
    fun `high food footprint triggers meat-free tip`() {
        val tips = engine.generateRecommendations(carbonData(food = 25.0))
        assertTrue(tips.any { it.category == ActivityType.FOOD })
    }

    @Test
    fun `results are sorted by saving descending`() {
        val tips = engine.generateRecommendations(
            carbonData(transport = 60.0, energy = 40.0, food = 30.0, shopping = 25.0, waste = 10.0)
        )
        for (i in 0 until tips.size - 1) {
            assertTrue(
                "Tips should be sorted by saving descending",
                tips[i].saving >= tips[i + 1].saving
            )
        }
    }

    @Test
    fun `respects maxTips parameter`() {
        val tips = engine.generateRecommendations(
            carbonData(transport = 60.0, energy = 40.0, food = 30.0, shopping = 25.0, waste = 10.0),
            maxTips = 2
        )
        assertTrue("Should return at most maxTips results", tips.size <= 2)
    }

    @Test
    fun `recycling tip triggered by high waste footprint`() {
        val tips = engine.generateRecommendations(carbonData(waste = 10.0))
        assertTrue(tips.any { it.category == ActivityType.WASTE })
    }
}
