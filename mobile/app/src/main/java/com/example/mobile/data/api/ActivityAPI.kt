package com.example.mobile.data.api

import com.example.mobile.data.dto.activity.ActivitySearchResponse
import com.example.mobile.data.dto.activity.AddActivityRequest
import com.example.mobile.data.dto.activity.EditActivityRequest
import com.example.mobile.data.dto.activity.RemoveActivityRequest
import com.example.mobile.domain.model.Activity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ActivityApi {
    @GET("activities/search")
    fun searchActivities(
        @Query("query") query: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sortBy") sortBy: String?,
        @Query("sortOrder") sortOrder: String?
    ): Call<ActivitySearchResponse>

    @GET("activities/{id}")
    fun getActivityById(@Path("id") id: String): Call<Activity>

    @GET("activities")
    suspend fun getAllActivities(): List<Activity>

    @POST("activities/add-to-daily-log")
    fun addActivityToDailyLog(
        @Body body: AddActivityRequest
    ): Call<ResponseBody>

    @PUT("activities/edit-from-daily-log")
    fun editActivityInDailyLog(@Body request: EditActivityRequest): Call<ResponseBody>

    @HTTP(method = "DELETE", path = "activities/remove-from-daily-log", hasBody = true)
    fun deleteActivityFromDailyLog(@Body request: RemoveActivityRequest): Call<ResponseBody>
}
