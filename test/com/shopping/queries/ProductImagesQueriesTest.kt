package com.shopping.queries

import com.shopping.DefaultSpec
import com.shopping.dataSourceModule
import com.shopping.db.ProductImagesQueries
import com.shopping.dbModule
import com.shopping.domain.model.valueObject.ID
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.collections.shouldHaveSize
import org.koin.test.inject

class ProductImagesQueriesTest : DefaultSpec(dbModule, dataSourceModule) {

    private val productImagesQueries by inject<ProductImagesQueries>()

    init {

        Given("a list of images") {
            And("a unique product id") {
                When("inserting all of them") {
                    Then("it should insert them successfully") {

                        val productId = ID.random()

                        shouldNotThrowAny {

                            repeat(10) {

                                productImagesQueries.createImageUrl(
                                    productId,
                                    ID.random().toString(),
                                )
                            }
                        }

                        productImagesQueries.getImagesUrlByProductId(productId)
                            .executeAsList() shouldHaveAtLeastSize 10
                    }
                }
            }
        }

        Given("a list of images ") {
            And("a unique product id") {
                When("inserting all of them") {
                    And("deleting the using the same product id") {
                        Then("it should delete all of them successfully") {

                            val productId = ID.random()

                            shouldNotThrowAny {

                                repeat(10) {

                                    productImagesQueries.createImageUrl(
                                        productId,
                                        ID.random().toString(),
                                    )
                                }
                            }

                            productImagesQueries.getImagesUrlByProductId(productId)
                                .executeAsList() shouldHaveAtLeastSize 10

                            productImagesQueries.deleteImagesByProductId(productId)

                            productImagesQueries.getImagesUrlByProductId(productId)
                                .executeAsList() shouldHaveSize 0
                        }
                    }
                }
            }
        }

        Given("a list of images with the same id") {
            And("a unique product id") {
                When("inserting all of them") {
                    Then("iit should fail due to unique constraint") {

                        val productId = ID.random()
                        val imageId = ID.random().toString()

                        shouldThrowAny {

                            repeat(10) {

                                productImagesQueries.createImageUrl(
                                    productId,
                                    imageId,
                                )
                            }
                        }

                        productImagesQueries.getImagesUrlByProductId(productId)
                            .executeAsList() shouldHaveAtMostSize 1
                    }
                }
            }
        }
    }
}
