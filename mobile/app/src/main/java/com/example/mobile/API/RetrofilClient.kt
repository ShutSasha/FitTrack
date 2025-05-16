package com.example.mobile.API

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.concurrent.TimeUnit
import com.example.mobile.API.AppAPI

class RetrofitClient private constructor(context: Context) {
    private val BASE_URL = "https://fit-track-application-d9492f6bb75e.herokuapp.com/api/"

    private var retrofit: Retrofit
//    private val encryptedPreferencesManager = EncryptedPreferencesManager(context)
    val appAPI: AppAPI

    init {
        val gson = GsonBuilder().create()

//        val baseRetrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()

//        authAPI = baseRetrofit.create(AuthAPI::class.java)

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
//            .addInterceptor(AuthInterceptor(authAPI, encryptedPreferencesManager))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        appAPI = retrofit.create(AppAPI::class.java)
    }

    fun getRetrofit(): Retrofit {
        return retrofit
    }

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