package com.shopping.queries

import com.shopping.DefaultSpec
import com.shopping.dataSourceModule
import com.shopping.domain.StripeCustomerDataSource
import com.stripe.Stripe
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import org.koin.test.inject

class StripeCustomerDataSourceTest : DefaultSpec(dataSourceModule) {

    private val stripeCustomerDataSource by inject<StripeCustomerDataSource>()

    init {

        Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc"

        Given("a name and an email") {
            When("creating stripe customer") {
                Then("it should create it successfully") {

                    val name = "John doe"
                    val email = "johndoe@mail.com"

                    val stripeCustomer = shouldNotThrowAny {
                        stripeCustomerDataSource.createStripeCustomer(name, email)
                            .getOrThrow()
                    }

                    stripeCustomer.name shouldBeEqualIgnoringCase name
                    stripeCustomer.email shouldBeEqualIgnoringCase email
                }
            }
        }

        Given("a stripe customer") {
            When("creating the customer") {
                And("updating the customer") {
                    Then("it should update it successfully") {

                        val name = "John doe"
                        val email = "johndoe@mail.com"

                        val stripeCustomer = shouldNotThrowAny {
                            stripeCustomerDataSource.createStripeCustomer(name, email)
                                .getOrThrow()
                        }

                        val updatedName = "Mark Nike"
                        val updatedEmail = "marknike@mail.com"

                        val updatedStripeCustomer = shouldNotThrowAny {
                            stripeCustomerDataSource.updateStripeCustomerById(
                                stripeCustomer.id,
                                updatedName,
                                updatedEmail
                            ).getOrThrow()
                        }

                        updatedStripeCustomer.name shouldNotBe stripeCustomer.name
                        updatedStripeCustomer.email shouldNotBe stripeCustomer.email
                    }
                }
            }
        }

        Given("a stripe customer id") {
            When("deleting the customer") {
                Then("it should delete it successfully") {

                    val name = "John doe"
                    val email = "johndoe@mail.com"

                    val stripeCustomerId = stripeCustomerDataSource.createStripeCustomer(name, email).getOrThrow().id

                    shouldNotThrowAny {
                        stripeCustomerDataSource.deleteStripeCustomerById(stripeCustomerId).getOrThrow()
                    } shouldBe stripeCustomerId
                }
            }
        }
    }
}
