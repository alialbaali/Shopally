package com.shopping.queries

import com.shopping.DefaultSpec
import com.shopping.dataSourceModule
import com.shopping.db.OrderItemsQueries
import com.shopping.dbModule
import com.shopping.domain.model.Order
import com.shopping.domain.model.valueObject.ID
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldExist
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainIgnoringCase
import org.koin.test.inject
import kotlin.random.Random

class OrderItemsQueriesTest : DefaultSpec(dbModule, dataSourceModule) {

    private val orderItemsQueries by inject<OrderItemsQueries>()

    init {

        Given("an order id, product id, quantity and a price") {
            When("creating an order item") {
                Then("it should create an order item successfully") {

                    val orderId = ID.random()
                    val productId = ID.random()
                    val quantity = 3L
                    val price = 99.99

                    shouldNotThrowAny {
                        orderItemsQueries.createOrderItem(orderId, productId, quantity, price)
                    }

                    val orderItems = orderItemsQueries.getOrderItemsByOrderId(orderId).executeAsList()

                    orderItems.shouldExist { it.product_id == productId && it.quantity == quantity && it.price == price }
                }
            }
        }

        Given("a list of order items with a unique product ids") {
            And("an order id") {
                When("creating all of them using the same order id") {
                    Then("we should be able to query all of them by the same order id") {

                        val orderItems = mutableListOf<Order.OrderItem>()

                        val orderId = ID.random()

                        repeat(10) {

                            val orderItem = Order.OrderItem(
                                productId = ID.random(),
                                quantity = it.toLong(),
                                price = it.times(it).times(Random.nextDouble(100.0)),
                            )

                            orderItems.add(orderItem)
                        }

                        shouldNotThrowAny {

                            orderItems.forEach { orderItem ->
                                orderItemsQueries.createOrderItem(
                                    orderId,
                                    orderItem.productId,
                                    orderItem.quantity,
                                    orderItem.price
                                )
                            }
                        }

                        val dbOrderItems =
                            orderItemsQueries.getOrderItemsByOrderId(orderId) { _, product_id, quantity, price ->
                                Order.OrderItem(
                                    product_id,
                                    quantity,
                                    price
                                )
                            }.executeAsList().sortedBy { it.quantity }

                        dbOrderItems shouldHaveSize orderItems.size
                        dbOrderItems shouldBe orderItems
                    }
                }
            }
        }

        Given("a list of order items with a unique order ids") {
            And("a product id") {
                When("creating all of them using the same product id") {
                    Then("we should be able to query all of them by the same product id") {

                        val productId = ID.random()

                        val orderItem = Order.OrderItem(
                            productId = productId,
                            quantity = 3,
                            price = 3.times(Random.nextDouble(100.0)),
                        )

                        shouldNotThrowAny {

                            repeat(10) {
                                orderItemsQueries.createOrderItem(
                                    ID.random(),
                                    orderItem.productId,
                                    orderItem.quantity,
                                    orderItem.price
                                )
                            }
                        }

                        val dbOrderItems =
                            orderItemsQueries.getOrderItemsByProductId(productId) { _, product_id, quantity, price ->
                                Order.OrderItem(
                                    product_id,
                                    quantity,
                                    price
                                )
                            }.executeAsList().sortedBy { it.quantity }

                        dbOrderItems shouldHaveSize 10
                        dbOrderItems.all { it == orderItem }.shouldBeTrue()
                    }
                }
            }
        }

        Given("a list of order items with a non-unique order and product ids") {
            When("creating all of them using the same product id and the same order id") {
                Then("it should fail due unique constraint") {

                    val orderItems = mutableListOf<Order.OrderItem>()

                    val orderId = ID.random()
                    val productId = ID.random()

                    repeat(10) {

                        val orderItem = Order.OrderItem(
                            productId = productId,
                            quantity = it.toLong(),
                            price = it.times(it).times(Random.nextDouble(100.0)),
                        )

                        orderItems.add(orderItem)
                    }

                    shouldThrowAny {

                        orderItems.forEach { orderItem ->
                            orderItemsQueries.createOrderItem(
                                orderId,
                                orderItem.productId,
                                orderItem.quantity,
                                orderItem.price
                            )
                        }
                    }.message shouldContainIgnoringCase "UNIQUE CONSTRAINT FAILED"
                }
            }
        }
    }
}
