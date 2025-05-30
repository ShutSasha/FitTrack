package com.example.mobile.dto.auth

data class RefreshRes(
    val tokens: Tokens
)

data class Tokens(
    val accessToken: String,
    val refreshToken: String
)
