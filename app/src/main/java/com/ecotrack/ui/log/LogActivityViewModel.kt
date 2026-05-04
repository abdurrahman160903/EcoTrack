package com.ecotrack.ui.log

import androidx.lifecycle.*
import com.ecotrack.data.db.entity.EcoActivityEntity
import com.ecotrack.data.model.ActivityType
import com.ecotrack.data.repository.EcoRepository
import com.ecotrack.ml.CarbonEstimator
import kotlinx.coroutines.launch

/**
 * ViewModel for [LogActivityFragment].
 *
 * Handles input validation, carbon estimation, and persisting a new log entry.
 */
class LogActivityViewModel(
    private val repository: EcoRepository,
    private val estimator: CarbonEstimator = CarbonEstimator()
) : ViewModel() {

    private val _logResult = MutableLiveData<Result<Long>>()
    val logResult: LiveData<Result<Long>> = _logResult

    /**
     * Estimates CO₂ for the selected activity without persisting it, so the
     * user can see a live preview before confirming.
     */
    fun previewCarbon(type: ActivityType, subType: String, quantity: Double): Double =
        estimator.estimate(type, subType, quantity)

    /**
     * Validates inputs, estimates carbon, persists the entry, and posts the
     * result to [logResult].
     */
    fun logActivity(
        type: ActivityType,
        subType: String,
        description: String,
        quantity: Double,
        unit: String
    ) {
        if (description.isBlank() || quantity <= 0) {
            _logResult.value = Result.failure(IllegalArgumentException("Invalid input"))
            return
        }
        val carbonKg = estimator.estimate(type, subType, quantity)
        val entity = EcoActivityEntity(
            activityType = type,
            description  = description.trim(),
            carbonKg     = carbonKg,
            quantity     = quantity,
            unit         = unit
        )
        viewModelScope.launch {
            val id = repository.logActivity(entity)
            _logResult.postValue(Result.success(id))
        }
    }

    class Factory(private val repository: EcoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            LogActivityViewModel(repository) as T
    }
}
