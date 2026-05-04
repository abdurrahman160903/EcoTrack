package com.ecotrack.ui.analytics

import androidx.lifecycle.*
import com.ecotrack.data.model.CarbonData
import com.ecotrack.data.repository.EcoRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for [AnalyticsFragment].
 *
 * Loads aggregated carbon data for the selected period and exposes it as
 * [LiveData] so the fragment can render charts without managing coroutines.
 */
class AnalyticsViewModel(private val repository: EcoRepository) : ViewModel() {

    private val _carbonData = MutableLiveData<CarbonData>()
    val carbonData: LiveData<CarbonData> = _carbonData

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadData(7)
    }

    fun loadData(days: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            val data = repository.getCarbonDataForPeriod(days)
            _carbonData.postValue(data)
            _isLoading.postValue(false)
        }
    }

    class Factory(private val repository: EcoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AnalyticsViewModel(repository) as T
    }
}
