package com.example.mobile.data.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface AppAPI {
    @GET("hello")
    fun hello(): Call<ResponseBody>

    @GET("hello-with-auth")
    fun helloWithAuth(): Call<ResponseBody>
}
