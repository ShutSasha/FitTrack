package com.example.mobile.data.dto.activity

data class EditActivityRequest(
    val userId: String,
    val date: String,
    val activityId: String,
    val totalMinutes: Int
)