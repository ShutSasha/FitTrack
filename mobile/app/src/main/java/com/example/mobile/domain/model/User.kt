package com.example.mobile.domain.model

data class User(
    val _id: String,
    val username: String,
    val email: String,
    val password: String,
    val roles: List<Role>,
    val avatar: String,
    val gender: String,
    val height: Int,
    val weight: Int,
    val bodyType: String,
    val activityLevel: String,
    val birthDate: String,
    val goalType: String,
    val targetWeight: Int,
)
