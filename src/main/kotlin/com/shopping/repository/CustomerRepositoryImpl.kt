package com.shopping.repository

import com.shopping.db.CustomersQueries
import com.shopping.domain.Customer
import com.shopping.domain.repository.CustomerRepository

private const val ERROR_NOT_EXIST = "Customer doesn't exist"
private const val ERROR_EXIST = "Customer exists"

class CustomerRepositoryImpl(private val customersQueries: CustomersQueries) : CustomerRepository {

    override suspend fun getCustomerByEmail(email: String): Result<Customer> {

        val dbCustomer = customersQueries.getCustomerByEmail(email).executeAsOneOrNull()
            ?: return Result.failure(Throwable(ERROR_NOT_EXIST))

        val customer =
            Customer(dbCustomer.id, dbCustomer.name, dbCustomer.email, dbCustomer.password, dbCustomer.creation_date)

        return Result.success(customer)
    }

    override suspend fun createCustomer(customer: Customer): Result<Unit> {

        customersQueries.getCustomerByEmail(customer.email).executeAsOneOrNull() ?: run {
            customersQueries.createCustomer(customer.name, customer.email, customer.password, customer.creationDate)
            return Result.success(Unit)
        }

        return Result.failure(Throwable(ERROR_EXIST))
    }

    override suspend fun updateCustomer(customer: Customer): Result<Unit> {

        customersQueries.getCustomerById(customer.id).executeAsOneOrNull()
            ?: return Result.failure(Throwable(ERROR_NOT_EXIST))

        customersQueries.updateCustomer(customer.name, customer.email, customer.password, customer.id)

        return Result.success(Unit)
    }

    override suspend fun deleteCustomer(id : Long): Result<Unit> {

        customersQueries.getCustomerById(id).executeAsOneOrNull()
            ?: return Result.failure(Throwable(ERROR_NOT_EXIST))

        customersQueries.deleteCustomer(id)

        return Result.success(Unit)
    }

}