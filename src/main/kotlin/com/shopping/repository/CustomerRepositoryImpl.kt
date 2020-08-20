package com.shopping.repository

import com.shopping.Errors
import com.shopping.db.AddressesQueries
import com.shopping.db.CardsQueries
import com.shopping.db.CustomersQueries
import com.shopping.domain.model.Customer
import com.shopping.domain.model.valueObject.Address
import com.shopping.domain.model.valueObject.Card
import com.shopping.domain.model.valueObject.Email
import com.shopping.domain.model.valueObject.ID
import com.shopping.domain.repository.CustomerRepository

private const val ERROR_NOT_EXIST = "Customer doesn't exist"
private const val ERROR_EXIST = "Customer exists"

class CustomerRepositoryImpl(
    private val customersQueries: CustomersQueries,
    private val addressesQueries: AddressesQueries,
    private val cardsQueries: CardsQueries
) : CustomerRepository {

    override suspend fun getCustomerById(customerId: ID): Result<Customer> {

        val customer = customersQueries.getCustomerById(customerId) { id, name, email, password, image, creation_date ->

            Customer(id, name, email, password, image, creationDate = creation_date)

        }.executeAsOneOrNull() ?: return Result.failure(Throwable(Errors.INVALID_ID))

        return Result.success(customer)
    }

    override suspend fun getCustomerByEmail(customerEmail: Email): Result<Customer> {

        val customer =
            customersQueries.getCustomerByEmail(customerEmail) { id, name, email, password, image, creation_date ->

                Customer(id, name, email, password, image, creationDate = creation_date)

            }.executeAsOneOrNull() ?: return Result.failure(Throwable(ERROR_NOT_EXIST))

        return Result.success(customer)
    }

    override suspend fun createCustomer(customer: Customer): Result<Unit> {

        customersQueries.getCustomerByEmail(customer.email).executeAsOneOrNull() ?: run {

            customersQueries.createCustomer(
                customer.id,
                customer.name,
                customer.email,
                customer.password,
                customer.image,
                customer.creationDate
            )

            return Result.success(Unit)
        }

        return Result.failure(Throwable(ERROR_EXIST))
    }

    override suspend fun updateCustomer(customer: Customer): Result<Unit> {

        customersQueries.getCustomerById(customer.id).executeAsOneOrNull()
            ?: return Result.failure(Throwable(ERROR_NOT_EXIST))

        customersQueries.updateCustomer(customer.name, customer.email, customer.password, customer.image, customer.id)

        return Result.success(Unit)
    }

    override suspend fun deleteCustomerById(customerId: ID): Result<Unit> {

        customersQueries.getCustomerById(customerId).executeAsOneOrNull()
            ?: return Result.failure(Throwable(ERROR_NOT_EXIST))

        customersQueries.deleteCustomer(customerId)

        return Result.success(Unit)
    }

    override suspend fun getAddressesByCustomerId(customerId: ID): Result<List<Address>> {

        val addresses =
            addressesQueries.getAddressesByCustomerID(customerId) { _, name, country, city, address_line, zip_code ->

                Address(name, country, city, address_line, zip_code)

            }.executeAsList()

        return Result.success(addresses)
    }


    override suspend fun createAddressByCustomerId(customerId: ID, address: Address): Result<Unit> {

        val addresses =
            addressesQueries.getAddressesByCustomerID(customerId) { _, name, country, city, address_line, zip_code ->

                Address(name, country, city, address_line, zip_code)

            }.executeAsList()

        return if (addresses.any { it == address })

            Result.failure(Throwable(ERROR_EXIST))

        else {

            addressesQueries.createAddress(
                customerId,
                address.name,
                address.country,
                address.city,
                address.line,
                address.zipCode
            )

            Result.success(Unit)
        }
    }

    override suspend fun countAddressesByCustomerId(customerId: ID): Result<Long> {

        val addressCount =
            addressesQueries.countAddressesByCustomerId(customerId).executeAsOneOrNull()
                ?: return Result.failure(Throwable(Errors.INVALID_ID))

        return Result.success(addressCount)
    }

    override suspend fun getCardsByCustomerId(customerId: ID): Result<List<Card>> {

        val cards =
            cardsQueries.getCardsByCustomerId(customerId) { _, name, brand, number, balance, ccv, expiration_date ->

                Card(name, brand, number, balance, ccv, expiration_date)

            }.executeAsList()

        return Result.success(cards)
    }

    override suspend fun createCardByCustomerId(customerId: ID, card: Card): Result<Unit> {

        val cards =

            cardsQueries.getCardsByCustomerId(customerId) { _, name, brand, number, balance, ccv, expiration_date ->

                Card(name, brand, number, balance, ccv, expiration_date)

            }.executeAsList()

        return if (cards.any { it == card })

            Result.failure(Throwable(ERROR_EXIST))

        else {

            cardsQueries.createCard(
                customerId,
                card.name,
                card.brand,
                card.number,
                card.balance,
                card.ccv,
                card.expirationDate
            )

            Result.success(Unit)
        }
    }

    override suspend fun countCardsByCustomerId(customerId: ID): Result<Long> {

        val cardsCount =
            cardsQueries.countCardsByCustomerId(customerId).executeAsOneOrNull()
                ?: return Result.failure(Throwable(Errors.INVALID_ID))

        return Result.success(cardsCount)
    }

}