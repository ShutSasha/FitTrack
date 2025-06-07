package com.example.mobile.data.dto.activity

import com.example.mobile.domain.model.Activity

data class ActivitySearchResponse(
    val items: List<Activity>,
    val total: Int,
    val page: Int,
    val limit: Int
)
