package com.ecotrack.ui.analytics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ecotrack.EcoTrackApplication
import com.ecotrack.data.model.ActivityType
import com.ecotrack.data.repository.EcoRepository
import com.ecotrack.databinding.FragmentAnalyticsBinding
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate

/**
 * Displays pie and line charts showing the user's carbon breakdown and trend.
 */
class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnalyticsViewModel by viewModels {
        val db = (requireActivity().application as EcoTrackApplication).database
        AnalyticsViewModel.Factory(
            EcoRepository(db.ecoActivityDao(), db.achievementDao(), db.userProfileDao())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.carbonData.observe(viewLifecycleOwner) { data ->
            renderPieChart(data.byCategory)
            renderLineChart(data.weeklyTrend)
            binding.tvTotalCarbon.text = String.format("Total: %.2f kg CO₂", data.totalKgCo2)
        }

        // Period toggle chips
        binding.chipWeek.setOnClickListener  { viewModel.loadData(7)  }
        binding.chipMonth.setOnClickListener { viewModel.loadData(30) }
    }

    private fun renderPieChart(byCategory: Map<ActivityType, Double>) {
        val entries = byCategory
            .filter { it.value > 0 }
            .map { PieEntry(it.value.toFloat(), it.key.displayName) }

        if (entries.isEmpty()) return

        val dataSet = PieDataSet(entries, "").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 12f
            valueTextColor = Color.WHITE
        }
        binding.pieChart.apply {
            this.data = PieData(dataSet)
            description.isEnabled = false
            animateY(600)
            invalidate()
        }
    }

    private fun renderLineChart(weeklyTrend: List<Double>) {
        val entries = weeklyTrend.mapIndexed { i, v -> Entry(i.toFloat(), v.toFloat()) }
        val dataSet = LineDataSet(entries, "Daily CO₂ (kg)").apply {
            color = Color.parseColor("#4CAF50")
            setCircleColor(Color.parseColor("#4CAF50"))
            lineWidth = 2f
            circleRadius = 4f
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }
        binding.lineChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            animateX(600)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
