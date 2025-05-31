package com.example.mobile.domain.model

data class Meal(
    val _id: String,
    val userId: String,
    val type: String,
    val date: String,
    val totalCalories: Double,
    val nutritionProducts: List<NutritionProduct>,
)
