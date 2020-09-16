package com.shopping.domain.service

import com.shopping.domain.dto.order.request.CreateOrderRequest
import com.shopping.domain.dto.order.response.OrderDetailsResponse
import com.shopping.domain.dto.order.response.OrderResponse

interface OrderService {

    suspend fun getOrdersByCustomerId(customerId: String): List<OrderResponse>

    suspend fun getOrderById(customerId: String, orderId: String): OrderDetailsResponse

    suspend fun createOrder(customerId: String, createOrderRequest: CreateOrderRequest): OrderDetailsResponse
}
