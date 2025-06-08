package com.example.mobile.data.dto.meal

data class AddMealRequest(
    val type: String,
    val userId: String,
    val date: String,
    val nutritionProduct: NutritionProduct
) {
    data class NutritionProduct(
        val nutritionProductId: String,
        val amount: Double
    )
}
