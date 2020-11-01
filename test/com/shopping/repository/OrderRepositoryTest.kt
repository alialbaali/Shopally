package com.shopping.repository

import com.shopping.*
import com.shopping.domain.repository.OrderRepository
import io.kotest.matchers.result.shouldBeSuccess
import org.koin.test.inject

class OrderRepositoryTest : DefaultSpec(testRepositoryModule, dataSourceModule, dbModule) {

    private val orderRepository by inject<OrderRepository>()

    init {

        Given("an order") {
            When("calling create order") {
                Then("it should create the order") {

                    orderRepository.createTestOrder()
                        .shouldBeSuccess()

                }
            }
        }

    }
}
