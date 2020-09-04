package com.shopping.queries

import com.shopping.DefaultSpec
import com.shopping.dataSourceModule
import com.shopping.db.CustomerAddressesQueries
import com.shopping.dbModule
import com.shopping.domain.model.valueObject.ID
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.koin.test.inject

class AddressesQueriesTest : DefaultSpec(dbModule, dataSourceModule) {

    private val customerAddressesQueries by inject<CustomerAddressesQueries>()

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

                        customerAddressesQueries.createAddress(
                            customerId,
                            name,
                            country,
                            city,
                            line,
                            zipCode
                        )

                        val addresses = customerAddressesQueries.getAddressesByCustomerID(customerId).executeAsList()

                        val addressesCount = customerAddressesQueries.countAddressesByCustomerId(customerId).executeAsOne()

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

                        customerAddressesQueries.createAddress(
                            customerId,
                            name,
                            country,
                            city,
                            line,
                            zipCode
                        )
                    }

                    val addressesCount = customerAddressesQueries.countAddressesByCustomerId(customerId).executeAsOne()

                    addressesCount shouldBeGreaterThanOrEqual 5

                    customerAddressesQueries.deleteAddressesByCustomerID(customerId)

                    val addressesCountAfterDeletion =
                        customerAddressesQueries.countAddressesByCustomerId(customerId).executeAsOne()

                    addressesCountAfterDeletion shouldBeExactly 0
                }
            }
        }
    }
}
