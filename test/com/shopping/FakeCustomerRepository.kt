package com.shopping

import com.shopping.domain.model.Customer
import com.shopping.domain.model.valueObject.Address
import com.shopping.domain.model.valueObject.Card
import com.shopping.domain.model.valueObject.Email
import com.shopping.domain.model.valueObject.ID
import com.shopping.domain.repository.CustomerRepository

class FakeCustomerRepository(
    private val customers: MutableList<Customer> = mutableListOf(),
    private val addresses: MutableMap<ID, Address> = mutableMapOf(),
    private val cards: MutableMap<ID, Card> = mutableMapOf(),
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

    override suspend fun createCustomer(customer: Customer): Result<Unit> =
        if (customers.any { it.email == customer.email })
            Result.failure(Throwable(Errors.INVALID_EMAIL))
        else {
            customers.add(customer)
            Result.success(Unit)
        }

    override suspend fun updateCustomer(customer: Customer): Result<Unit> {

        val customerIndex = customers.indexOfFirst { it.id == customer.id }

        return if (customerIndex == -1)
            Result.failure(Throwable(Errors.INVALID_ID))
        else {
            customers[customerIndex] = customer
            Result.success(Unit)
        }

    }

    override suspend fun deleteCustomerById(customerId: ID): Result<Unit> =
        if (customers.removeIf { customer -> customer.id == customerId })
            Result.success(Unit)
        else
            Result.failure(Throwable(Errors.INVALID_ID))


    override suspend fun countAddressesByCustomerId(customerId: ID): Result<Long> {
        val addressesCount = addresses.count { entry -> entry.key == customerId }
        return Result.success(addressesCount.toLong())
    }

    override suspend fun countCardsByCustomerId(customerId: ID): Result<Long> {
        val cardsCount = cards.count { entry -> entry.key == customerId }
        return Result.success(cardsCount.toLong())
    }

    override suspend fun getAddressesByCustomerId(customerId: ID): Result<List<Address>> {
        return Result.success(addresses.filterKeys { it == customerId }.values.toList())
    }

    override suspend fun getCardsByCustomerId(customerId: ID): Result<List<Card>> {
        return Result.success(cards.filterKeys { it == customerId }.values.toList())
    }

    override suspend fun createAddressByCustomerId(customerId: ID, address: Address): Result<Unit> {
        addresses[customerId] = address
        return Result.success(Unit)
    }

    override suspend fun createCardByCustomerId(customerId: ID, card: Card): Result<Unit> {
        cards[customerId] = card
        return Result.success(Unit)
    }

}