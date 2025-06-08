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
import com.example.mobile.data.dto.meal.EditMealRequest
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.FragmentAddFoodToMealBinding
import com.example.mobile.domain.model.Product
import com.example.mobile.presentation.view.util.PersonalizationDropdown
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class AddFoodToMealFragment : Fragment() {

    private lateinit var binding: FragmentAddFoodToMealBinding
    private lateinit var encryptedPrefs: EncryptedPreferencesManager
    private lateinit var productId: String
    private lateinit var selectedMealType: String

    private var isEditMode = false
    private var existingEntryId: String? = null
    private var existingMealId: String? = null
    private var existingUserId: String? = null
    private var existingDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddFoodToMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        encryptedPrefs = EncryptedPreferencesManager(requireContext())
        val args = arguments

        productId = args?.getString("productId") ?: args?.getString("nutritionProductId") ?: return

        args?.getString("entryId")?.let {
            isEditMode = true
            existingEntryId = it
            existingMealId = args.getString("mealId")
            existingUserId = args.getString("userId")
            existingDate = args.getString("date")
            val amount = args.getDouble("amount").toInt().toString()
            binding.inputAmount.setText(amount)

            selectedMealType = args.getString("type") ?: ""
            binding.selectedMealType.text = selectedMealType
        }

        RetrofitClient.getInstance(requireContext())
            .nutritionProductApi
            .getProductById(productId)
            .enqueue(object : Callback<Product> {
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    if (response.isSuccessful) {
                        response.body()?.let { product ->
                            binding.productName.text = product.name
                            binding.caloriesText.text = "${product.calories} kcal"
                            binding.proteinText.text = "${product.protein} g"
                            binding.fatText.text = "${product.fat} g"
                            binding.carbsText.text = "${product.carbs} g"
                            binding.productTypeText.text = product.productType
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to load product",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })

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

            val mealApi = RetrofitClient.getInstance(requireContext()).mealApi

            if (isEditMode && existingEntryId != null && existingMealId != null && existingUserId != null && existingDate != null) {
                val request = EditMealRequest(
                    userId = existingUserId!!,
                    date = existingDate!!,
                    mealId = existingMealId!!,
                    _idForNutritionProduct = existingEntryId!!,
                    amount = amount,
                    type = selectedMealType
                )

                mealApi.editNutritionInMeal(request)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(requireContext(), "Meal updated", Toast.LENGTH_SHORT)
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
                            Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            } else {
                val request = AddMealRequest(
                    type = selectedMealType,
                    userId = userId,
                    date = date,
                    nutritionProduct = AddMealRequest.NutritionProduct(
                        nutritionProductId = productId,
                        amount = amount
                    )
                )

                mealApi.addMeal(request)
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
                            Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }
        }
    }
}
