package com.example.mobile.data.api

import com.example.mobile.data.dto.auth.LoginDto
import com.example.mobile.data.dto.auth.PersonalizeDto
import com.example.mobile.data.dto.auth.PersonalizeResponse
import com.example.mobile.data.dto.auth.RefreshRes
import com.example.mobile.data.dto.auth.RegisterDto
import com.example.mobile.data.dto.auth.ResetPasswordDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthAPI {
    @POST("auth/login")
    fun login(@Body loginDto: LoginDto): Call<RefreshRes>

    @POST("auth/registration")
    fun register(@Body registerDto: RegisterDto): Call<RefreshRes>

    @POST("auth/refresh/{refreshToken}")
    fun refreshToken(@Path("refreshToken") refreshToken: String?): Call<RefreshRes>

    @POST("auth/personalization")
    fun personalize(@Body personalizeDto: PersonalizeDto): Call<PersonalizeResponse>

    @POST("auth/send-reset-password-code/{email}")
    fun sendResetPasswordCode(@Path("email") email: String): Call<ResponseBody>

    @POST("auth/confirm-reset-password")
    fun confirmResetPassword(@Body resetPasswordDto: ResetPasswordDto): Call<ResponseBody>
}
