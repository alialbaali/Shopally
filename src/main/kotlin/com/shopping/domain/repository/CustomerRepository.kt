package com.shopping.domain.repository

import com.shopping.domain.Customer

interface CustomerRepository {

    suspend fun getCustomerByEmail(email: String): Result<Customer>

    suspend fun createCustomer(customer: Customer): Result<Unit>

    suspend fun updateCustomer(customer: Customer): Result<Unit>

    suspend fun deleteCustomer(id: Long): Result<Unit>

}