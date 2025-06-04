package com.example.mobile.data.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Path

interface MealApi {
    @DELETE("meals/{mealId}/{nutritionEntryId}")
    fun deleteNutritionFromMeal(
        @Path("mealId") mealId: String?,
        @Path("nutritionEntryId") nutritionEntryId: String?
    ): Call<ResponseBody>
}
