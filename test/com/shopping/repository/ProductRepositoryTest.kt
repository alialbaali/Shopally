package com.shopping.repository

import com.shopping.DefaultSpec
import com.shopping.domain.repository.ProductRepository
import com.shopping.mockDataSourceModule
import com.shopping.testRepositoryModule
import io.kotest.assertions.throwables.shouldThrowAny
import org.koin.test.inject

class ProductRepositoryTest : DefaultSpec(testRepositoryModule, mockDataSourceModule) {

    private val productRepository by inject<ProductRepository>()

    init {

        Given("a empty list of products") {
            When("calling get products") {
                Then("it should return an error") {

                    shouldThrowAny {
                        productRepository.getProducts(100, 0, emptySet(), emptySet(), null, null)
                    }

                }
            }
        }


    }
}
