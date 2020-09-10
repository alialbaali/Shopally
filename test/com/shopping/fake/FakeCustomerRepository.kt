package com.shopping

import com.shopping.domain.model.Customer
import com.shopping.domain.model.Order
import com.shopping.domain.model.valueObject.*
import com.shopping.domain.repository.CustomerRepository
import java.io.File

class FakeCustomerRepository(
    private val customers: MutableList<Customer> = mutableListOf(),
    private val carts: MutableMap<ID, Cart> = mutableMapOf(),
    private val addresses: MutableMap<ID, Address> = mutableMapOf(),
    private val cards: MutableMap<ID, Card> = mutableMapOf(),
    private val cloudDataSource: MutableMap<ID, File> = mutableMapOf(),
) : CustomerRepository {

    override suspend fun getCustomerById(customerId: ID): Result<Customer> {
        val customer = customers.find { customer -> customer.id == customerId }
            ?: return Result.failure(Throwable(Errors.INVALID_ID))

        return Result.success(customer)
    }

    override suspend fun getCustomerByEmail(customerEmail: Email): Result<Customer> {
        val customer = customers.find { customer -> customer.email == customerEmail }
            ?: return Result.failure(Throwable(Errors.INVALID_EMAIL))

        return Result.success(customer)
    }

    override suspend fun createCustomer(customer: Customer): Result<Customer> {
        return if (customers.any { it.email.toString().toLowerCase() == customer.email.toString().toLowerCase() }){
            Result.failure(Throwable(Errors.INVALID_EMAIL))
        }
        else {
            customers.add(customer)
            Result.success(customer)
        }
    }

    override suspend fun updateCustomer(customer: Customer): Result<Customer> {

        val customerIndex = customers.indexOfFirst { it.id == customer.id }

        return if (customerIndex == -1)
            Result.failure(Throwable(Errors.INVALID_ID))
        else {
            customers[customerIndex] = customer
            Result.success(customer)
        }
    }

    override suspend fun updateCustomerImage(customerId: ID, imageFile: File): Result<String> {

        val customer = getCustomerById(customerId).getOrElse {
            return Result.failure(it)
        }

        cloudDataSource[customer.id] = imageFile

        return Result.success(imageFile.absolutePath)
    }

    override suspend fun deleteCustomerById(customerId: ID): Result<ID> =
        if (customers.removeIf { customer -> customer.id == customerId })
            Result.success(customerId)
        else
            Result.failure(Throwable(Errors.INVALID_ID))

    override suspend fun getCartByCustomerId(customerId: ID): Result<Cart> {
        val entry = carts.entries.find { entry -> entry.key == customerId } ?: return Result.failure(Throwable())
        return Result.success(entry.value)
    }

    override suspend fun createCartItem(customerId: ID, orderItem: Order.OrderItem): Result<Order.OrderItem> {
        carts[customerId] = Cart(carts[customerId]?.value?.plus(orderItem) ?: return Result.failure(Throwable()))
        return Result.success(orderItem)
    }

    override suspend fun updateCartItem(customerId: ID, orderItem: Order.OrderItem): Result<Order.OrderItem> {

        TODO("Not yet implemented")
    }

    override suspend fun deleteCartItem(customerId: ID, productID: ID): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun emptyCartByCustomerId(customerId: ID): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getAddressByName(customerId: ID, addressName: String): Result<Address> {
        TODO("Not yet implemented")
    }

    override suspend fun countAddressesByCustomerId(customerId: ID): Result<Long> {
        val addressesCount = addresses.count { entry -> entry.key == customerId }
        return Result.success(addressesCount.toLong())
    }

    override suspend fun getCardByNumber(customerId: ID, cardNumber: Long): Result<Card> {
        TODO("Not yet implemented")
    }

    override suspend fun countCardsByCustomerId(customerId: ID): Result<Long> {
        val cardsCount = cards.count { entry -> entry.key == customerId }
        return Result.success(cardsCount.toLong())
    }

    override suspend fun getAddressesByCustomerId(customerId: ID): Result<Set<Address>> {
        return Result.success(addresses.filterKeys { it == customerId }.values.toSet())
    }

    override suspend fun getCardsByCustomerId(customerId: ID): Result<Set<Card>> {
        return Result.success(cards.filterKeys { it == customerId }.values.toSet())
    }

    override suspend fun createAddressByCustomerId(customerId: ID, address: Address): Result<Address> {
        addresses[customerId] = address
        return Result.success(address)
    }

    override suspend fun createCardByCustomerId(customerId: ID, card: Card): Result<Card> {
        cards[customerId] = card
        return Result.success(card)
    }
}
