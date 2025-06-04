package com.example.mobile.data.dto.auth

data class RefreshRes(
    val tokens: Tokens
)

data class Tokens(
    val accessToken: String,
    val refreshToken: String
)
