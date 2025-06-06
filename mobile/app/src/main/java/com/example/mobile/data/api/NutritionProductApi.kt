package com.example.mobile.data.api

import com.example.mobile.data.dto.dailyLog.UserDailyLogRes
import com.example.mobile.data.dto.nutritionProduct.ProductSearchResponse
import com.example.mobile.domain.model.Product
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NutritionProductApi {
    @GET("nutrition-products/{id}")
    fun getNutritionProduct(
        @Path("id") id: String?,
        @Path("date") date: String?
    ): Call<UserDailyLogRes>

    @GET("nutrition-products")
    suspend fun getAllNutritionProducts(): List<Product>

    @GET("nutrition-products/search")
    fun searchNutritionProducts(
        @Query("query") query: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sortBy") sortBy: String?,
        @Query("sortOrder") sortOrder: String?,
        @Query("productType") productType: String?,
    ): Call<ProductSearchResponse>
}
