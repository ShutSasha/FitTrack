package com.example.mobile.data.api

import android.util.Log
import com.example.mobile.data.store.EncryptedPreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authAPI: AuthAPI,
    private val encryptedPreferencesManager: EncryptedPreferencesManager
) : Interceptor {

    @Volatile
    private var isRefreshing = false
    private val lock = Any()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val url = originalRequest.url()

        if (url.encodedPath().contains("/refresh-token")) {
            return chain.proceed(originalRequest)
        }

        val accessToken = encryptedPreferencesManager.getAccessToken()
        var requestWithAuth = originalRequest
        if (!accessToken.isNullOrBlank()) {
            requestWithAuth = originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        }

        var response = chain.proceed(requestWithAuth)

        if (response.code() == 401) {
            response.close()

            synchronized(lock) {
                if (!isRefreshing) {
                    isRefreshing = true
                    val refreshToken = encryptedPreferencesManager.getRefreshToken()
                    val newAccessToken =
                        if (refreshToken != null) refreshAccessToken(refreshToken) else null
                    isRefreshing = false

                    if (!newAccessToken.isNullOrBlank()) {
                        Log.i("AuthInterceptor", "Token refreshed successfully, retrying request")
                        val newRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                        return chain.proceed(newRequest)
                    } else {
                        Log.e("AuthInterceptor", "Failed to refresh token")
                    }
                } else {
                    val latestToken = encryptedPreferencesManager.getAccessToken()
                    return if (!latestToken.isNullOrBlank()) {
                        val newRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer $latestToken")
                            .build()
                        chain.proceed(newRequest)
                    } else {
                        response
                    }
                }
            }
        }

        return response
    }

    private fun refreshAccessToken(refreshToken: String): String? {
        return try {
            val refreshResponse = authAPI.refreshToken(refreshToken).execute()

            if (refreshResponse.isSuccessful) {
                val body = refreshResponse.body()
                if (body != null) {
                    val newAccessToken = body.tokens.accessToken
                    val newRefreshToken = body.tokens.refreshToken

                    encryptedPreferencesManager.saveTokens(newAccessToken, newRefreshToken)

                    Log.i("AuthInterceptor", "Token refreshed: new access token saved")
                    return newAccessToken
                } else {
                    Log.e("AuthInterceptor", "Empty response body during token refresh")
                }
            } else {
                Log.e(
                    "AuthInterceptor",
                    "Refresh token failed: ${refreshResponse.errorBody()?.string()}"
                )
            }
            null
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Exception during token refresh: ${e.message}")
            null
        }
    }
}
