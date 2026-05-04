package com.ecotrack.ui.tips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecotrack.EcoTrackApplication
import com.ecotrack.data.model.Recommendation
import com.ecotrack.data.repository.EcoRepository
import com.ecotrack.databinding.FragmentTipsBinding
import com.ecotrack.databinding.ItemRecommendationBinding

/**
 * Displays AI-generated personalised eco-tips for the current user.
 */
class TipsFragment : Fragment() {

    private var _binding: FragmentTipsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TipsViewModel by viewModels {
        val db = (requireActivity().application as EcoTrackApplication).database
        TipsViewModel.Factory(
            EcoRepository(db.ecoActivityDao(), db.achievementDao(), db.userProfileDao())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RecommendationAdapter()
        binding.rvTips.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        viewModel.recommendations.observe(viewLifecycleOwner) { tips ->
            adapter.submitList(tips)
            binding.tvNoTips.visibility = if (tips.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabRefresh.setOnClickListener { viewModel.refresh() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// ── Inline adapter ────────────────────────────────────────────────────────────

private class RecommendationAdapter :
    androidx.recyclerview.widget.ListAdapter<Recommendation,
            RecommendationAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val b: ItemRecommendationBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(b.root) {
        fun bind(item: Recommendation) {
            b.tvTitle.text    = item.title
            b.tvBody.text     = item.body
            b.tvCategory.text = item.category.displayName
            b.tvSaving.text   = if (item.saving > 0)
                String.format("Save ~%.1f kg CO₂/week", item.saving) else ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemRecommendationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    private class DiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Recommendation>() {
        override fun areItemsTheSame(a: Recommendation, b: Recommendation) = a.title == b.title
        override fun areContentsTheSame(a: Recommendation, b: Recommendation) = a == b
    }
}
