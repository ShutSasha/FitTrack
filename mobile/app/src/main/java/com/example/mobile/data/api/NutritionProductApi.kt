package com.example.mobile.data.api

import com.example.mobile.dto.dailyLog.UserDailyLogRes
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NutritionProductApi {

    @GET("nutrition-products/{id}")
    fun getNutritionProduct(@Path("id") id: String?, @Path("date") date: String?): Call<UserDailyLogRes>
}