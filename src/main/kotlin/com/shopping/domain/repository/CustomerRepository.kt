package com.shopping.domain.repository

import com.shopping.domain.model.Customer
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

    suspend fun getCartItem(customerId: ID, productId: ID): Result<CartItem>

    suspend fun createCartItem(customerId: ID, cartItem: CartItem): Result<CartItem>

    suspend fun updateCartItem(customerId: ID, cartItem: CartItem): Result<CartItem>

    suspend fun deleteCartItem(customerId: ID, productId: ID): Result<Pair<ID, ID>>

    suspend fun deleteCartItemsByCustomerId(customerId: ID): Result<ID>

    suspend fun getAddressesByCustomerId(customerId: ID): Result<Set<Address>>

    suspend fun getAddress(customerId: ID, addressName: String): Result<Address>

    suspend fun createAddress(customerId: ID, address: Address): Result<Address>

    suspend fun deleteAddress(customerId: ID, addressName: String): Result<String>

    suspend fun countAddressesByCustomerId(customerId: ID): Result<Long>

    suspend fun getCardsByCustomerId(customerId: ID): Result<Set<Card>>

    suspend fun getCard(customerId: ID, cardLast4Numbers: Long): Result<Card>

    suspend fun createCard(customerId: ID, card: Card): Result<Card>

    suspend fun deleteCard(customerId: ID, cardLast4Numbers: Long): Result<Long>

    suspend fun countCardsByCustomerId(customerId: ID): Result<Long>
}
