package com.shopping.queries

import com.shopping.DefaultSpec
import com.shopping.dataSourceModule
import com.shopping.db.CustomerCardsQueries
import com.shopping.dbModule
import com.shopping.domain.model.valueObject.ID
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.*
import org.koin.core.inject

class CustomerCardsQueriesTest : DefaultSpec(dbModule, dataSourceModule) {

    private val customerCardsQueries by inject<CustomerCardsQueries>()

    init {

        Given("a new customer card") {
            And("a unique customer id") {
                When("inserting the card into the db") {
                    Then("we should be able to query the card using the same customer id") {

                        val customerId = ID.random()

                        shouldNotThrowAny {
                            customerCardsQueries.createCard(
                                customerId,
                                ID.random().toString(),
                                4242,
                            )
                        }

                        val dbCards = customerCardsQueries.getCardsByCustomerId(customerId)
                            .executeAsList()

                        dbCards.shouldNotBeEmpty()
                        dbCards shouldExist { it.customer_id == customerId }
                        dbCards.any { it.card_last_4_numbers == 4242L }.shouldBeTrue()
                    }
                }
            }
        }

        Given("a customer card in the db") {
            And("a unique customer id") {
                When("deleting the card from the db based on customer id and stripe card id") {
                    Then("it should delete the card successfully") {

                        val customerId = ID.random()
                        val stripeCardId = ID.random().toString()

                        shouldNotThrowAny {
                            customerCardsQueries.createCard(
                                customerId,
                                stripeCardId,
                                4343,
                            )
                        }

                        customerCardsQueries.getCardsByCustomerId(customerId)
                            .executeAsList()
                            .shouldNotBeEmpty()

                        shouldNotThrowAny {
                            customerCardsQueries.deleteCard(customerId, stripeCardId)
                        }

                        customerCardsQueries.getCardsByCustomerId(customerId)
                            .executeAsList()
                            .shouldBeEmpty()
                    }
                }
            }
        }

        Given("a list of new customer cards") {
            And("a unique customer id") {
                When("inserting the cards into the db using the customer id") {
                    And("deleting them after by using the same customer id") {
                        Then("it should delete all the cards successfully") {

                            val customerId = ID.random()

                            repeat(10) {

                                customerCardsQueries.createCard(
                                    customerId,
                                    ID.random().toString(),
                                    "$it$it$it$it".toLong(),
                                )
                            }

                            customerCardsQueries.getCardsByCustomerId(customerId)
                                .executeAsList() shouldHaveAtLeastSize 10

                            shouldNotThrowAny {
                                customerCardsQueries.deleteCardsByCustomerId(customerId)
                            }

                            customerCardsQueries.getCardsByCustomerId(customerId)
                                .executeAsList() shouldHaveSize 0
                        }
                    }
                }
            }
        }
    }
}
