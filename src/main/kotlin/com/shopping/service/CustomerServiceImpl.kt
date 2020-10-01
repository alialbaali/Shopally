package com.shopping.service

import com.shopping.*
import com.shopping.domain.dto.customer.request.*
import com.shopping.domain.dto.customer.response.AddressResponse
import com.shopping.domain.dto.customer.response.CardResponse
import com.shopping.domain.dto.customer.response.CartItemResponse
import com.shopping.domain.dto.customer.response.CustomerResponse
import com.shopping.domain.model.Customer
import com.shopping.domain.model.Product
import com.shopping.domain.model.valueObject.Address
import com.shopping.domain.model.valueObject.Card
import com.shopping.domain.model.valueObject.CartItem
import com.shopping.domain.repository.CustomerRepository
import com.shopping.domain.repository.ProductRepository
import com.shopping.domain.service.CustomerService
import org.koin.core.KoinComponent
import org.koin.core.get
import java.io.InputStream

class CustomerServiceImpl(private val customerRepository: CustomerRepository) : CustomerService, KoinComponent {

    override suspend fun getCustomerById(customerId: String): CustomerResponse {
        return customerRepository.getCustomerById(customerId.asID())
            .fold(
                onSuccess = { createCustomerResponse(it) },
                onFailure = { notFoundError(it.message) }
            )
    }

    override suspend fun updateCustomerById(
        customerId: String,
        updateCustomerRequest: UpdateCustomerRequest
    ): CustomerResponse {
        return customerRepository.getCustomerById(customerId.asID())
            .fold(
                onSuccess = { customer ->

                    var (name, email) = updateCustomerRequest

                    name = name ?: customer.name
                    email = email ?: customer.email

                    customerRepository.updateCustomer(customer.copy(name = name, email = email))
                        .fold(
                            onSuccess = { createCustomerResponse(it) },
                            onFailure = { badRequestError(it.message) }
                        )
                },
                onFailure = { notFoundError(it.message) }
            )
    }

    override suspend fun updateCustomerPassword(
        customerId: String,
        updateCustomerPasswordRequest: UpdateCustomerPasswordRequest
    ) {
        return customerRepository.getCustomerById(customerId.asID())
            .fold(
                onSuccess = { customer ->
                    val (oldPassword, newPassword) = updateCustomerPasswordRequest

                    if (oldPassword == customer.password)
                        customerRepository.updateCustomer(customer.copy(password = newPassword))
                            .getOrElse { badRequestError(it.message) }
                    else
                        authenticationError(Errors.PasswordsDontMatch)
                },
                onFailure = { notFoundError(it.message) }
            )
    }

    override suspend fun updateCustomerImageById(customerId: String, inputStream: InputStream): String {
        val imageFile = inputStream.buffered().toFile(customerId)

        return customerRepository.updateCustomerImage(customerId.asID(), imageFile)
            .getOrElse { badRequestError(it.message) }
    }

    override suspend fun deleteCustomer(customerId: String): String {
        return customerRepository.deleteCustomerById(customerId.asID())
            .getOrElse { badRequestError(it.message) }
            .toString()
    }

    override suspend fun getCartByCustomerId(customerId: String): List<CartItemResponse> {
        return customerRepository.getCartByCustomerId(customerId.asID())
            .fold(
                onSuccess = { cart -> cart.value.map { cartItem -> createCartItemResponse(cartItem) } },
                onFailure = { notFoundError(it.message) }
            )
    }

    override suspend fun createCartItem(
        customerId: String,
        createCartItemRequest: CreateCartItemRequest
    ): CartItemResponse {

        val (productId, quantity) = createCartItemRequest

        customerRepository.getCartItem(customerId.asID(), productId)
            .exceptionOrNull() ?: badRequestError(Errors.CartItemAlreadyExist)

        val cartItem = CartItem(productId, quantity)

        return customerRepository.createCartItem(customerId.asID(), cartItem)
            .fold(
                onSuccess = { createCartItemResponse(it) },
                onFailure = { badRequestError(it.message) }
            )
    }

    override suspend fun updateCartItem(
        customerId: String,
        updateCartItemRequest: UpdateCartItemRequest
    ): CartItemResponse {

        val (productId, quantity) = updateCartItemRequest

        return customerRepository.getCartItem(customerId.asID(), productId)
            .fold(
                onSuccess = { cartItem ->
                    customerRepository.updateCartItem(customerId.asID(), cartItem.copy(quantity = quantity))
                        .fold(
                            onSuccess = { createCartItemResponse(it) },
                            onFailure = { badRequestError(it.message) }
                        )
                },
                onFailure = { notFoundError(it.message) }
            )
    }

    override suspend fun deleteCartItem(customerId: String, productId: String): String {
        return customerRepository.deleteCartItem(customerId.asID(), productId.asID())
            .fold(
                onSuccess = { productId },
                onFailure = { notFoundError(it.message) },
            )
    }

    override suspend fun getAddressesByCustomerId(customerId: String): Set<AddressResponse> {
        return customerRepository.getAddressesByCustomerId(customerId.asID())
            .getOrElse { notFoundError(it.message) }
    }

    override suspend fun createAddressByCustomerId(
        customerId: String,
        createAddressRequest: CreateAddressRequest
    ): AddressResponse {

        val (name, country, city, line, zipCode) = createAddressRequest

        customerRepository.getAddress(customerId.asID(), name)
            .exceptionOrNull() ?: badRequestError(Errors.AddressAlreadyExist)

        val address = Address(name, country, city, line, zipCode)

        return customerRepository.createAddress(customerId.asID(), address)
            .getOrElse { badRequestError(it.message) }
    }

    override suspend fun deleteAddressByName(customerId: String, addressName: String): String {
        return customerRepository.deleteAddress(customerId.asID(), addressName)
            .getOrElse { badRequestError(it.message) }
    }

    override suspend fun getCardsByCustomerId(customerId: String): Set<CardResponse> {
        return customerRepository.getCardsByCustomerId(customerId.asID())
            .fold(
                onSuccess = { cards -> cards.map { it.toCardResponse() }.toSet() },
                onFailure = { notFoundError(it.message) }
            )
    }

    override suspend fun createCardByCustomerId(
        customerId: String,
        createCardRequest: CreateCardRequest
    ): CardResponse {

        val (number, expirationDate, cvc) = createCardRequest

        val card = Card(number = number, expirationDate = expirationDate, cvc = cvc)

        customerRepository.getCard(customerId.asID(), card.last4Numbers)
            .exceptionOrNull() ?: badRequestError(Errors.CardAlreadyExist)

        return customerRepository.createCard(customerId.asID(), card)
            .getOrElse { badRequestError(it.message) }
            .toCardResponse()
    }

    override suspend fun deleteCartByLast4(customerId: String, cardLast4Numbers: Long): Long {
       return customerRepository.deleteCard(customerId.asID(), cardLast4Numbers)
            .getOrElse { badRequestError(it.message) }
    }

    private suspend fun createCustomerResponse(customer: Customer): CustomerResponse {
        val addressCount = customerRepository.countAddressesByCustomerId(customer.id)
            .getOrDefault(0)

        val cardsCount = customerRepository.countCardsByCustomerId(customer.id)
            .getOrDefault(0)

        return customer.toCustomerResponse(addressCount, cardsCount)
    }

    private suspend fun createCartItemResponse(cartItem: CartItem): CartItemResponse {
        val product = get<ProductRepository>()
            .getProductById(cartItem.productId)
            .getOrElse { notFoundError(it.message) }

        return cartItem.toCartItemResponse(product)
    }
}

private fun Customer.toCustomerResponse(addressCount: Long, cardsCount: Long): CustomerResponse {
    return CustomerResponse(
        id.toString(),
        name,
        email.toString(),
        imageUrl,
        addressCount,
        cardsCount,
        creationDate.toString()
    )
}

private fun Card.toCardResponse(): CardResponse {
    val expMonth = expirationDate.monthValue
    val expYear = expirationDate.year
    return CardResponse(brand, number.toString(), expMonth, expYear)
}

private fun CartItem.toCartItemResponse(product: Product): CartItemResponse {
    return CartItemResponse(
        productId.toString(),
        product.brand,
        product.name,
        product.imagesUrls.firstOrNull() ?: "",
        product.price,
        quantity,
        product.price.times(quantity)
    )
}