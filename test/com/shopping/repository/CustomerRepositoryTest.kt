package com.shopping.repository

import com.shopping.*
import com.shopping.domain.model.valueObject.ID
import com.shopping.domain.repository.CustomerRepository
import com.stripe.Stripe
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.koin.test.inject


class CustomerRepositoryTest : DefaultSpec(repositoryModule, dataSourceModule, dbModule) {

    private val customerRepository by inject<CustomerRepository>()

    init {

        Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc"

        Given("an invalid customer id") {
            When("calling get customer by id") {
                Then("it should fail") {

                    customerRepository
                        .getCustomerById(ID.random())
                        .shouldBeFailure()
                }
            }
        }

        Given("an invalid customer email") {
            When("calling get customer by email") {
                Then("it should fail") {

                    customerRepository
                        .getCustomerByEmail("johnDoe@mail.com".asEmail())
                        .shouldBeFailure()

                }
            }
        }

        Given("a new customer with a unique email and id") {
            When("calling create customer") {
                Then("it should succeed") {

                    val customer = shouldNotThrowAny {
                        customerRepository.createTestCustomer(email = "Johndoe2@mail.com")
                            .getOrThrow()
                    }

                    customerRepository
                        .getCustomerById(customer.id)
                        .shouldBeSuccess()

                    customerRepository
                        .getCustomerByEmail(customer.email)
                        .shouldBeSuccess()

                }
            }
        }

        Given("an invalid customer with a non-unique email") {
            When("calling create customer") {
                Then("it should fail due to unique constraint") {

                    val customer = shouldNotThrowAny {
                        customerRepository.createTestCustomer()
                            .getOrThrow()
                    }

                    customerRepository
                        .createCustomer(customer)
                        .shouldBeFailure()

                }
            }
        }

        Given("a customer in the db") {
            When("calling update customer") {
                Then("it should succeed") {

                    val customer = shouldNotThrowAny {
                        customerRepository.createTestCustomer(email = "MarkDoe@mail.com")
                            .getOrThrow()
                    }


                    customerRepository
                        .updateCustomer(customer.copy(name = "Mark Doe"))
                        .shouldBeSuccess()

                    customerRepository
                        .getCustomerByEmail(customer.email)
                        .shouldBeSuccess {
                            it.shouldNotBeNull()
                            it.name shouldBe "Mark Doe"
                        }

                }
            }
        }

        Given("a customer in the db ") {
            When("calling delete customer") {
                Then("it should succeed") {

                    val customer = shouldNotThrowAny {
                        customerRepository.createTestCustomer(email = "MarkDoe6@mail.com")
                            .getOrThrow()
                    }

                    customerRepository
                        .deleteCustomerById(customer.id)
                        .shouldBeSuccess()

                    customerRepository
                        .getCustomerByEmail(customer.email)
                        .shouldBeFailure()

                }
            }
        }

        Given("an invalid customer id ") {
            When("calling get cart by customer id") {
                Then("it should fail") {

                    val customer = shouldNotThrowAny {
                        customerRepository.createTestCustomer(email = "MarkDoe@mail.com")
                            .getOrThrow()
                    }

                    customerRepository
                        .getCartByCustomerId(customer.id)
                        .getOrThrow()

                }
            }
        }

    }
}
