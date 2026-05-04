package com.ecotrack.ui.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ecotrack.EcoTrackApplication
import com.ecotrack.R
import com.ecotrack.data.model.ActivityType
import com.ecotrack.data.repository.EcoRepository
import com.ecotrack.databinding.FragmentLogActivityBinding
import com.ecotrack.util.formatCo2
import com.google.android.material.snackbar.Snackbar

/**
 * Screen where the user selects an activity category, provides a description,
 * and enters a quantity. The ViewModel estimates carbon in real-time and
 * persists the entry when the user confirms.
 */
class LogActivityFragment : Fragment() {

    private var _binding: FragmentLogActivityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LogActivityViewModel by viewModels {
        val db = (requireActivity().application as EcoTrackApplication).database
        LogActivityViewModel.Factory(
            EcoRepository(db.ecoActivityDao(), db.achievementDao(), db.userProfileDao())
        )
    }

    private var selectedType: ActivityType = ActivityType.TRANSPORTATION
    private var selectedSubType: String = "car"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategorySpinner()
        setupObservers()

        binding.btnSave.setOnClickListener { submitLog() }
        binding.btnCancel.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupCategorySpinner() {
        val categories = ActivityType.entries.map { it.displayName }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, v: View?, pos: Int, id: Long) {
                selectedType = ActivityType.entries[pos]
                updatePreview()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updatePreview() {
        val qty = binding.etQuantity.text?.toString()?.toDoubleOrNull() ?: return
        val carbon = viewModel.previewCarbon(selectedType, selectedSubType, qty)
        binding.tvCarbonPreview.text = getString(R.string.carbon_preview, carbon.formatCo2())
    }

    private fun setupObservers() {
        viewModel.logResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                Snackbar.make(binding.root, R.string.activity_logged, Snackbar.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Snackbar.make(binding.root, R.string.invalid_input, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun submitLog() {
        viewModel.logActivity(
            type        = selectedType,
            subType     = selectedSubType,
            description = binding.etDescription.text?.toString() ?: "",
            quantity    = binding.etQuantity.text?.toString()?.toDoubleOrNull() ?: 0.0,
            unit        = binding.etUnit.text?.toString() ?: ""
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
