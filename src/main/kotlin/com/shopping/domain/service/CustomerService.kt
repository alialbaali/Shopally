package com.shopping.domain.service

import com.shopping.domain.dto.customer.request.*
import com.shopping.domain.dto.customer.response.AddressResponse
import com.shopping.domain.dto.customer.response.CardResponse
import com.shopping.domain.dto.customer.response.CartItemResponse
import com.shopping.domain.dto.customer.response.CustomerResponse
import java.io.InputStream

interface CustomerService {

    suspend fun getCustomerById(customerId: String): CustomerResponse

    suspend fun updateCustomerById(customerId: String, updateCustomerRequest: UpdateCustomerRequest): CustomerResponse

    suspend fun updateCustomerPassword(customerId: String, updateCustomerPasswordRequest: UpdateCustomerPasswordRequest)

    suspend fun updateCustomerImageById(customerId: String, inputStream: InputStream): String

    suspend fun deleteCustomer(customerId: String): String

    suspend fun getCartByCustomerId(customerId: String): List<CartItemResponse>

    suspend fun createCartItem(customerId: String, createCartItemRequest: CreateCartItemRequest): CartItemResponse

    suspend fun updateCartItem(customerId: String, updateCartItemRequest: UpdateCartItemRequest): CartItemResponse

    suspend fun deleteCartItem(customerId: String, productId: String): String

    suspend fun getAddressesByCustomerId(customerId: String): Set<AddressResponse>

    suspend fun createAddressByCustomerId(customerId: String, createAddressRequest: CreateAddressRequest): AddressResponse

    suspend fun deleteAddressByName(customerId: String, addressName: String): String

    suspend fun getCardsByCustomerId(customerId: String): Set<CardResponse>

    suspend fun createCardByCustomerId(customerId: String, createCardRequest: CreateCardRequest): CardResponse

    suspend fun deleteCartByLast4(customerId: String, cardLast4Numbers: Long): Long
}
