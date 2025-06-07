package com.example.mobile.presentation.view.main.home

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobile.R
import com.example.mobile.data.api.RetrofitClient
import com.example.mobile.data.dto.dailyLog.UserDailyLogRes
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.example.mobile.databinding.ActivityItemBinding
import com.example.mobile.databinding.FragmentHomeBinding
import com.example.mobile.presentation.view.custom.MealsDropdowns
import com.example.mobile.presentation.view.util.DateUtils
import com.example.mobile.presentation.view.util.DateUtils.formatIsoDateToReadable
import com.example.mobile.presentation.view.util.DateUtils.getTodayDateString
import com.example.mobile.presentation.view.util.ErrorUtils
import es.dmoral.toasty.Toasty
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var encryptedPreferencesManager: EncryptedPreferencesManager? = null
    private val binding get() = _binding!!
    private var userDailyLogRes: UserDailyLogRes? = null
    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        encryptedPreferencesManager = EncryptedPreferencesManager(requireContext())

        selectedDate = getTodayDateString()
        dailylog(selectedDate)

        binding.calendarIcon.setOnClickListener {
            openDatePicker()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpFragment() {
        userDailyLogRes?.let { logRes ->

            binding.date.text = formatIsoDateToReadable(logRes.date)

            binding.calorieProgress.setCalories(
                logRes.totalCalories.toInt(),
                logRes.calories.target.toInt(),
                unit = "Kcal"
            )
            binding.proteinProgress.setNutrition(
                logRes.protein.current.toInt(),
                logRes.protein.target.toInt(),
                label = "Proteins",
                unit = "g"
            )
            binding.fatProgress.setNutrition(
                logRes.fat.current.toInt(),
                logRes.fat.target.toInt(),
                label = "Fats",
                unit = "g"
            )
            binding.carbProgress.setNutrition(
                logRes.carbs.current.toInt(),
                logRes.carbs.target.toInt(),
                label = "Carbohydrates",
                unit = "g"
            )

            binding.firstCard.cardIcon.setImageResource(R.drawable.ic_apple)
            binding.firstCard.cardMainText.text = "${logRes.calories.current.toInt()} Kcal"
            binding.firstCard.cardSubText.text = "${logRes.calories.target.toInt()} Kcal"
            binding.firstCard.cardTitle.text = "Consumed calories"
            binding.firstCard.cardWrapper.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.shape_yellow_card)

            binding.secondCard.cardIcon.setImageResource(R.drawable.ic_fire)
            binding.secondCard.cardMainText.text = "${logRes.burnedCalories.toInt()} Kcal"
            binding.secondCard.cardSubText.text = ""
            binding.secondCard.cardTitle.text = "Burned calories"
            binding.secondCard.cardWrapper.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.shape_green_card)

            binding.thirdCard.cardIcon.setImageResource(R.drawable.ic_water)
            binding.thirdCard.cardMainText.text = "${logRes.water.current.toInt()} ml"
            binding.thirdCard.cardSubText.text = "${logRes.water.target.toInt()} ml"
            binding.thirdCard.cardTitle.text = "Drank water"
            binding.thirdCard.cardWrapper.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.shape_blue_card)

            binding.fourthCard.cardIcon.setImageResource(R.drawable.ic_weight)
            binding.fourthCard.cardMainText.text = "${logRes.weight.current.toInt()} Kg"
            binding.fourthCard.cardSubText.text = "${logRes.weight.target.toInt()} Kg"
            binding.fourthCard.cardTitle.text = "Weight goal"
            binding.fourthCard.cardWrapper.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.shape_pink_card)

            val dropdownMap = mapOf(
                "Breakfast" to requireView().findViewById<View>(R.id.breakfast),
                "Lunch" to requireView().findViewById<View>(R.id.lunch),
                "Dinner" to requireView().findViewById<View>(R.id.dinner),
                "Snack1" to requireView().findViewById<View>(R.id.snack1),
                "Snack2" to requireView().findViewById<View>(R.id.snack2),
                "Snack3" to requireView().findViewById<View>(R.id.snack3)
            )

            val data = userDailyLogRes
            if (data != null) {
                MealsDropdowns().renderMeals(
                    dailyLog = data,
                    dropdownMap = dropdownMap,
                    inflater = layoutInflater,
                    onEditClicked = { nutritionProductId ->
                        val meal = logRes.meals.firstOrNull { meal ->
                            meal.nutritionProducts.any { it._id == nutritionProductId }
                        }
                        val product =
                            meal?.nutritionProducts?.firstOrNull { it._id == nutritionProductId }

                        if (meal != null && product != null) {
                            val bundle = Bundle().apply {
                                putString("mealId", meal._id)
                                putString("userId", logRes.userId)
                                putString("date", logRes.date)
                                putString("type", meal.type)
                                putString("nutritionProductId", product.nutritionProductId)
                                putString("entryId", product._id)
                                putDouble("amount", product.amount)
                                putString("productName", product.productName)
                                val caloriesPer100g =
                                    product.productCalories / (product.amount / 100.0)
                                putDouble("calories", caloriesPer100g)
                            }
                            findNavController().navigate(R.id.navigation_addFoodToMeal, bundle)
                        }
                    },
                    onDeleteClicked = { meal, nutritionEntryId ->
                        deleteNutritionFromMeal(meal._id, nutritionEntryId)
                    }
                )
            }

            binding.calendarIcon.setOnClickListener {
                openDatePicker()
            }

            binding.activitiesContainer.removeAllViews()

            binding.activityTitle.isVisible = logRes.activities.isNotEmpty()

            logRes.activities.forEach { activity ->
                val itemBinding =
                    ActivityItemBinding.inflate(layoutInflater, binding.activitiesContainer, false)

                itemBinding.optionName.text = activity.activityName
                itemBinding.optionCalories.text = "${activity.burnedCalories.toInt()} Kcal"

                itemBinding.editIcon.setOnClickListener {
                    // логика редактирования активности
                }
                itemBinding.deleteIcon.setOnClickListener {
                    // логика удаления активности
                }

                binding.activitiesContainer.addView(itemBinding.root)
            }
        }
    }

    private fun openDatePicker() {
        val datePicker = DatePickerDialog(requireContext())
        datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
            val formattedDate = DateUtils.formatDate(year, month, dayOfMonth)
            selectedDate = formattedDate
            dailylog(selectedDate)
        }
        datePicker.show()
    }

    private fun dailylog(date: String) {
        val userId = encryptedPreferencesManager?.getUserIdFromAccessToken()

        val dailyLogApi = RetrofitClient.getInstance(requireContext()).dailyLogAPI

        dailyLogApi.userDailyLog(userId, date)
            .enqueue(object : Callback<UserDailyLogRes> {
                override fun onResponse(
                    call: Call<UserDailyLogRes>,
                    response: Response<UserDailyLogRes>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            userDailyLogRes = it
                            Log.d("DailyLog", it.toString())
                            setUpFragment()
                        }
                    } else {
                        val errorMessage =
                            ErrorUtils.parseErrorMessage(response.errorBody()?.string())

                        Toasty.error(
                            requireContext(),
                            errorMessage,
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                        Log.e("DailyLog", "Error: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<UserDailyLogRes>, t: Throwable) {
                    Toasty.error(
                        requireContext(),
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    Log.e("DailyLog", "Failed: ${t.message}", t)
                }
            })
    }

    private fun deleteNutritionFromMeal(mealId: String, nutritionEntryId: String) {

        val mealApi = RetrofitClient.getInstance(requireContext()).mealApi

        mealApi.deleteNutritionFromMeal(mealId, nutritionEntryId)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toasty.success(
                            requireContext(),
                            "Product removed from meal successfully!",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                        Log.d("Meal", response.toString())
                        dailylog(selectedDate)
                    } else {
                        val errorMessage =
                            ErrorUtils.parseErrorMessage(response.errorBody()?.string())

                        Toasty.error(
                            requireContext(),
                            errorMessage,
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                        Log.e("Meal", "Error: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toasty.error(
                        requireContext(),
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    Log.e("Meal", "Failed: ${t.message}", t)
                }
            })
    }
}
