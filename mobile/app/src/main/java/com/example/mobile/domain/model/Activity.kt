package com.example.mobile.domain.model

data class Activity(
    val _id: String,
    val activity: String,
    val activityName: String,
    val totalMinutes: Int,
    val burnedCalories: Double,
)
