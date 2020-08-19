package com.shopping.queries

import com.shopping.KoinTestListener
import com.shopping.db.AddressesQueries
import com.shopping.di.dataSourceModule
import com.shopping.di.dbModule
import com.shopping.domain.model.valueObject.ID
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.koin.test.KoinTest
import org.koin.test.inject

class AddressesQueriesTest : BehaviorSpec(), KoinTest {

    override fun listeners(): List<TestListener> = super.listeners().plus(KoinTestListener(dbModule, dataSourceModule))

    private val addressesQueries by inject<AddressesQueries>()

    init {

        Given("a new address") {
            And("a customer id") {
                When("calling create address") {
                    Then("it should create new address in the database") {

                        val customerId = ID.random()
                        val name = "Home"
                        val country = "US"
                        val city = "NYC"
                        val line = "Line"
                        val zipCode = "5438"

                        addressesQueries.createAddress(
                            customerId,
                            name,
                            country,
                            city,
                            line,
                            zipCode
                        )


                        val addresses = addressesQueries.getAddressesByCustomerID(customerId).executeAsList()
                        val addressesCount = addressesQueries.countAddressesByCustomerId(customerId).executeAsOne()

                        addresses.shouldNotBeEmpty()
                        addressesCount shouldBe 1
                        addresses.find { it.name == name }.shouldNotBeNull()

                    }
                }
            }
        }

        Given("a 5 addresses in the database") {
            When("calling delete addresses by customer id") {
                Then("it should delete all addresses of that customer id") {

                    val customerId = ID.random()

                    (1..5).forEach {
                        val name = "Home $it"
                        val country = "US $it"
                        val city = "NYC $it"
                        val line = "Line $it"
                        val zipCode = "5438 $it"

                        addressesQueries.createAddress(
                            customerId,
                            name,
                            country,
                            city,
                            line,
                            zipCode
                        )

                    }

                    val addressesCount = addressesQueries.countAddressesByCustomerId(customerId).executeAsOne()

                    addressesCount shouldBeGreaterThanOrEqual 5

                    addressesQueries.deleteAddressesByCustomerID(customerId)

                    val addressesCountAfterDeletion = addressesQueries.countAddressesByCustomerId(customerId).executeAsOne()

                    addressesCountAfterDeletion shouldBeExactly 0

                }
            }
        }

    }

}
