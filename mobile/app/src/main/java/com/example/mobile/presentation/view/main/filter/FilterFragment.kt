package com.example.mobile.presentation.view.main.filter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.FragmentFilterBinding
import com.example.mobile.presentation.view.custom.FilterDropdown

class FilterFragment : Fragment() {

    private var _binding: FragmentFilterBinding? = null
    private var encryptedPreferencesManager: EncryptedPreferencesManager? = null
    private val binding get() = _binding!!
    private val filterOptions = listOf(
        "Fruit" to "fruit",
        "Vegetable" to "vegetable",
        "Meat" to "meat",
        "Fish" to "fish",
        "Drink" to "drink",
        "Bakery" to "bakery",
        "Pastry" to "pastry",
        "Milk and milk products" to "milk and milk products",
        "Sweets" to "sweets",
        "Sauces" to "sauces",
        "Water" to "water",
        "Grain" to "grain"
    )
    private lateinit var filterDropdown: FilterDropdown
    private var selectedFilter: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        encryptedPreferencesManager = EncryptedPreferencesManager(requireContext())
        filterDropdown = FilterDropdown(requireContext())

        selectedFilter = arguments?.getString("selected_filter")
        binding.filterByProductType.title.text = "Product type"
        binding.filterByProductType.totalCalories.text = ""

        val header = binding.filterByProductType.header
        val arrow = binding.filterByProductType.arrow
        val optionsContainer = binding.filterByProductType.options
        val selectedTextView = binding.filterByProductType.totalCalories

        filterDropdown.setUpDropdown(
            container = optionsContainer,
            header = header,
            arrow = arrow,
            selectedTextView = selectedTextView,
            options = filterOptions,
            initialSelectedKey = selectedFilter
        ) { selectedKey ->
            selectedFilter = selectedKey
        }

        binding.saveButton.setOnClickListener {
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set("selected_filter", selectedFilter)
            findNavController().popBackStack()
        }

        return binding.root
    }
}
