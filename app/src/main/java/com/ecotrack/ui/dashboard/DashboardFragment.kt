package com.ecotrack.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ecotrack.EcoTrackApplication
import com.ecotrack.R
import com.ecotrack.data.repository.EcoRepository
import com.ecotrack.databinding.FragmentDashboardBinding
import com.ecotrack.util.formatCo2

/**
 * Home screen showing a greeting, weekly progress ring, and recent activity list.
 */
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels {
        val db = (requireActivity().application as EcoTrackApplication).database
        DashboardViewModel.Factory(
            EcoRepository(db.ecoActivityDao(), db.achievementDao(), db.userProfileDao())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profile ?: return@observe
            binding.tvGreeting.text = getString(R.string.greeting, profile.name)
            binding.tvStreak.text   = getString(R.string.streak_days, profile.streakDays)
        }

        viewModel.recentActivities.observe(viewLifecycleOwner) { activities ->
            binding.tvNoActivities.visibility =
                if (activities.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabLogActivity.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_logActivity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
