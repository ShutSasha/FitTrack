package com.example.mobile.data.dto.activity

data class RemoveActivityRequest(
    val userId: String,
    val date: String,
    val activityId: String
)
