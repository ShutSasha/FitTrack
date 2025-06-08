package com.example.mobile.data.dto.nutritionProduct

import com.example.mobile.domain.model.Product

data class ProductSearchResponse(
    val items: List<Product>,
    val total: Int,
    val page: Int,
    val limit: Int,
)
