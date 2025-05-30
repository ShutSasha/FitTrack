package com.example.mobile.dto.auth

data class PersonalizeDto(
    val userId: String,
    val gender: String,
    val height: Int,
    val weight: Double,
    val bodyType: String,
    val activityLevel: String,
    val birthDate: String,
    val goalType: String,
    val targetWeight: Double
)

