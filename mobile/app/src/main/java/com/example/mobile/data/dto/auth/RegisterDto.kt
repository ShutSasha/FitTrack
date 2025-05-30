package com.example.mobile.dto.auth

data class RegisterDto(
    val email: String,
    val username: String,
    val password: String,
    val confirmPassword: String,
)
