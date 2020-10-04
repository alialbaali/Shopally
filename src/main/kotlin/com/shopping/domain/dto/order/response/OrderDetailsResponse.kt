package com.shopping.domain.dto.order.response

import com.shopping.domain.model.Order

data class OrderDetailsResponse(
    val orderId: String,
    val addressName: String,
    val cardLast4Numbers: Long,
    val orderItems: Set<Order.OrderItem>,
    val totalPrice: Double,
)
