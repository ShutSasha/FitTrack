package com.example.mobile.dto.auth

data class ResetPasswordDto(
    val email: String,
    val code: Int,
    val newPassword: String,
    val newPasswordConfirm: String,
)