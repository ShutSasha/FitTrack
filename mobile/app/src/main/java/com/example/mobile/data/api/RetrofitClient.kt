package com.example.mobile.data.api

import android.content.Context
import com.example.mobile.data.store.EncryptedPreferencesManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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

    val dailyLogAPI: DailyLog = retrofit.create(DailyLog::class.java)
    val mealApi: MealApi = retrofit.create(MealApi::class.java)
    val userApi: UserApi = retrofit.create(UserApi::class.java)
    val roleApi: RoleApi = retrofit.create(RoleApi::class.java)
    val productRequestApi: ProductRequestApi = retrofit.create(ProductRequestApi::class.java)

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