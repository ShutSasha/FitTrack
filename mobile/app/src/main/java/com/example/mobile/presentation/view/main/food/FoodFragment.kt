package com.example.mobile.presentation.view.main.food

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobile.R
import com.example.mobile.databinding.FragmentFoodBinding

class FoodFragment : Fragment() {

    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chooseFoodButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_searchFood)
        }

        binding.addNewFoodButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_addNewFood)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
