package com.ecotrack

import com.ecotrack.data.model.ActivityType
import com.ecotrack.ml.CarbonEstimator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [CarbonEstimator].
 *
 * Verifies that emission factors and quantity multiplication produce the
 * expected carbon values for every [ActivityType].
 */
class CarbonEstimatorTest {

    private lateinit var estimator: CarbonEstimator

    @Before
    fun setUp() {
        estimator = CarbonEstimator()
    }

    // ── Transportation ────────────────────────────────────────────────────

    @Test
    fun `car travel multiplies factor by distance`() {
        val result = estimator.estimate(ActivityType.TRANSPORTATION, "car", 100.0)
        assertEquals(21.0, result, 0.001)
    }

    @Test
    fun `bus travel emits less than car for same distance`() {
        val car = estimator.estimate(ActivityType.TRANSPORTATION, "car", 50.0)
        val bus = estimator.estimate(ActivityType.TRANSPORTATION, "bus", 50.0)
        assertTrue("Bus should emit less than car", bus < car)
    }

    @Test
    fun `bicycle emits zero carbon`() {
        val result = estimator.estimate(ActivityType.TRANSPORTATION, "bicycle", 20.0)
        assertEquals(0.0, result, 0.0)
    }

    @Test
    fun `unknown transport sub-type falls back to average`() {
        val result = estimator.estimate(ActivityType.TRANSPORTATION, "hovercraft", 10.0)
        assertEquals(1.70, result, 0.001)
    }

    // ── Energy ────────────────────────────────────────────────────────────

    @Test
    fun `electricity emission is correct for 10 kWh`() {
        val result = estimator.estimate(ActivityType.ENERGY, "electricity", 10.0)
        assertEquals(2.33, result, 0.001)
    }

    @Test
    fun `solar energy emits zero`() {
        val result = estimator.estimate(ActivityType.ENERGY, "solar", 100.0)
        assertEquals(0.0, result, 0.0)
    }

    // ── Food ─────────────────────────────────────────────────────────────

    @Test
    fun `beef meal has highest food emission`() {
        val beef = estimator.estimate(ActivityType.FOOD, "beef", 1.0)
        val vegan = estimator.estimate(ActivityType.FOOD, "vegan", 1.0)
        assertTrue("Beef should emit more than vegan meal", beef > vegan)
    }

    @Test
    fun `vegetarian meal emission equals vegan`() {
        val vegan = estimator.estimate(ActivityType.FOOD, "vegan", 1.0)
        val veg   = estimator.estimate(ActivityType.FOOD, "vegetarian", 1.0)
        assertEquals(vegan, veg, 0.0)
    }

    // ── Shopping ─────────────────────────────────────────────────────────

    @Test
    fun `electronics purchase has high emission`() {
        val result = estimator.estimate(ActivityType.SHOPPING, "electronics", 1.0)
        assertTrue(result > 50.0)
    }

    // ── Waste ─────────────────────────────────────────────────────────────

    @Test
    fun `recycling yields negative (saving) emission`() {
        val result = estimator.estimate(ActivityType.WASTE, "recycling", 5.0)
        assertTrue("Recycling should have negative carbon impact", result < 0.0)
    }

    @Test
    fun `estimate scales linearly with quantity`() {
        val single = estimator.estimate(ActivityType.TRANSPORTATION, "car", 1.0)
        val double = estimator.estimate(ActivityType.TRANSPORTATION, "car", 2.0)
        assertEquals(double, single * 2, 0.001)
    }
}
