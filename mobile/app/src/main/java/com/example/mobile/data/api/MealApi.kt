package com.example.mobile.data.api

import com.example.mobile.data.dto.meal.AddMealRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface MealApi {
    @DELETE("meals/{mealId}/{nutritionEntryId}")
    fun deleteNutritionFromMeal(
        @Path("mealId") mealId: String?,
        @Path("nutritionEntryId") nutritionEntryId: String?
    ): Call<ResponseBody>

    @POST("meals")
    fun addMeal(@Body request: AddMealRequest): Call<ResponseBody>
}
