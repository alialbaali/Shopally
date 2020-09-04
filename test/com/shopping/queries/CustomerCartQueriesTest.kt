package com.shopping.queries

import com.shopping.DefaultSpec
import com.shopping.dataSourceModule
import com.shopping.db.CustomerCartQueries
import com.shopping.dbModule
import com.shopping.domain.model.valueObject.ID
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.koin.test.inject
import kotlin.random.Random

class CustomerCartQueriesTest : DefaultSpec(dbModule, dataSourceModule) {

    private val customerCartQueries by inject<CustomerCartQueries>()

    init {

        Given("a list of order items") {
            And("a unique customer id") {
                When("creating the order items") {
                    Then("we should be able to query them by the customer id") {

                        val customerId = ID.random()

                        shouldNotThrowAny {
                            repeat(10) {
                                customerCartQueries.addOrderItem(
                                    customerId,
                                    product_id = ID.random(),
                                    quantity = it.toLong(),
                                    price = it.times(it).times(Random.nextDouble(100.0))
                                )
                            }
                        }

                        val orderItems = shouldNotThrowAny {
                            customerCartQueries.getOrderItemsByCustomerId(customerId).executeAsList()
                        }

                        orderItems shouldHaveSize 10
                    }
                }
            }
        }

        Given("a list of order items ") {
            And("a unique customer id") {
                When("creating the order items") {
                    And("update all of them") {
                        Then("all order items with that customer id should be updated") {

                            val customerId = ID.random()

                            shouldNotThrowAny {
                                repeat(10) {
                                    customerCartQueries.addOrderItem(
                                        customerId,
                                        product_id = ID.random(),
                                        quantity = it.toLong(),
                                        price = it.times(it).times(Random.nextDouble(100.0))
                                    )
                                }
                            }

                            val orderItems = shouldNotThrowAny {
                                customerCartQueries.getOrderItemsByCustomerId(customerId).executeAsList()
                            }

                            val updatedOrderItems = orderItems.map { orderItem ->
                                orderItem.copy(
                                    quantity = orderItem.quantity.times(orderItem.quantity)
                                )
                            }.sortedBy { it.quantity }

                            shouldNotThrowAny {
                                updatedOrderItems.forEach { orderItem ->
                                    customerCartQueries.updateOrderItem(
                                        orderItem.quantity,
                                        customerId,
                                        orderItem.product_id
                                    )
                                }
                            }

                            val dbOrderItems = customerCartQueries.getOrderItemsByCustomerId(customerId).executeAsList()
                                .sortedBy { it.quantity }

                            dbOrderItems shouldBe updatedOrderItems
                        }
                    }
                }
            }
        }

        Given("a list of order items  ") {
            And("a unique customer id") {
                When("creating the order items") {
                    Then("we should be able to delete all of them") {

                        val customerId = ID.random()

                        shouldNotThrowAny {
                            repeat(10) {
                                customerCartQueries.addOrderItem(
                                    customerId,
                                    product_id = ID.random(),
                                    quantity = it.toLong(),
                                    price = it.times(it).times(Random.nextDouble(100.0))
                                )
                            }
                        }

                        customerCartQueries.getOrderItemsByCustomerId(customerId).executeAsList() shouldHaveSize 10

                        customerCartQueries.deleteOrderItems(customerId)

                        customerCartQueries.getOrderItemsByCustomerId(customerId).executeAsList().shouldBeEmpty()
                    }
                }
            }
        }
    }
}
