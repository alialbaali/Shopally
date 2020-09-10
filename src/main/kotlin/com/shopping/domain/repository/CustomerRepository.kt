package com.shopping.domain.repository

import com.shopping.domain.model.Customer
import com.shopping.domain.model.Order
import com.shopping.domain.model.valueObject.*
import java.io.File

interface CustomerRepository {

    suspend fun getCustomerById(customerId: ID): Result<Customer>

    suspend fun getCustomerByEmail(customerEmail: Email): Result<Customer>

    suspend fun createCustomer(customer: Customer): Result<Customer>

    suspend fun updateCustomer(customer: Customer): Result<Customer>

    suspend fun updateCustomerImage(customerId: ID, customerImageFile: File): Result<String>

    suspend fun deleteCustomerById(customerId: ID): Result<ID>


    suspend fun getCartByCustomerId(customerId: ID): Result<Cart>

    suspend fun createCartItem(customerId: ID, orderItem: Order.OrderItem): Result<Order.OrderItem>

    suspend fun updateCartItem(customerId: ID, orderItem: Order.OrderItem): Result<Order.OrderItem>

    suspend fun deleteCartItem(customerId: ID, productID: ID): Result<Unit>

    suspend fun clearCartByCustomerId(customerId: ID): Result<Unit>


    suspend fun getAddress(customerId: ID, addressName: String): Result<Address>

    suspend fun getAddressesByCustomerId(customerId: ID): Result<Set<Address>>

    suspend fun createAddress(customerId: ID, address: Address): Result<Address>

    suspend fun deleteAddress(customerId: ID, addressName: String): Result<String>

    suspend fun countAddressesByCustomerId(customerId: ID): Result<Long>


    suspend fun getCard(customerId: ID, cardNumber: Long): Result<Card>

    suspend fun getCardsByCustomerId(customerId: ID): Result<Set<Card>>

    suspend fun createCard(customerId: ID, card: Card): Result<Card>

    suspend fun deleteCard(customerId: ID, cardLast4: String): Result<String>

    suspend fun countCardsByCustomerId(customerId: ID): Result<Long>
}
