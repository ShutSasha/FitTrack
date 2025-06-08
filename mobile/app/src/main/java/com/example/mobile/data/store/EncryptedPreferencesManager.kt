package com.example.mobile.data.store

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.mobile.data.api.AuthAPI
import org.json.JSONObject

class EncryptedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences
    private lateinit var authAPI: AuthAPI

    init {
        val masterKeyAlias = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "encrypted_preferences",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getUserIdFromAccessToken(): String? {

        val accessToken = getAccessToken()
        if (accessToken == null) return null

        return try {
            val parts = accessToken.split(".")
            if (parts.size < 2) return null

            val payloadEncoded = parts[1]
            val decodedBytes = Base64.decode(
                payloadEncoded,
                Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
            )
            val payload = String(decodedBytes, Charsets.UTF_8)

            val json = JSONObject(payload)
            json.optString("id", null)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getRoleFromAccessToken(): String? {
        val accessToken = getAccessToken() ?: return null

        return try {
            val parts = accessToken.split(".")
            if (parts.size < 2) return null

            val payloadEncoded = parts[1]
            val decodedBytes = Base64.decode(
                payloadEncoded,
                Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
            )
            val payload = String(decodedBytes, Charsets.UTF_8)

            val json = JSONObject(payload)
            val rolesArray = json.optJSONArray("roles") ?: return null
            if (rolesArray.length() == 0) return null

            val firstRole = rolesArray.getJSONObject(0)
            firstRole.optString("value", null)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveUserId(_id: String?) {
        val editor = sharedPreferences.edit()
        editor.putString("_id", _id ?: "")
        editor.apply()
    }

    fun saveTokens(accessToken: String?, refreshToken: String?) {
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", accessToken ?: "")
        editor.putString("refreshToken", refreshToken ?: "")
        editor.apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString("_id", null)
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("accessToken", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refreshToken", null)
    }
}
