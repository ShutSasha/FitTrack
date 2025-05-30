package com.example.mobile.domain.model

data class Product(
    val _id: String,
    val name: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double,
    val productType: String,
)
