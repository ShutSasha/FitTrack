package com.example.mobile.data.dto.activity

data class AddActivityRequest(
    val userId: String,
    val date: String,
    val activityId: String,
    val totalMinutes: Int
)
