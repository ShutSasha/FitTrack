package com.example.mobile.api

import com.example.mobile.dto.auth.LoginDto
import com.example.mobile.dto.auth.RefreshRes
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthAPI {

    @POST("auth/login")
    fun login(@Body loginDto: LoginDto): Call<RefreshRes>

    @POST("auth/refresh/{refreshToken}")
    fun refreshToken(@Path("refreshToken") refreshToken: String?): Call<RefreshRes>
}