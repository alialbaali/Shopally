package com.shopping.fake

import com.shopping.Errors
import com.shopping.domain.model.Order
import com.shopping.domain.model.valueObject.ID
import com.shopping.domain.repository.OrderRepository

class FakeOrderRepository(
    private val orders: MutableList<Order> = mutableListOf(),
) : OrderRepository {

    override suspend fun getOrdersByCustomerId(customerId: ID): Result<List<Order>> {
        return orders.filter { order -> order.customerId == customerId }
            .let { orders -> Result.success(orders) }
    }

    override suspend fun getOrderById(orderId: ID): Result<Order> {
        return orders.find { order -> order.id == orderId }
            ?.let { order -> Result.success(order) } ?: Result.failure(Throwable(Errors.OrderDoesntExist))
    }

    override suspend fun createOrder(order: Order): Result<Order> {
        return orders.add(order)
            .let { Result.success(order) }
    }

    override suspend fun getOrderItemsByOrderId(orderId: ID): Result<Set<Order.OrderItem>> {
        return getOrderById(orderId)
            .map { order -> order.orderItems }
    }

}
