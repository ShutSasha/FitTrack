package com.example.mobile.API

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface AppAPI {
    @GET("hello")
    fun hello(): Call<ResponseBody>
}