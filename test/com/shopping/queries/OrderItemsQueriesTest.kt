package com.shopping.queries

import com.shopping.DefaultSpec
import com.shopping.dataSourceModule
import com.shopping.db.OrderItemsQueries
import com.shopping.dbModule
import com.shopping.domain.model.valueObject.ID
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.shouldExist
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.string.shouldContainIgnoringCase
import org.koin.test.inject

class OrderItemsQueriesTest : DefaultSpec(dbModule, dataSourceModule) {

    private val orderItemsQueries by inject<OrderItemsQueries>()

    init {

        Given("an order id, product id, quantity and a price") {
            When("creating an order item") {
                Then("it should create an order item successfully") {

                    val orderId = ID.random()
                    val productId = ID.random()
                    val quantity = 3L

                    shouldNotThrowAny {
                        orderItemsQueries.createOrderItem(orderId, productId, quantity)
                    }

                    orderItemsQueries.getOrderItemsByOrderId(orderId)
                        .executeAsList() shouldExist { it.product_id == productId && it.quantity == quantity }
                }
            }
        }

        Given("a list of order items with a unique product ids") {
            And("an order id") {
                When("creating all of them using the same order id") {
                    Then("we should be able to query all of them by the same order id") {

                        val orderId = ID.random()

                        shouldNotThrowAny {

                            repeat(10) {

                                orderItemsQueries.createOrderItem(
                                    orderId,
                                    ID.random(),
                                    it.times(2L)
                                )
                            }
                        }

                        orderItemsQueries.getOrderItemsByOrderId(orderId)
                            .executeAsList() shouldHaveAtLeastSize 10
                    }
                }
            }
        }

        Given("a list of order items with a unique order ids") {
            And("a product id") {
                When("creating all of them using the same product id") {
                    Then("we should be able to query all of them by the same product id") {

                        val productId = ID.random()

                        shouldNotThrowAny {

                            repeat(10) {
                                orderItemsQueries.createOrderItem(
                                    ID.random(),
                                    productId,
                                    it.times(2L),
                                )
                            }
                        }

                        orderItemsQueries.getOrderItemsByProductId(productId)
                            .executeAsList() shouldHaveAtLeastSize 10
                    }
                }
            }
        }

        Given("a list of order items with a non-unique order and product ids") {
            When("creating all of them using the same product id and the same order id") {
                Then("it should fail due to unique constraint") {

                    val orderId = ID.random()
                    val productId = ID.random()

                    shouldThrowAny {

                        repeat(10) {

                            orderItemsQueries.createOrderItem(
                                orderId,
                                productId,
                                it.times(2L),
                            )
                        }
                    }.message shouldContainIgnoringCase "UNIQUE CONSTRAINT FAILED"
                }
            }
        }
    }
}
