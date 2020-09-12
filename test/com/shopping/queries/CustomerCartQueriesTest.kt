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

class CustomerCartQueriesTest : DefaultSpec(dbModule, dataSourceModule) {

    private val customerCartQueries by inject<CustomerCartQueries>()

    init {

        Given("a list of cart items") {
            And("a unique customer id") {
                When("creating the cart items") {
                    Then("we should be able to query them by the customer id") {

                        val customerId = ID.random()

                        shouldNotThrowAny {
                            repeat(10) {
                                customerCartQueries.createCartItem(
                                    customerId,
                                    product_id = ID.random(),
                                    quantity = it.toLong(),
                                )
                            }
                        }

                        val cartItems = shouldNotThrowAny {
                            customerCartQueries.getCartItemsByCustomerId(customerId).executeAsList()
                        }

                        cartItems shouldHaveSize 10
                    }
                }
            }
        }

        Given("a list of cart items ") {
            And("a unique customer id") {
                When("creating the cart items") {
                    And("update all of them") {
                        Then("all cart items with that customer id should be updated") {

                            val customerId = ID.random()

                            shouldNotThrowAny {
                                repeat(10) {
                                    customerCartQueries.createCartItem(
                                        customerId,
                                        product_id = ID.random(),
                                        quantity = it.toLong(),
                                    )
                                }
                            }

                            val updatedCartItems = customerCartQueries.getCartItemsByCustomerId(customerId)
                                .executeAsList()
                                .map { cartItem ->
                                    cartItem.copy(quantity = cartItem.quantity.times(cartItem.quantity))
                                }.sortedBy { it.quantity }

                            shouldNotThrowAny {
                                updatedCartItems.forEach { cartItem ->
                                    customerCartQueries.updateCartItem(
                                        cartItem.quantity,
                                        customerId,
                                        cartItem.product_id
                                    )
                                }
                            }

                            val dbCartItems = customerCartQueries.getCartItemsByCustomerId(customerId)
                                .executeAsList()
                                .sortedBy { it.quantity }

                            dbCartItems shouldBe updatedCartItems
                        }
                    }
                }
            }
        }

        Given("a non-empty cart") {
            And("a unique customer id") {
                When("clearing the cart by customer id") {
                    Then("it should delete all the cart items with that customer id") {

                        val customerId = ID.random()

                        shouldNotThrowAny {
                            repeat(10) {
                                customerCartQueries.createCartItem(
                                    customerId,
                                    product_id = ID.random(),
                                    quantity = it.toLong(),
                                )
                            }
                        }

                        customerCartQueries.getCartItemsByCustomerId(customerId)
                            .executeAsList() shouldHaveSize 10

                        customerCartQueries.deleteCartItemsByCustomerId(customerId)

                        customerCartQueries.getCartItemsByCustomerId(customerId)
                            .executeAsList()
                            .shouldBeEmpty()
                    }
                }
            }
        }
    }
}
