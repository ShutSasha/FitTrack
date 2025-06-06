package com.example.mobile.data.api

import com.example.mobile.data.dto.productRequest.ProductRequestDto
import com.example.mobile.data.dto.productRequest.ProductRequestResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductRequestApi {
    @GET("product-requests/search")
    fun searchProductRequests(
        @Header("accessToken") accessToken: String?,
        @Query("query") query: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sortBy") sortBy: String?,
        @Query("sortOrder") sortOrder: String?,
        @Query("productType") productType: String?,
    ): Call<ProductRequestResponse>

    @POST("product-requests/aprove/{requestId}")
    fun approveRequest(
        @Header("accessToken") accessToken: String?,
        @Path("requestId") requestId: String,
    ): Call<ResponseBody>

    @POST("product-requests/reject/{requestId}")
    fun rejectRequest(
        @Header("accessToken") accessToken: String?,
        @Path("requestId") requestId: String,
    ): Call<ResponseBody>

    @POST("product-requests")
    fun createProductRequest(
        @Header("accessToken") accessToken: String?,
        @Body product: ProductRequestDto
    ): Call<ResponseBody>

}
