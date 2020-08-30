package com.shopping.queries

import com.shopping.DefaultSpec
import com.shopping.dataSourceModule
import com.shopping.db.ProductSpecs
import com.shopping.db.ProductSpecsQueries
import com.shopping.dbModule
import com.shopping.domain.model.valueObject.ID
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.koin.test.inject

class ProductSpecsQueriesTest : DefaultSpec(dbModule, dataSourceModule) {

    private val productSpecsQueries by inject<ProductSpecsQueries>()

    init {

        Given("a product id and a key value pair") {
            When("calling create spec") {
                Then("it should create it successfully") {

                    val productId = ID.random()

                    val spec = "Genre" to "Action"

                    productSpecsQueries.createSpec(productId, spec.first, spec.second)

                    val dbSpecs = productSpecsQueries.getSpecsByProductId(productId).executeAsList().map { dbSpec ->
                        dbSpec.toSpec()
                    }

                    dbSpecs.shouldNotBeEmpty()
                    dbSpecs shouldContain spec
                    dbSpecs[0] shouldBe spec

                }
            }
        }


        Given("a product id and a key value pair ") {
            When("calling create spec") {
                And("delete spec by key") {
                    Then("it should delete the spec matching the key") {

                        val productId = ID.random()

                        val spec = "Genre" to "Action"

                        productSpecsQueries.createSpec(productId, spec.first, spec.second)

                        val dbSpecs = productSpecsQueries.getSpecsByProductId(productId).executeAsList()

                        dbSpecs.size shouldBe 1

                        productSpecsQueries.deleteSpecByKey(productId, spec.first)

                        val dbSpecsAfterDeletion = productSpecsQueries.getSpecsByProductId(productId).executeAsList()

                        dbSpecsAfterDeletion.size shouldBe 0


                    }
                }
            }
        }

        Given("a product id and a list of specs") {
            When("calling create spec for each") {
                And("calling update spec by key") {
                    then("it should update the spec matching that key") {

                        val productId = ID.random()

                        val specs = mapOf("Platform" to "Linux", "Version" to "3.0.0", "Available since" to "18/7/2020")

                        specs.forEach { (key, value) ->
                            productSpecsQueries.createSpec(productId, key, value)
                        }

                        val dbSpecs = productSpecsQueries.getSpecsByProductId(productId).executeAsList()

                        dbSpecs.size shouldBe 3

                        productSpecsQueries.updateSpecByKey("Windows", productId, "Platform")

                        productSpecsQueries.getSpecsByProductId(productId).executeAsList()
                            .map { dbSpec -> dbSpec.toSpec() }
                            .any { it.second == "Windows" }.shouldBeTrue()

                    }
                }
            }
        }


        Given("a product id and list of db specs") {
            When("calling delete specs by product id") {
                then("it should delete all the specs matching that product id") {

                    val productId = ID.random()
                    val specs = mapOf("Platform" to "Linux", "Version" to "3.0.0", "Available since" to "18/7/2020")

                    specs.forEach { (key, value) ->
                        productSpecsQueries.createSpec(productId, key, value)
                    }

                    val dbSpecs = productSpecsQueries.getSpecsByProductId(productId).executeAsList()

                    dbSpecs.size shouldBe 3

                    productSpecsQueries.deleteSpecsByProductId(productId)

                    productSpecsQueries.getSpecsByProductId(productId).executeAsList().shouldBeEmpty()

                }
            }
        }

    }

}

private fun ProductSpecs.toSpec(): Pair<String, String> = key to value
