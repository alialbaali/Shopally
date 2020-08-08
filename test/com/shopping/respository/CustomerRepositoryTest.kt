package com.shopping.respository

import com.shopping.data.repository.CustomerRepository
import com.shopping.di.dataSourceModule
import com.shopping.di.dbModule
import com.shopping.di.repositoryModule
import com.shopping.domain.Customer
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.equality.shouldNotBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class CustomerRepositoryTest : BehaviorSpec(), KoinTest {

    private val customerRepository by inject<CustomerRepository>()

    private val customer = Customer(name = "John Doe", email = "johndoe@hey.com", password = "johnDoePassword")

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        startKoin { modules(dbModule, dataSourceModule, repositoryModule) }
    }

    override fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        stopKoin()
    }

    init {

        given("a new customer") {
            `when`("calling create customer repository method") {
                then("it should insert a new customer to the database") {

                    customerRepository.createCustomer(customer)

                    val dbCustomer = customerRepository.getCustomerByEmail(customer.email).getOrNull()

                    dbCustomer.shouldNotBeNull()
                    dbCustomer.shouldBeEqualToIgnoringFields(customer, Customer::id)

                }
            }
        }

        given("a new customer ") {
            and("an updated customer with the same id") {
                `when`("calling update customer repository method") {
                    then("it should update the customer in the database") {

                        customerRepository.createCustomer(customer)

                        val newCustomer = customerRepository.getCustomerByEmail(customer.email).getOrNull()

                        newCustomer.shouldNotBeNull()

                        val updatedCustomer =
                            newCustomer.copy(name = "Mark", email = "MarkEmail@mail.com", password = "Mark Password")

                        customerRepository.updateCustomer(updatedCustomer)

                        val dbCustomer = customerRepository.getCustomerByEmail(updatedCustomer.email).getOrNull()

                        dbCustomer.shouldNotBeNull()
                        dbCustomer shouldBe updatedCustomer
                        dbCustomer.shouldNotBeEqualToIgnoringFields(newCustomer, Customer::id)

                    }
                }
            }
        }

        given("an email of a customer") {
            `when`("calling get customer by email") {
                then("it should return a customer matching that email") {

                    customerRepository.createCustomer(customer)

                    val dbCustomer = customerRepository.getCustomerByEmail(customer.email).getOrNull()

                    dbCustomer.shouldNotBeNull()
                    dbCustomer.shouldBeEqualToIgnoringFields(customer, Customer::id)

                }
            }
        }

        given("a customer in the db") {
            `when`("calling delete customer") {
                then("it should delete the customer from the database") {

                    customerRepository.createCustomer(customer)

                    val dbCustomer = customerRepository.getCustomerByEmail(customer.email).getOrNull()

                    dbCustomer.shouldNotBeNull()

                    customerRepository.deleteCustomer(dbCustomer.id)

                    val deletedDbCustomer = customerRepository.getCustomerByEmail(customer.email).getOrNull()

                    deletedDbCustomer.shouldBeNull()

                }
            }
        }

    }
}
