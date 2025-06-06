package com.example.mobile.data.dto.productRequest

data class ProductRequestDto(
    val name: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double,
    val productType: String
)
