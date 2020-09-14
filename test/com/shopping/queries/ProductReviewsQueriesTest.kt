package com.shopping.queries

import com.shopping.DefaultSpec
import com.shopping.dataSourceModule
import com.shopping.db.ProductReviewsQueries
import com.shopping.dbModule
import com.shopping.domain.model.valueObject.ID
import com.shopping.domain.model.valueObject.Rating
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.koin.test.inject
import java.time.LocalDate

class ProductReviewsQueriesTest : DefaultSpec(dbModule, dataSourceModule) {

    private val productReviewsQueries by inject<ProductReviewsQueries>()

    init {

        Given("a new review") {
            When("inserting the review into the db") {
                Then("it should insert it successfully") {

                    val productId = ID.random()
                    val customerId = ID.random()

                    shouldNotThrowAny {

                        productReviewsQueries.createReview(
                            productId,
                            customerId,
                            Rating.Three,
                            null,
                            LocalDate.now()
                        )
                    }

                    productReviewsQueries.getReview(productId, customerId)
                        .executeAsOneOrNull()
                        .shouldNotBeNull()
                }
            }
        }

        Given("a list of reviews each with unique customer id") {
            When("inserting all the reviews into the db") {
                Then("we should be able to query them all") {

                    val productId = ID.random()

                    shouldNotThrowAny {

                        repeat(10) {

                            productReviewsQueries.createReview(
                                productId,
                                ID.random(),
                                Rating.Three,
                                null,
                                LocalDate.now()
                            )
                        }
                    }

                    productReviewsQueries.getReviewsByProductId(productId)
                        .executeAsList() shouldHaveAtMostSize 10
                }
            }
        }

        Given("a review in the db") {
            When("updating the review using the product and customer ids") {
                Then("it should update it successfully") {

                    val productId = ID.random()
                    val customerId = ID.random()

                    shouldNotThrowAny {

                        productReviewsQueries.createReview(
                            productId,
                            customerId,
                            Rating.Three,
                            null,
                            LocalDate.now()
                        )
                    }

                    productReviewsQueries.getReview(productId, customerId)
                        .executeAsOneOrNull()
                        .apply {
                            this.shouldNotBeNull()
                            this.rating shouldBe Rating.Three
                            this.description.shouldBeNull()
                        }

                    productReviewsQueries.updateReview(
                        Rating.Five,
                        "Great Product",
                        productId,
                        customerId
                    )

                    productReviewsQueries.getReview(productId, customerId)
                        .executeAsOneOrNull()
                        .apply {
                            shouldNotBeNull()
                            rating shouldBe Rating.Five
                            description.shouldNotBeNull()
                        }
                }
            }
        }

        Given("a review in the db ") {
            When("deleting the review by using customer and product id") {
                Then("it should delete it successfully") {

                    val productId = ID.random()
                    val customerId = ID.random()

                    shouldNotThrowAny {

                        productReviewsQueries.createReview(
                            productId,
                            customerId,
                            Rating.Three,
                            null,
                            LocalDate.now()
                        )
                    }

                    productReviewsQueries.getReview(productId, customerId)
                        .executeAsOneOrNull()
                        .shouldNotBeNull()

                    productReviewsQueries.deleteReview(productId, customerId)

                    productReviewsQueries.getReview(productId, customerId)
                        .executeAsOneOrNull()
                        .shouldBeNull()
                }
            }
        }

        Given("a list of reviews each with a non-unique customer id") {
            And("a non-unique product id") {
                When("inserting all the reviews into the db") {
                    Then("we should be able to query them all") {

                        val productId = ID.random()
                        val customerId = ID.random()

                        shouldThrowAny {

                            repeat(10) {

                                productReviewsQueries.createReview(
                                    productId,
                                    customerId,
                                    Rating.Three,
                                    null,
                                    LocalDate.now()
                                )
                            }
                        }

                        productReviewsQueries.getReviewsByProductId(productId)
                            .executeAsList() shouldHaveAtMostSize 1
                    }
                }
            }
        }
    }
}
