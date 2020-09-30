package com.shopping.domain.dto.customer.response

data class CartItemResponse(
    val productId: String,
    val productBrand: String,
    val productName: String,
    val productImage: String,
    val productPrice: Double,
    val quantity: Long,
    val totalPrice: Double = productPrice.times(quantity),
)
