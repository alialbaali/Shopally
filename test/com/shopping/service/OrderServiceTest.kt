package com.shopping.service

import com.shopping.*
import com.shopping.domain.dto.order.request.CreateOrderRequest
import com.shopping.domain.repository.ProductRepository
import com.shopping.domain.service.AuthService
import com.shopping.domain.service.CustomerService
import com.shopping.domain.service.OrderService
import io.kotest.assertions.throwables.shouldNotThrowAny
import org.koin.test.inject

class OrderServiceTest : DefaultSpec(testServiceModule, fakeRepositoryModule, helperModule) {

    private val orderService by inject<OrderService>()
    private val customerService by inject<CustomerService>()
    private val productRepository by inject<ProductRepository>()
    private val authService by inject<AuthService>()

    init {

        Given("a customer ") {
            When("calling create order") {
                Then("it should create the order successfully") {

                    val (customerId) = authService.signUpTestCustomer()

                    val (addressName) = customerService.createTestAddress(customerId)
                    val cardLast4Numbers = customerService.createTestCard(customerId).cardLast4Numbers
                    val (productId) = productRepository.createTestProducts().random()
                    customerService.createTestCartItems(customerId, productId.toString())

                    val createOrderRequest = CreateOrderRequest(addressName, cardLast4Numbers)

                    shouldNotThrowAny {
                        orderService.createOrder(customerId, createOrderRequest)
                    }

                }
            }
        }

    }

}

