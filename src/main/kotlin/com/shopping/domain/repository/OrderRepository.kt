package com.shopping.domain.repository

import com.shopping.domain.model.Order
import com.shopping.domain.model.valueObject.ID

interface OrderRepository {

    suspend fun getOrdersByCustomerId(customerId: ID): Result<List<Order>>

    suspend fun getOrderById(orderId: ID): Result<Order>

    suspend fun createOrder(order: Order): Result<Order>

    suspend fun getOrderItemsByOrderId(orderId: ID): Result<Set<Order.OrderItem>>
}
