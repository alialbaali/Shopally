package com.shopping.service

import com.shopping.*
import com.shopping.domain.dto.order.request.CreateOrderRequest
import com.shopping.domain.dto.order.response.OrderDetailsResponse
import com.shopping.domain.dto.order.response.OrderResponse
import com.shopping.domain.model.Order
import com.shopping.domain.repository.CustomerRepository
import com.shopping.domain.repository.OrderRepository
import com.shopping.domain.repository.ProductRepository
import com.shopping.domain.service.OrderService
import org.koin.core.KoinComponent
import org.koin.core.get

class OrderServiceImpl(private val orderRepository: OrderRepository) : OrderService, KoinComponent {

    override suspend fun getOrdersByCustomerId(customerId: String): List<OrderResponse> {
        return orderRepository.getOrdersByCustomerId(customerId.asID())
            .fold(
                onSuccess = { orders ->
                    orders.map { order ->
                        val orderItems = orderRepository.getOrderItemsByOrderId(order.id)
                            .getOrElse { notFoundError(it.message) }

                        createOrderResponse(order.copy(orderItems = orderItems))
                    }
                },
                onFailure = { notFoundError(it.message) }
            )
    }

    override suspend fun getOrderById(customerId: String, orderId: String): OrderDetailsResponse {
        return orderRepository.getOrderById(orderId.asID())
            .fold(
                onSuccess = { order ->
                    val orderItems = orderRepository.getOrderItemsByOrderId(order.id)
                        .getOrElse { notFoundError(it.message) }

                    createOrderDetailsResponse(order.copy(orderItems = orderItems))
                },
                onFailure = { notFoundError(it.message) }
            )
    }

    override suspend fun createOrder(customerId: String, createOrderRequest: CreateOrderRequest): OrderDetailsResponse {

        val id = customerId.asID()

        val (addressName, cardLast4) = createOrderRequest

        val customerRepository = get<CustomerRepository>()

        val address = customerRepository.getAddress(id, addressName)
            .getOrElse { notFoundError(it.message) }

        val card = customerRepository.getCard(id, cardLast4.toLong())
            .getOrElse { notFoundError(it.message) }

        val orderItems = customerRepository.getCartByCustomerId(id)
            .map { it.value }
            .getOrElse { notFoundError(it.message) }
            .takeIf { it.isNotEmpty() } ?: badRequestError(Errors.CartIsEmpty)

        val order = Order(customerId = id, address = address, card = card, orderItems = orderItems)

        customerRepository.chargeCard(id, card.last4Numbers, order.sumOrderItemsByPrice())
            .onFailure { badRequestError(it.message) }

        return orderRepository.createOrder(order)
            .fold(
                onSuccess = {
                    customerRepository.deleteCartItemsByCustomerId(id)
                    createOrderDetailsResponse(order)
                },
                onFailure = { badRequestError(it.message) }
            )
    }

    private suspend fun createOrderDetailsResponse(order: Order): OrderDetailsResponse {
        return order.toOrderDetailsResponse(order.sumOrderItemsByPrice())
    }

    private suspend fun createOrderResponse(order: Order): OrderResponse {
        return order.toOrderResponse(order.sumOrderItemsByPrice())
    }

    private suspend fun Order.sumOrderItemsByPrice(): Double {
        return orderItems.sumByDouble { orderItem ->
            get<ProductRepository>()
                .getProductById(orderItem.productId)
                .map { it.price }
                .getOrElse { notFoundError(it.message) }
                .times(orderItem.quantity)
        }
    }
}

private fun Order.toOrderResponse(totalPrice: Double): OrderResponse {
    return OrderResponse(
        id.toString(),
        address.name,
        card.number.toString().takeLast(4).toLong(),
        orderItems.count().toLong(),
        totalPrice
    )
}

private fun Order.toOrderDetailsResponse(totalPrice: Double): OrderDetailsResponse {
    return OrderDetailsResponse(
        id.toString(),
        address.name,
        card.number.toString().takeLast(4).toLong(),
        orderItems,
        totalPrice,
    )
}
