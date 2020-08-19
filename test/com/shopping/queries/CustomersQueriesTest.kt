package com.shopping.queries

import com.shopping.KoinTestListener
import com.shopping.db.CustomersQueries
import com.shopping.di.dataSourceModule
import com.shopping.di.dbModule
import com.shopping.domain.model.Customer
import com.shopping.domain.model.valueObject.Email
import com.shopping.domain.model.valueObject.ID
import com.shopping.domain.model.valueObject.Password
import com.shopping.hash
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.LocalDate

class CustomersQueriesTest : BehaviorSpec(), KoinTest {

    override fun listeners(): List<TestListener> = listOf(KoinTestListener(dbModule, dataSourceModule))

    private val customersQueries by inject<CustomersQueries>()

    init {

        Given("a new customer instance") {
            When("calling creating customer") {
                Then("it should insert a new customer in the database") {

                    val customerId = ID.random()

                    val name = "John Doe"
                    val email = Email.create("johndoe@mail.com").getOrThrow()
                    val password = Password.create("password0") { hash() }.getOrThrow()

                    customersQueries.createCustomer(
                        customerId,
                        name,
                        email,
                        password,
                        image = String(),
                        creation_date = LocalDate.now(),
                    )

                    val dbCustomer = customersQueries.getCustomerById(customerId).executeAsOneOrNull()

                    dbCustomer.shouldNotBeNull()
                    dbCustomer.email shouldBe email
                    dbCustomer.name shouldBe name

                }
            }
        }

        Given("a new customer") {
            And("an updated customer with the same id") {
                When("calling update customer") {
                    Then("it should update the customer in the database matching that id") {

                        val customerId = ID.random()

                        val name = "John Doe 1"
                        val email = Email.create("johndoe1@mail.com").getOrThrow()
                        val password = Password.create("password1") { hash() }.getOrThrow()

                        customersQueries.createCustomer(
                            customerId,
                            name,
                            email,
                            password,
                            image = String(),
                            creation_date = LocalDate.now(),
                        )

                        val dbCustomer = customersQueries.getCustomerById(customerId).executeAsOneOrNull()

                        dbCustomer.shouldNotBeNull()

                        val updatedName = "John Doe 2"
                        val updatedEmail = Email.create("johndoe2@mail.com").getOrThrow()
                        val updatedPassword = Password.create("password02") { hash() }.getOrThrow()

                        customersQueries.updateCustomer(
                            updatedName,
                            updatedEmail,
                            updatedPassword,
                            image = String(),
                            id = customerId,
                        )

                        val dbUpdatedCustomer = customersQueries.getCustomerById(customerId).executeAsOneOrNull()

                        dbUpdatedCustomer.shouldNotBeNull()
                        dbUpdatedCustomer.name shouldBe updatedName
                        dbUpdatedCustomer.email shouldBe updatedEmail
                        dbUpdatedCustomer.password shouldBe updatedPassword

                    }
                }
            }
        }

        Given("a customer in the database") {
            When("calling delete customer") {
                Then("it should remove the customer from the database") {

                    val customerId = ID.random()

                    val name = "John Doe"
                    val email = Email.create("johndoe@email.com").getOrThrow()
                    val password = Password.create("password0") { hash() }.getOrThrow()

                    customersQueries.createCustomer(
                        customerId,
                        name,
                        email,
                        password,
                        image = String(),
                        creation_date = LocalDate.now(),
                    )

                    val dbCustomer = customersQueries.getCustomerById(customerId).executeAsOneOrNull()

                    dbCustomer.shouldNotBeNull()

                    customersQueries.deleteCustomer(customerId)

                    val dbCustomerAfterDeletion = customersQueries.getCustomerById(customerId).executeAsOneOrNull()

                    dbCustomerAfterDeletion.shouldBeNull()

                }
            }
        }

    }

}