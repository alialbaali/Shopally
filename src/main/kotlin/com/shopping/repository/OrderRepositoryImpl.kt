package com.shopping.repository

import com.shopping.Errors
import com.shopping.db.OrderItems
import com.shopping.db.OrderItemsQueries
import com.shopping.db.Orders
import com.shopping.db.OrdersQueries
import com.shopping.domain.model.Order
import com.shopping.domain.model.valueObject.Address
import com.shopping.domain.model.valueObject.Card
import com.shopping.domain.model.valueObject.ID
import com.shopping.domain.repository.OrderRepository
import java.time.LocalDate

class OrderRepositoryImpl(
    private val ordersQueries: OrdersQueries,
    private val orderItemsQueries: OrderItemsQueries,
) : OrderRepository {

    override suspend fun getOrdersByCustomerId(customerId: ID): Result<List<Order>> {
        return ordersQueries.getOrdersByCustomerId(customerId)
            .executeAsList()
            .map { dbOrder -> dbOrder.toOrder() }
            .let { orders -> Result.success(orders) }
    }

    override suspend fun getOrderById(orderId: ID): Result<Order> {
        return ordersQueries.getOrderById(orderId)
            .executeAsOneOrNull()
            ?.toOrder()
            ?.let { order -> Result.success(order) } ?: Result.failure(Throwable(Errors.OrderDoesntExist))
    }

    override suspend fun createOrder(order: Order): Result<Order> {

        val (id, customerId, orderItems, address, card, creationDate) = order

        ordersQueries.transaction {

            ordersQueries.createOrder(id, customerId, card.number, address.name, creationDate)

            orderItems.forEach { orderItem ->
                orderItemsQueries.createOrderItem(id, orderItem.productId, orderItem.quantity)
            }
        }

        return getOrderById(id)
    }

    override suspend fun getOrderItemsByOrderId(orderId: ID): Result<Set<Order.OrderItem>> {
        return orderItemsQueries.getOrderItemsByOrderId(orderId)
            .executeAsList()
            .map { dbOrderItem -> dbOrderItem.toOrderItem() }
            .toSet()
            .let { Result.success(it) }
    }
}

private fun Orders.toOrder(orderItems: Set<Order.OrderItem> = emptySet()): Order {
    return Order(
        id,
        customer_id,
        orderItems,
        Address(address_name, "", "", "", ""),
        Card(number = card_last_4_numbers, expirationDate = LocalDate.now()),
        creation_date
    )
}

private fun OrderItems.toOrderItem(): Order.OrderItem {
    return Order.OrderItem(product_id, quantity)
}
