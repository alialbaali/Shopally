package com.shopping.repository

import com.shopping.db.CustomersQueries
import com.shopping.domain.model.Customer
import com.shopping.domain.model.inline.Email
import com.shopping.domain.model.inline.Id
import com.shopping.domain.repository.CustomerRepository

private const val ERROR_NOT_EXIST = "Customer doesn't exist"
private const val ERROR_EXIST = "Customer exists"

class CustomerRepositoryImpl(private val customersQueries: CustomersQueries) : CustomerRepository {

    override suspend fun getCustomerByEmail(customerEmail: Email): Result<Customer> {

        val (id, name, email, password, image, creationDate) =
            customersQueries.getCustomerByEmail(customerEmail).executeAsOneOrNull()
                ?: return Result.failure(Throwable(ERROR_NOT_EXIST))

        val customer = Customer(id, name, email, password, image, creationDate)

        return Result.success(customer)
    }

    override suspend fun createCustomer(customer: Customer): Result<Unit> {

        customersQueries.getCustomerByEmail(customer.email).executeAsOneOrNull() ?: run {

            customersQueries.createCustomer(
                customer.id,
                customer.name,
                customer.email,
                customer.password,
                customer.image,
                customer.creationDate
            )

            return Result.success(Unit)
        }

        return Result.failure(Throwable(ERROR_EXIST))
    }

    override suspend fun updateCustomer(customer: Customer): Result<Unit> {

        customersQueries.getCustomerById(customer.id).executeAsOneOrNull()
            ?: return Result.failure(Throwable(ERROR_NOT_EXIST))

        customersQueries.updateCustomer(customer.name, customer.email, customer.password, customer.image, customer.id)

        return Result.success(Unit)
    }

    override suspend fun deleteCustomer(customerId: Id): Result<Unit> {

        customersQueries.getCustomerById(customerId).executeAsOneOrNull()
            ?: return Result.failure(Throwable(ERROR_NOT_EXIST))

        customersQueries.deleteCustomer(customerId)

        return Result.success(Unit)
    }

}