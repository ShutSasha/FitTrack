package com.example.mobile.presentation.view.main.food

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobile.R
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.data.dto.meal.AddMealRequest
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.FragmentAddFoodToMealBinding
import com.example.mobile.presentation.view.util.PersonalizationDropdown
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class AddFoodToMealFragment : Fragment() {

    private lateinit var binding: FragmentAddFoodToMealBinding
    private lateinit var selectedMealType: String
    private lateinit var productId: String
    private lateinit var encryptedPrefs: EncryptedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddFoodToMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        encryptedPrefs = EncryptedPreferencesManager(requireContext())
        productId = arguments?.getString("productId") ?: ""
        val productName = arguments?.getString("productName") ?: ""
        binding.productName.text = productName

        setupDropdown()
        setupSaveButton()
    }

    private fun setupDropdown() {
        val mealOptions = listOf(
            "Breakfast" to "Breakfast",
            "Snack 1" to "Snack1",
            "Lunch" to "Lunch",
            "Snack 2" to "Snack2",
            "Dinner" to "Dinner",
            "Snack 3" to "Snack3"
        )

        PersonalizationDropdown(requireContext()).setUpDropdown(
            container = binding.mealTypeOptions,
            header = binding.headerMealType,
            arrow = binding.mealTypeArrow,
            selectedTextView = binding.selectedMealType,
            options = mealOptions,
            onOptionSelected = { selectedKey ->
                selectedMealType = selectedKey
            },
            optionBackgroundRes = R.drawable.shape_outlined_dropdown_item
        )
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            val amount = binding.inputAmount.text.toString().toDoubleOrNull()
            val userId = encryptedPrefs.getUserId() ?: return@setOnClickListener
            val date = LocalDate.now().toString()

            if (!::selectedMealType.isInitialized || amount == null) {
                Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = AddMealRequest(
                type = selectedMealType,
                userId = userId,
                date = date,
                nutritionProduct = AddMealRequest.NutritionProduct(
                    nutritionProductId = productId,
                    amount = amount
                )
            )

            RetrofitClient.getInstance(requireContext()).mealApi.addMeal(request)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Meal saved", Toast.LENGTH_SHORT)
                                .show()
                            findNavController().navigate(R.id.navigation_home)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
