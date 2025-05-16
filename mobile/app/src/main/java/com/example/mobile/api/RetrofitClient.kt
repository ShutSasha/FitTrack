package com.example.mobile.api

import android.content.Context
import com.example.mobile.store.EncryptedPreferencesManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.mobile.api.AuthAPI
import com.example.mobile.api.AppAPI
import com.example.mobile.api.AuthInterceptor

class RetrofitClient private constructor(context: Context) {
    private val BASE_URL = "https://fit-track-application-d9492f6bb75e.herokuapp.com/api/"

    private val gson = GsonBuilder().create()
    private val encryptedPreferencesManager = EncryptedPreferencesManager(context)

    private val authRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val authAPI: AuthAPI = authRetrofit.create(AuthAPI::class.java)

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(AuthInterceptor(authAPI, encryptedPreferencesManager))
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    val appAPI: AppAPI = retrofit.create(AppAPI::class.java)

    companion object {
        @Volatile
        private var instance: RetrofitClient? = null

        fun getInstance(context: Context): RetrofitClient {
            return instance ?: synchronized(this) {
                instance ?: RetrofitClient(context).also { instance = it }
            }
        }
    }
}