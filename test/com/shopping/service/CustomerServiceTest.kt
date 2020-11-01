package com.shopping.service

import com.shopping.*
import com.shopping.domain.dto.customer.request.*
import com.shopping.domain.repository.ProductRepository
import com.shopping.domain.service.AuthService
import com.shopping.domain.service.CustomerService
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import org.koin.test.get
import org.koin.test.inject
import java.io.InputStream

class CustomerServiceTest : DefaultSpec(testServiceModule, fakeRepositoryModule, helperModule) {

    private val customerService by inject<CustomerService>()
    private val authService by inject<AuthService>()

    init {

        Given("a customer in the db") {
            When("calling get customer by id") {
                Then("it should return a customer matching that id") {

                    val (customerId) = shouldNotThrowAny { authService.signUpTestCustomer() }

                    shouldNotThrowAny {
                        customerService.getCustomerById(customerId)
                    }.apply {
                        name shouldBe "John Doe"
                        email shouldBe "johndoe@mail.com"
                    }

                }
            }
        }

        Given("a customer in the db ") {
            When("calling update customer by id") {
                Then("it should update the customer") {

                    val (customerId) = shouldNotThrowAny { authService.signUpTestCustomer() }

                    val updateCustomerRequest = UpdateCustomerRequest(
                        name = "Mark Doe",
                        email = "markdoe@mail.com",
                    )

                    shouldNotThrowAny {
                        customerService.updateCustomerById(customerId, updateCustomerRequest)
                    }.apply {
                        name shouldBe updateCustomerRequest.name
                        email shouldBe updateCustomerRequest.email
                    }

                }
            }
        }

        Given("a customer in the db  ") {
            When("updating customer password") {
                Then("it should succeed") {

                    val (customerId) = shouldNotThrowAny { authService.signUpTestCustomer() }

                    val updateCustomerPassword = UpdateCustomerPasswordRequest(
                        oldPassword = "johndoe0",
                        newPassword = "doejohn1",
                    )

                    shouldNotThrowAny {
                        customerService.updateCustomerPassword(customerId, updateCustomerPassword)
                    }
                }
            }
        }

        Given(" a customer in the db") {
            When("updating customer image ") {
                Then("it should return an image URI") {

                    val (customerId) = shouldNotThrowAny { authService.signUpTestCustomer() }

                    shouldNotThrowAny {
                        customerService.updateCustomerImageById(
                            customerId,
                            InputStream.nullInputStream()
                        )
                    }
                }
            }
        }

        Given("  a customer in the db ") {
            When("deleting the customer") {
                Then("it should delete it successfully") {

                    val (customerId) = shouldNotThrowAny { authService.signUpTestCustomer() }

                    shouldNotThrowAny {
                        customerService.deleteCustomer(customerId)
                    } shouldBe customerId
                }
            }
        }

        Given(" a customer id") {
            When("calling create cart item") {
                Then("it should create a cart item successfully") {

                    val (productId) = get<ProductRepository>().createTestProducts().random()

                    val (customerId) = shouldNotThrowAny { authService.signUpTestCustomer(email = "ivy@mail.com") }

                    val createCartItemRequest = CreateCartItemRequest(
                        productId.toString(),
                        3
                    )

                    shouldNotThrowAny {
                        customerService.createCartItem(customerId, createCartItemRequest)
                    }.apply {
                        quantity shouldBe createCartItemRequest.quantity
                    }

                }
            }
        }

        Given("a customerId") {
            When("calling create address") {
                Then("it should create an address successfully") {

                    val (customerId) = shouldNotThrowAny { authService.signUpTestCustomer(email = "markdoe6@mail.com") }

                    val createAddressRequest = CreateAddressRequest(
                        "Home",
                        "US",
                        "LA",
                        "Unknown",
                        "2032",
                    )

                    customerService.createAddressByCustomerId(customerId, createAddressRequest)
                        .apply {
                            name shouldBe createAddressRequest.name
                            country shouldBe createAddressRequest.country
                            city shouldBe createAddressRequest.city
                            line shouldBe createAddressRequest.line
                            zipCode shouldBe createAddressRequest.zipCode
                        }

                }
            }
        }

        Given("a  customerId ") {
            When("calling delete address by name") {
                Then("it should delete the address successfully") {

                    val (customerId) = shouldNotThrowAny { authService.signUpTestCustomer(email = "markdoe5@mail.com") }

                    val createAddressRequest = CreateAddressRequest(
                        "Home",
                        "US",
                        "LA",
                        "Unknown",
                        "2032",
                    )

                    shouldNotThrowAny {
                        customerService.createAddressByCustomerId(customerId, createAddressRequest)
                    }

                    shouldNotThrowAny {
                        customerService.deleteAddressByName(customerId, createAddressRequest.name!!)
                    }

                }
            }
        }

        Given("a customerId ") {
            When("calling create card") {
                Then("it should create an card successfully") {

                    val (customerId) = shouldNotThrowAny { authService.signUpTestCustomer(email = "Markdoe2@mail.com") }

                    val createCardRequest = CreateCardRequest(
                        number = "4242_4242_4242_4242".replace("_", ""),
                        expMonth = 12,
                        expYear = 2030,
                        cvc = 232,
                    )

                    val cardResponse = customerService.createCardByCustomerId(customerId, createCardRequest)

                    cardResponse.cardLast4Numbers shouldBe createCardRequest.number?.takeLast(4)
                    cardResponse.expMonth shouldBe createCardRequest.expMonth
                    cardResponse.expYear shouldBe createCardRequest.expYear
                }
            }
        }

        Given(" a customer id ") {
            When("calling delete card by last 4 numbers") {
                Then("it should delete the card") {

                    val (customerId) = shouldNotThrowAny { authService.signUpTestCustomer(email = "Markdoe3@mail.com") }

                    val createCardRequest = CreateCardRequest(
                        number = "4242_4242_4242_4242".replace("_", ""),
                        expMonth = 12,
                        expYear = 2030,
                        cvc = 232,
                    )

                    val cardResponse = customerService.createCardByCustomerId(customerId, createCardRequest)

                    shouldNotThrowAny {
                        customerService.deleteCartByLast4(customerId, cardResponse.cardLast4Numbers.toLong())
                    }

                    customerService.getCardsByCustomerId(customerId)
                        .shouldNotContain(cardResponse)

                }
            }
        }


    }

}
