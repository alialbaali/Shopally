package com.shopping.domain.repository

import com.shopping.domain.model.Customer
import com.shopping.domain.model.valueObject.Address
import com.shopping.domain.model.valueObject.Card
import com.shopping.domain.model.valueObject.Email
import com.shopping.domain.model.valueObject.ID

interface CustomerRepository {

    suspend fun getCustomerById(customerId: ID): Result<Customer>

    suspend fun getCustomerByEmail(customerEmail: Email): Result<Customer>

    suspend fun createCustomer(customer: Customer): Result<Unit>

    suspend fun updateCustomer(customer: Customer): Result<Unit>

    suspend fun deleteCustomerById(customerId: ID): Result<Unit>

    suspend fun getAddressesByCustomerId(customerId: ID): Result<List<Address>>

    suspend fun createAddressByCustomerId(customerId: ID, address: Address): Result<Unit>

    suspend fun countAddressesByCustomerId(customerId: ID): Result<Long>

    suspend fun getCardsByCustomerId(customerId: ID): Result<List<Card>>

    suspend fun createCardByCustomerId(customerId: ID, card: Card): Result<Unit>

    suspend fun countCardsByCustomerId(customerId: ID): Result<Long>

}