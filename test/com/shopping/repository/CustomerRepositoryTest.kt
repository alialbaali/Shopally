package com.shopping.repository

import com.shopping.KoinTestListener
import com.shopping.di.dataSourceModule
import com.shopping.di.dbModule
import com.shopping.di.repositoryModule
import com.shopping.domain.model.Customer
import com.shopping.domain.model.valueObject.Email
import com.shopping.domain.model.valueObject.Password
import com.shopping.domain.repository.CustomerRepository
import com.shopping.hash
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equality.shouldNotBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.koin.test.KoinTest
import org.koin.test.inject

class CustomerRepositoryTest : BehaviorSpec(), KoinTest {

    private val customerRepository by inject<CustomerRepository>()

    override fun listeners(): List<TestListener> = listOf(KoinTestListener(dbModule, dataSourceModule, repositoryModule))

    init {

        Given("a new customer") {
            When("calling create customer") {
                Then("it should insert a new customer to the database") {

                    val customer = Customer(
                        name = "John Doe",
                        email = Email.create("johndoe@mail.com").getOrThrow(),
                        password = Password.create("johnDoePassword0") { hash() }.getOrThrow()
                    )

                    customerRepository.createCustomer(customer)

                    val dbCustomer = customerRepository.getCustomerByEmail(customer.email).getOrNull()

                    dbCustomer.shouldNotBeNull()
                    dbCustomer shouldBe customer

                }
            }
        }

        Given("a new customer ") {
            And("an updated customer with the same id") {
                When("calling update customer repository method") {
                    Then("it should update the customer in the database") {

                        val customer = Customer(
                            name = "John Doe",
                            email = Email.create("johndoe@email.com").getOrThrow(),
                            password = Password.create("johnDoePassword0") { hash() }.getOrThrow()
                        )

                        customerRepository.createCustomer(customer)

                        val updatedCustomer = customer.copy(
                            name = "Mark",
                            email = Email.create("mark@mail.com").getOrThrow(),
                            password = Password.create("markPassword0") { hash() }.getOrThrow()
                        )

                        customerRepository.updateCustomer(updatedCustomer)

                        val dbCustomer = customerRepository.getCustomerById(updatedCustomer.id).getOrNull()

                        dbCustomer.shouldNotBeNull()
                        dbCustomer shouldBe updatedCustomer
                        dbCustomer.shouldNotBeEqualToIgnoringFields(customer, Customer::id)

                    }
                }
            }
        }

        Given("a customer in the db") {
            When("calling delete customer") {
                Then("it should delete the customer from the database") {


                    val customer = Customer(
                        name = "John Doe",
                        email = Email.create("johndoe1@mail.com").getOrThrow(),
                        password = Password.create("johnDoePassword0") { hash() }.getOrThrow()
                    )

                    customerRepository.createCustomer(customer)

                    val dbCustomer = customerRepository.getCustomerByEmail(customer.email).getOrNull()

                    dbCustomer.shouldNotBeNull()

                    customerRepository.deleteCustomerById(dbCustomer.id)

                    val deletedCustomer = customerRepository.getCustomerByEmail(customer.email).getOrNull()

                    deletedCustomer shouldNotBe customer
                    deletedCustomer.shouldBeNull()

                }
            }
        }

        Given("a new customer  ") {
            And("an updated customer with the same id") {
                When("calling update customer repository method") {
                    And("and delete method") {
                        Then("it should delete the updated customer in the database") {


                            val customer = Customer(
                                name = "John Doe",
                                email = Email.create("johndoe@email.com").getOrThrow(),
                                password = Password.create("johnDoePassword0") { hash() }.getOrThrow()
                            )


                            customerRepository.createCustomer(customer)

                            val updatedCustomer = customer.copy(
                                name = "Mark",
                                email = Email.create("mark@mail.com").getOrThrow(),
                                password = Password.create("markPassword0") { hash() }.getOrThrow()
                            )

                            customerRepository.updateCustomer(updatedCustomer)

                            customerRepository.deleteCustomerById(updatedCustomer.id)

                            val deletedCustomer =
                                customerRepository.getCustomerByEmail(updatedCustomer.email).getOrNull()

                            deletedCustomer shouldNotBe customer
                            deletedCustomer shouldNotBe updatedCustomer
                            deletedCustomer.shouldBeNull()

                        }
                    }
                }
            }
        }

    }
}
