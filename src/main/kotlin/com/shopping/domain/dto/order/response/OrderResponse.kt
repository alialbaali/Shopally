package com.shopping.domain.dto.order.response

data class OrderResponse(
    val orderId: String,
    val addressName: String,
    val cardLast4Numbers: Long,
    val numberOfItems: Long,
    val totalPrice: Double,
)
