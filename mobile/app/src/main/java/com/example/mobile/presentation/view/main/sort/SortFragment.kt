package com.example.mobile.presentation.view.main.sort

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mobile.R
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.FragmentFilterBinding
import com.example.mobile.databinding.FragmentSortBinding
import com.example.mobile.presentation.view.custom.FilterDropdown

class SortFragment : Fragment() {

    private var _binding: FragmentSortBinding? = null
    private var encryptedPreferencesManager: EncryptedPreferencesManager? = null
    private val binding get() = _binding!!
    private val sortByOptions = listOf(
        "Calories" to "calories",
        "Protein" to "protein",
        "Fat" to "fat",
        "Carbs" to "carbs"
    )

    private val sortOrderOptions = listOf(
        "Ascending" to "asc",
        "Descending" to "desc"
    )
    private lateinit var filterDropdown: FilterDropdown
    private var selectedSortBy: String? = null
    private var selectedSortOrder: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSortBinding.inflate(inflater, container, false)
        encryptedPreferencesManager = EncryptedPreferencesManager(requireContext())
        filterDropdown = FilterDropdown(requireContext())

        selectedSortBy = arguments?.getString("selected_sort_by")
        selectedSortOrder = arguments?.getString("selected_sort_order")

        binding.sortBy.title.text = "Sort by"
        binding.sortBy.totalCalories.text = ""

        filterDropdown.setUpDropdown(
            container = binding.sortBy.options,
            header = binding.sortBy.header,
            arrow = binding.sortBy.arrow,
            selectedTextView = binding.sortBy.totalCalories,
            options = sortByOptions,
            initialSelectedKey = selectedSortBy
        ) { selectedKey ->
            selectedSortBy = selectedKey
        }

        binding.sortOrder.title.text = "Sort order"
        binding.sortOrder.totalCalories.text = ""

        filterDropdown.setUpDropdown(
            container = binding.sortOrder.options,
            header = binding.sortOrder.header,
            arrow = binding.sortOrder.arrow,
            selectedTextView = binding.sortOrder.totalCalories,
            options = sortOrderOptions,
            initialSelectedKey = selectedSortOrder
        ) { selectedKey ->
            selectedSortOrder = selectedKey
        }

        binding.saveButton.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.let { handle ->
                handle["selected_sort_by"] = selectedSortBy
                handle["selected_sort_order"] = selectedSortOrder
            }
            findNavController().popBackStack()
        }

        return binding.root
    }

}