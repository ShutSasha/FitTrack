package com.example.mobile.presentation.view.util

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

object ErrorUtils {

    fun parseErrorMessage(errorBody: String?): String {
        if (errorBody.isNullOrBlank()) return "Unknown error"

        Log.d("ErrorUtils", "Error body: $errorBody")

        return try {
            val jsonObject = JSONObject(errorBody)
            when (val messagesAny = jsonObject.opt("messages")) {
                is JSONArray -> buildMultilineMessage(messagesAny)
                is String -> messagesAny
                else -> jsonObject.optString("message", "Unknown error")
            }
        } catch (e: Exception) {
            Log.e("ErrorUtils", "Failed to parse error: ${e.message}")
            "Unknown error"
        }
    }

    private fun buildMultilineMessage(messages: JSONArray): String {
        return (0 until messages.length()).joinToString("\n") { i ->
            val fullMessage = messages.getString(i)
            val parts = fullMessage.split(" - ", limit = 2)
            if (parts.size == 2) parts[1].trim() else fullMessage
        }
    }
}
