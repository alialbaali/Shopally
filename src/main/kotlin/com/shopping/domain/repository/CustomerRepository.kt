package com.shopping.domain.repository

import com.shopping.domain.model.Customer
import com.shopping.domain.model.valueObject.*
import java.io.File

interface CustomerRepository {

    suspend fun getCustomerById(customerId: ID): Result<Customer>

    suspend fun getCustomerByEmail(customerEmail: Email): Result<Customer>

    suspend fun createCustomer(customer: Customer): Result<Customer>

    suspend fun updateCustomer(customer: Customer): Result<Customer>

    suspend fun updateCustomerImageById(customerId: ID, imageFile: File): Result<String>

    suspend fun deleteCustomerById(customerId: ID): Result<ID>

    suspend fun getAddressesByCustomerId(customerId: ID): Result<Set<Address>>

    suspend fun createAddressByCustomerId(customerId: ID, address: Address): Result<Address>

    suspend fun countAddressesByCustomerId(customerId: ID): Result<Long>

    suspend fun getCardsByCustomerId(customerId: ID): Result<Set<Card>>

    suspend fun createCardByCustomerId(customerId: ID, card: Card): Result<Card>

    suspend fun countCardsByCustomerId(customerId: ID): Result<Long>
}
