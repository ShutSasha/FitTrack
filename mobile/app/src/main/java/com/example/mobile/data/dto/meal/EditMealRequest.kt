package com.example.mobile.data.dto.meal

data class EditMealRequest(
    val userId: String,
    val date: String,
    val mealId: String,
    val _idForNutritionProduct: String,
    val amount: Double,
    val type: String
)
