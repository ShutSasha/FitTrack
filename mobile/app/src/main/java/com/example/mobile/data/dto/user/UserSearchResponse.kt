package com.example.mobile.dto.user

import com.example.mobile.domain.model.User

data class UserSearchResponse(
    val items: List<User>,
    val total: Int,
    val page: Int,
    val limit: Int,
)
