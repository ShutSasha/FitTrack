package com.example.mobile.data.dto.productRequest

import com.example.mobile.domain.model.Product

data class ProductRequestResponse(
    val items: List<Product>,
    val total: Int,
    val page: Int,
    val limit: Int,
)
