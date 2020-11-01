package com.shopping.service

import com.shopping.DefaultSpec
import com.shopping.createTestProducts
import com.shopping.domain.repository.ProductRepository
import com.shopping.domain.service.AuthService
import com.shopping.domain.service.ProductService
import com.shopping.fakeRepositoryModule
import com.shopping.testServiceModule
import io.kotest.matchers.collections.shouldBeEmpty
import org.koin.test.get
import org.koin.test.inject

class ProductServiceTest : DefaultSpec(testServiceModule, fakeRepositoryModule) {

    private val productService by inject<ProductService>()

    init {

        Given("an empty list of products") {
            When("calling get products"){
                Then("it should return an empty list") {

                    productService
                        .getProducts(null, null, null, null, null, null, null, null, null)
                        .shouldBeEmpty()

                }
            }
        }

    }
}
