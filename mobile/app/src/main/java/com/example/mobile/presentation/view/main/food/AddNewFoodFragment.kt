package com.example.mobile.presentation.view.main.food

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mobile.R
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.data.dto.productRequest.ProductRequestDto
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.FragmentAddNewFoodBinding
import com.example.mobile.presentation.view.util.PersonalizationDropdown
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddNewFoodFragment : Fragment() {

    private var _binding: FragmentAddNewFoodBinding? = null
    private val binding get() = _binding!!

    private lateinit var encryptedPrefs: EncryptedPreferencesManager
    private lateinit var dropdown: PersonalizationDropdown
    private lateinit var selectedProductType: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        encryptedPrefs = EncryptedPreferencesManager(requireContext())
        dropdown = PersonalizationDropdown(requireContext())

        setupDropdown()
        setupSaveButton()
    }

    private fun setupDropdown() {
        val options = listOf(
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

        dropdown.setUpDropdown(
            container = binding.productTypeOptions,
            header = binding.headerProductType,
            arrow = binding.productTypeArrow,
            selectedTextView = binding.selectedProductType,
            options = options,
            onOptionSelected = { selectedKey ->
                selectedProductType = selectedKey
            },
            optionBackgroundRes = R.drawable.shape_outlined_dropdown_item
        )

        binding.productTypeOptions.post {
            for (i in 0 until binding.productTypeOptions.childCount) {
                val view = binding.productTypeOptions.getChildAt(i)
                val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.topMargin = dpToPx(15)
                view.layoutParams = layoutParams
                view.setBackgroundResource(R.drawable.shape_outlined_dropdown_item)
            }
        }

    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            val name = binding.inputName.text.toString().trim()
            val calories = binding.inputCalories.text.toString().toDoubleOrNull()
            val protein = binding.inputProtein.text.toString().toDoubleOrNull()
            val fat = binding.inputFat.text.toString().toDoubleOrNull()
            val carbs = binding.inputCarbs.text.toString().toDoubleOrNull()

            if (name.isBlank() || calories == null || protein == null || fat == null || carbs == null || !::selectedProductType.isInitialized) {
                Toast.makeText(requireContext(), "Fill all fields correctly", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val product = ProductRequestDto(
                name = name,
                calories = calories,
                protein = protein,
                fat = fat,
                carbs = carbs,
                productType = selectedProductType
            )

            val token = encryptedPrefs.getAccessToken()

            RetrofitClient.getInstance(requireContext())
                .productRequestApi
                .createProductRequest(token, product)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Food added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            clearFields()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(
                            requireContext(),
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun clearFields() {
        binding.inputName.text?.clear()
        binding.inputCalories.text?.clear()
        binding.inputProtein.text?.clear()
        binding.inputFat.text?.clear()
        binding.inputCarbs.text?.clear()
        binding.selectedProductType.text = "Select Product Type"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

}
