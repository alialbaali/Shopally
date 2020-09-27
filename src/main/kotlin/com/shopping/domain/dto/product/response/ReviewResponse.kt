package com.shopping.domain.dto.product.response

data class ReviewResponse(
    val productId: String,
    val customerId: String,
    val customerName: String,
    val customerImageUrl: String,
    val rating: Int,
    val description: String? = null,
    val creationDate: String,
)
