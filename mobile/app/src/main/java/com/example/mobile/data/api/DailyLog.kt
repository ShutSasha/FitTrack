package com.example.mobile.data.api

import com.example.mobile.dto.dailyLog.UserDailyLogRes
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DailyLog {

    @GET("daily-logs/{userId}/{date}")
    fun userDailyLog(@Path("userId") userId: String?, @Path("date") date: String?): Call<UserDailyLogRes>
}