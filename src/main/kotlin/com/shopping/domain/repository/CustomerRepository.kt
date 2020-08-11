package com.shopping.domain.repository

import com.shopping.domain.model.Customer
import com.shopping.domain.model.inline.Email
import com.shopping.domain.model.inline.Id

interface CustomerRepository {

    suspend fun getCustomerByEmail(customerEmail: Email): Result<Customer>

    suspend fun createCustomer(customer: Customer): Result<Unit>

    suspend fun updateCustomer(customer: Customer): Result<Unit>

    suspend fun deleteCustomer(customerId: Id): Result<Unit>

}