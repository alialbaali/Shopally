package com.shopping.queries

import com.shopping.DefaultSpec
import com.shopping.dataSourceModule
import com.shopping.db.ProductsQueries
import com.shopping.dbModule
import com.shopping.domain.model.Product
import com.shopping.domain.model.valueObject.ID
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import org.koin.test.inject
import java.time.LocalDate

class ProductsQueriesTest : DefaultSpec(dbModule, dataSourceModule) {

    private val productsQueries by inject<ProductsQueries>()

    init {

        Given("a list of products in the db") {
            And("a limit and offset") {
                When("getting products in the db") {
                    Then("it should return products based on limit and offset") {

                        val limit = 100L
                        val offset = 50L

                        shouldNotThrowAny {

                            repeat(500) {

                                productsQueries.createProduct(
                                    ID.random(),
                                    Product.Category.VideoGames,
                                    "Epic",
                                    "Fortnite",
                                    "Shooter game",
                                    0.0,
                                    LocalDate.now().minusYears(3),
                                    LocalDate.now(),
                                )
                            }
                        }

                        productsQueries.getProducts(limit, offset)
                            .executeAsList() shouldHaveSize 100
                    }
                }
            }
        }

        Given("a new list of products with a non-unique id") {
            When("getting products in the db") {
                Then("it should fail due to unique constraint") {

                    val productId = ID.random()

                    shouldThrowAny {

                        repeat(10) {

                            productsQueries.createProduct(
                                productId,
                                Product.Category.VideoGames,
                                "Epic",
                                "Fortnite",
                                "Shooter game",
                                0.0,
                                LocalDate.now().minusYears(3),
                                LocalDate.now(),
                            )
                        }
                    }

                    productsQueries.getProductById(productId)
                        .executeAsOneOrNull()
                        .shouldNotBeNull()
                }
            }
        }

        Given("a new product") {
            When("inserting the product into the db") {
                Then("we should be able to query the product using the product id") {

                    val productId = ID.random()

                    shouldNotThrowAny {

                        productsQueries.createProduct(
                            productId,
                            Product.Category.VideoGames,
                            "Epic",
                            "Fortnite",
                            "Shooter game",
                            0.0,
                            LocalDate.now().minusYears(3),
                            LocalDate.now(),
                        )
                    }

                    productsQueries.getProductById(productId)
                        .executeAsOneOrNull()
                        .shouldNotBeNull()
                }
            }
        }

        Given("a product in the db") {
            When("updating the product") {
                Then("it should update it successfully") {

                    val productId = ID.random()

                    shouldNotThrowAny {

                        productsQueries.createProduct(
                            productId,
                            Product.Category.VideoGames,
                            "Epic",
                            "Fortnite",
                            "Shooter game",
                            0.0,
                            LocalDate.now().minusYears(3),
                            LocalDate.now(),
                        )
                    }

                    productsQueries.getProductById(productId)
                        .executeAsOneOrNull()
                        .apply {
                            shouldNotBeNull()
                            name shouldBeEqualIgnoringCase "Fortnite"
                            price shouldBe 0.0
                        }

                    productsQueries.updateProductByID(
                        Product.Category.VideoGames,
                        "Mediatonic",
                        "Fall Guys",
                        "Super fun game",
                        30.0,
                        LocalDate.now().minusMonths(2),
                        productId
                    )

                    productsQueries.getProductById(productId)
                        .executeAsOneOrNull()
                        .apply {
                            shouldNotBeNull()
                            name shouldBeEqualIgnoringCase "Fall Guys"
                            price shouldBe 30.0
                        }
                }
            }
        }

        Given("a product in the db ") {
            When("deleting the product") {
                Then("it should delete the product successfully") {

                    val productId = ID.random()

                    shouldNotThrowAny {

                        productsQueries.createProduct(
                            productId,
                            Product.Category.VideoGames,
                            "Epic",
                            "Fortnite",
                            "Shooter game",
                            0.0,
                            LocalDate.now().minusYears(3),
                            LocalDate.now(),
                        )
                    }

                    productsQueries.getProductById(productId)
                        .executeAsOneOrNull()
                        .shouldNotBeNull()

                    productsQueries.deleteProductById(productId)

                    productsQueries.getProductById(productId)
                        .executeAsOneOrNull()
                        .shouldBeNull()
                }
            }
        }
    }
}
