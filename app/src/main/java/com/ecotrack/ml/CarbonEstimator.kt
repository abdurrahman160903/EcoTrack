package com.ecotrack.ml

import com.ecotrack.data.model.ActivityType

/**
 * On-device carbon-footprint estimator.
 *
 * Uses a lightweight look-up-table model derived from published emission
 * factors (IPCC AR6 / Our World in Data).  In production this layer would
 * be replaced by a quantised TensorFlow Lite regression model stored in
 * `assets/carbon_model.tflite`.
 *
 * Emission factors (kg CO₂e per unit):
 *  - Car travel           : 0.21 kg / km
 *  - Bus travel           : 0.089 kg / km
 *  - Train travel         : 0.041 kg / km
 *  - Flight               : 0.255 kg / km
 *  - Electricity (grid)   : 0.233 kg / kWh  (world average)
 *  - Natural gas          : 2.04  kg / m³
 *  - Beef meal            : 6.61  kg / meal
 *  - Chicken meal         : 1.24  kg / meal
 *  - Vegan meal           : 0.50  kg / meal
 *  - New clothing item    : 10.0  kg / item  (lifecycle estimate)
 *  - Recycling (per kg)   : -0.50 kg / kg   (avoided landfill)
 */
class CarbonEstimator {

    /**
     * Estimates the carbon footprint for a given activity.
     *
     * @param type     Category of the activity.
     * @param subType  More specific identifier (e.g. "car", "bus", "beef").
     * @param quantity Amount in the activity's natural unit (km, kWh, meals, etc.).
     * @return Estimated emission in kg CO₂-equivalent (negative = saving).
     */
    fun estimate(type: ActivityType, subType: String, quantity: Double): Double {
        val factor = emissionFactor(type, subType.lowercase().trim())
        return factor * quantity
    }

    /**
     * Returns the emission factor (kg CO₂e per unit) for the given sub-type.
     * Defaults to a category-level average when the sub-type is not recognised.
     */
    fun emissionFactor(type: ActivityType, subType: String): Double = when (type) {
        ActivityType.TRANSPORTATION -> when (subType) {
            "car"       -> 0.21
            "bus"       -> 0.089
            "train"     -> 0.041
            "flight"    -> 0.255
            "bicycle", "walking" -> 0.0
            else        -> 0.17   // average
        }
        ActivityType.ENERGY -> when (subType) {
            "electricity" -> 0.233
            "gas"         -> 2.04
            "solar"       -> 0.0
            else          -> 0.233
        }
        ActivityType.FOOD -> when (subType) {
            "beef"    -> 6.61
            "pork"    -> 2.90
            "chicken" -> 1.24
            "fish"    -> 1.34
            "dairy"   -> 1.90
            "vegan", "vegetarian" -> 0.50
            else      -> 2.50
        }
        ActivityType.SHOPPING -> when (subType) {
            "clothing"     -> 10.0
            "electronics"  -> 70.0
            "second_hand"  -> 0.5
            else           -> 8.0
        }
        ActivityType.WASTE -> when (subType) {
            "recycling"    -> -0.50
            "composting"   -> -0.20
            "landfill"     -> 0.58
            else           -> 0.10
        }
    }
}
