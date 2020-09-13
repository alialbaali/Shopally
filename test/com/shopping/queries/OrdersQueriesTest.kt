package com.shopping.queries

import com.shopping.DefaultSpec
import com.shopping.dataSourceModule
import com.shopping.db.OrdersQueries
import com.shopping.dbModule
import com.shopping.domain.model.valueObject.ID
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.string.shouldContainIgnoringCase
import org.koin.core.inject
import java.time.LocalDate

class OrdersQueriesTest : DefaultSpec(dbModule, dataSourceModule) {

    private val ordersQueries by inject<OrdersQueries>()

    init {

        Given("a list of orders") {
            And("a unique customer id") {
                When("inserting the order into the db") {
                    Then("we should be able to query it using the same customer id") {

                        val customerId = ID.random()

                        shouldNotThrowAny {

                            repeat(10) {

                                ordersQueries.createOrder(
                                    ID.random(),
                                    customerId,
                                    4242,
                                    "Home",
                                    LocalDate.now(),
                                )
                            }
                        }

                        ordersQueries.getOrdersByCustomerId(customerId)
                            .executeAsList() shouldHaveAtLeastSize 10
                    }
                }
            }
        }

        Given("a list of orders with same order id") {
            And("a unique customer id") {
                When("inserting the order into the db") {
                    Then("it should fail due to unique constraint") {

                        val customerId = ID.random()
                        val orderId = ID.random()

                        shouldThrowAny {

                            repeat(10) {

                                ordersQueries.createOrder(
                                    orderId,
                                    customerId,
                                    4242,
                                    "Home",
                                    LocalDate.now(),
                                )
                            }
                        }.message shouldContainIgnoringCase "UNIQUE CONSTRAINT FAILED"

                        ordersQueries.getOrdersByCustomerId(customerId)
                            .executeAsList() shouldHaveAtMostSize 1
                    }
                }
            }
        }
    }
}
