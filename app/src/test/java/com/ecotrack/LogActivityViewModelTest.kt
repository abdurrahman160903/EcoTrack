package com.ecotrack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ecotrack.data.db.entity.EcoActivityEntity
import com.ecotrack.data.model.ActivityType
import com.ecotrack.data.repository.EcoRepository
import com.ecotrack.ui.log.LogActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for [LogActivityViewModel].
 *
 * Uses Mockito to stub the repository so no real database is needed,
 * and [InstantTaskExecutorRule] to make LiveData work synchronously.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LogActivityViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: EcoRepository
    private lateinit var viewModel: LogActivityViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel  = LogActivityViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `logActivity with blank description posts failure`() {
        viewModel.logActivity(ActivityType.TRANSPORTATION, "car", "", 10.0, "km")
        val result = viewModel.logResult.value
        assertNotNull(result)
        assertTrue(result!!.isFailure)
    }

    @Test
    fun `logActivity with zero quantity posts failure`() {
        viewModel.logActivity(ActivityType.FOOD, "beef", "Steak dinner", 0.0, "meals")
        val result = viewModel.logResult.value
        assertNotNull(result)
        assertTrue(result!!.isFailure)
    }

    @Test
    fun `logActivity with valid input calls repository insert`() = runTest {
        whenever(repository.logActivity(any())).thenReturn(1L)

        viewModel.logActivity(ActivityType.TRANSPORTATION, "car", "Drive to work", 15.0, "km")

        verify(repository, timeout(1_000)).logActivity(
            argThat { entity: EcoActivityEntity ->
                entity.activityType == ActivityType.TRANSPORTATION &&
                entity.description == "Drive to work" &&
                entity.carbonKg > 0
            }
        )
        assertEquals(1L, viewModel.logResult.value?.getOrNull())
    }

    @Test
    fun `previewCarbon returns positive value for car travel`() {
        val carbon = viewModel.previewCarbon(ActivityType.TRANSPORTATION, "car", 10.0)
        assertTrue(carbon > 0)
    }

    @Test
    fun `previewCarbon returns negative value for recycling`() {
        val carbon = viewModel.previewCarbon(ActivityType.WASTE, "recycling", 5.0)
        assertTrue(carbon < 0)
    }
}
