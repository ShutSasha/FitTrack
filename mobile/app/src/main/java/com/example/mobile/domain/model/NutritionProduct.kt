package com.example.mobile.domain.model

data class NutritionProduct(
    val _id: String,
    val nutritionProductId: String,
    val productName: String,
    val amount: Double,
    val productCalories: Double,
)
