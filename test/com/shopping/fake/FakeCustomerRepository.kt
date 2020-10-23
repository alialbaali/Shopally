package com.shopping.fake

import com.shopping.Errors
import com.shopping.domain.model.Customer
import com.shopping.domain.model.valueObject.*
import com.shopping.domain.repository.CustomerRepository
import com.shopping.last4Numbers
import net.bytebuddy.implementation.bytecode.Throw
import java.io.File

class FakeCustomerRepository(
    private val customers: MutableList<Customer> = mutableListOf(),
) : CustomerRepository {

    override suspend fun getCustomerById(customerId: ID): Result<Customer> {
        return customers.find { customer -> customer.id == customerId }
            ?.let { Result.success(it) } ?: Result.failure(Throwable(Errors.CustomerDoesntExist))
    }

    override suspend fun getCustomerByEmail(customerEmail: Email): Result<Customer> {
        return customers.find { customer -> customer.email == customerEmail }
            ?.let { Result.success(it) } ?: return Result.failure(Throwable(Errors.CustomerDoesntExist))
    }

    override suspend fun createCustomer(customer: Customer): Result<Customer> {
        return customers.add(customer)
            .let { Result.success(customer) }
    }

    override suspend fun updateCustomer(customer: Customer): Result<Customer> {
        return getCustomerIndexById(customer.id)
            .map { customerIndex ->
                val currentCustomer = customers[customerIndex]
                customers[customerIndex] = currentCustomer.copy(
                    name = customer.name,
                    email = customer.email,
                    password = customer.password,
                )
                customer
            }
    }

    override suspend fun updateCustomerImage(customerId: ID, customerImageFile: File): Result<String> {
        return getCustomerIndexById(customerId)
            .map { customerIndex ->
                val customer = customers[customerIndex]
                val imageUrl = customerImageFile.absolutePath
                customers[customerIndex] = customer.copy(imageUrl = imageUrl)
                imageUrl
            }
    }

    override suspend fun deleteCustomerById(customerId: ID): Result<ID> {
        val isRemoved = customers.removeIf { customer -> customer.id == customerId }
        return if (isRemoved) Result.success(customerId) else Result.failure(Throwable(Errors.CustomerDoesntExist))
    }

    override suspend fun getCartByCustomerId(customerId: ID): Result<Cart> {
        return getCustomerById(customerId)
            .map { customer -> customer.cart }
    }

    override suspend fun getCartItem(customerId: ID, productId: ID): Result<CartItem> {
        return getCustomerById(customerId)
            .map { customer ->
                customer.cart.value
                    .find { cartItem -> cartItem.productId == productId }
                    ?: return Result.failure(Throwable(Errors.CartItemDoesntExist))
            }
    }

    override suspend fun createCartItem(customerId: ID, cartItem: CartItem): Result<CartItem> {
        return getCustomerIndexById(customerId)
            .map { customerIndex ->
                val customer = customers[customerIndex]
                val cartItems = customer.cart.value.toMutableSet()
                cartItems.add(cartItem)
                customers[customerIndex] = customer.copy(cart = Cart(cartItems))
                cartItem
            }
    }

    override suspend fun updateCartItem(customerId: ID, cartItem: CartItem): Result<CartItem> {
        return getCustomerIndexById(customerId)
            .map { customerIndex ->
                val customer = customers[customerIndex]
                val cartItems = customer.cart.value.toMutableList()
                val cartItemIndex = cartItems.indexOfFirst { it.productId == cartItem.productId }
                cartItems[cartItemIndex] = cartItem
                customers[customerIndex] = customer.copy(cart = Cart(cartItems.toSet()))
                cartItem
            }
    }

    override suspend fun deleteCartItem(customerId: ID, productId: ID): Result<ID> {
        return getCustomerIndexById(customerId)
            .map { customerIndex ->
                val customer = customers[customerIndex]
                val cartItems = customer.cart.value.toMutableList()
                cartItems.removeIf { it.productId == productId }
                productId
            }
    }

    override suspend fun deleteCartItemsByCustomerId(customerId: ID): Result<ID> {
        return getCustomerIndexById(customerId)
            .map { customerIndex ->
                val customer = customers[customerIndex]
                customers[customerIndex] = customer.copy(cart = Cart.Empty)
                customerId
            }
    }

    override suspend fun getAddressesByCustomerId(customerId: ID): Result<Set<Address>> {
        return getCustomerById(customerId)
            .map { customer -> customer.addresses }
    }

    override suspend fun getAddress(customerId: ID, addressName: String): Result<Address> {
        return getCustomerById(customerId)
            .map {
                it.addresses
                    .find { address -> address.name == addressName } ?: return Result.failure(Throwable(Errors.AddressDoesntExist))
            }
    }

    override suspend fun createAddress(customerId: ID, address: Address): Result<Address> {
        return getCustomerIndexById(customerId)
            .map { customerIndex ->
                val customer = customers[customerIndex]
                customers[customerIndex] = customer.copy(addresses = customer.addresses.plus(address))
                address
            }
    }

    override suspend fun deleteAddress(customerId: ID, addressName: String): Result<String> {
        return getCustomerIndexById(customerId)
            .map { customerIndex ->
                val customer = customers[customerIndex]
                val address = customer.addresses.first { it.name == addressName }
                customers[customerIndex] = customer.copy(addresses = customer.addresses.minus(address))
                addressName
            }
    }

    override suspend fun countAddressesByCustomerId(customerId: ID): Result<Long> {
        return getCustomerById(customerId)
            .map { customer -> customer.addresses.count().toLong() }
    }

    override suspend fun getCardsByCustomerId(customerId: ID): Result<Set<Card>> {
        return getCustomerById(customerId)
            .map { customer -> customer.cards }
    }

    override suspend fun getCard(customerId: ID, cardLast4Numbers: Long): Result<Card> {
        return getCustomerById(customerId)
            .map { customer ->
                customer.cards
                    .find { card -> card.last4Numbers == cardLast4Numbers }
                    ?: return Result.failure(Throwable(Errors.CardDoesntExist))
            }
    }

    override suspend fun createCard(customerId: ID, card: Card): Result<Card> {
        return getCustomerIndexById(customerId)
            .map { customerIndex ->
                val customer = customers[customerIndex]
                customers[customerIndex] = customer.copy(cards = customer.cards.plus(card))
                card
            }
    }

    override suspend fun deleteCard(customerId: ID, cardLast4Numbers: Long): Result<Long> {
        return getCustomerIndexById(customerId)
            .map { customerIndex ->
                val customer = customers[customerIndex]
                val card = customer.cards.first { card -> card.last4Numbers == cardLast4Numbers }
                customers[customerIndex] = customer.copy(cards = customer.cards.minus(card))
                cardLast4Numbers
            }
    }

    override suspend fun countCardsByCustomerId(customerId: ID): Result<Long> {
        return getCustomerById(customerId)
            .map { customer -> customer.cards.count().toLong() }
    }

    override suspend fun chargeCard(customerId: ID, cardLast4Numbers: Long, amount: Double): Result<Unit> = runCatching {}

    private fun getCustomerIndexById(customerId: ID): Result<Int> {
        return customers.indexOfFirst { customer -> customer.id == customerId }
            .takeUnless { it == -1 }
            ?.let { Result.success(it) } ?: Result.failure(Throwable(Errors.CustomerDoesntExist))
    }

}
