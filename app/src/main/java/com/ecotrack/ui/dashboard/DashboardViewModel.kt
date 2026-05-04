package com.ecotrack.ui.dashboard

import androidx.lifecycle.*
import com.ecotrack.data.db.entity.EcoActivityEntity
import com.ecotrack.data.repository.EcoRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for [DashboardFragment].
 *
 * Exposes the five most recent activities and the user profile so that the
 * dashboard can display a quick summary without fetching the full history.
 */
class DashboardViewModel(private val repository: EcoRepository) : ViewModel() {

    val recentActivities: LiveData<List<EcoActivityEntity>> =
        repository.getRecentActivities(5).asLiveData()

    val userProfile = repository.getProfile().asLiveData()

    fun deleteActivity(activity: EcoActivityEntity) {
        viewModelScope.launch {
            repository.deleteActivity(activity)
        }
    }

    class Factory(private val repository: EcoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DashboardViewModel(repository) as T
    }
}
