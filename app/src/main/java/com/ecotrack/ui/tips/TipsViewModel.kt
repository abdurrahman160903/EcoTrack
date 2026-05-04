package com.ecotrack.ui.tips

import androidx.lifecycle.*
import com.ecotrack.data.model.Recommendation
import com.ecotrack.data.repository.EcoRepository
import com.ecotrack.data.repository.RecommendationRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for [TipsFragment].
 *
 * Fetches carbon data from [EcoRepository], passes it through
 * [RecommendationRepository], and exposes the resulting tips as [LiveData].
 */
class TipsViewModel(
    private val ecoRepository: EcoRepository,
    private val recommendationRepo: RecommendationRepository = RecommendationRepository()
) : ViewModel() {

    private val _recommendations = MutableLiveData<List<Recommendation>>()
    val recommendations: LiveData<List<Recommendation>> = _recommendations

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val carbonData = ecoRepository.getCarbonDataForPeriod(7)
            val tips = recommendationRepo.getRecommendations(carbonData)
            _recommendations.postValue(tips)
        }
    }

    class Factory(private val repository: EcoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            TipsViewModel(repository) as T
    }
}
