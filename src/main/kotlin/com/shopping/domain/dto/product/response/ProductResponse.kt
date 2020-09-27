package com.shopping.domain.dto.product.response

data class ProductResponse(
    val id: String,
    val category: String,
    val brand: String,
    val name: String,
    val image: String,
    val price: Double,
    val avgRating: Double,
)
