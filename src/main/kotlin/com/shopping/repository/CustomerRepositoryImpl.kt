package com.shopping.repository

import com.shopping.Errors
import com.shopping.db.*
import com.shopping.domain.CloudDataSource
import com.shopping.domain.StripeCard
import com.shopping.domain.StripeCardDataSource
import com.shopping.domain.StripeCustomerDataSource
import com.shopping.domain.model.Customer
import com.shopping.domain.model.Order
import com.shopping.domain.model.valueObject.*
import com.shopping.domain.repository.CustomerRepository
import java.io.File
import java.time.LocalDate

private const val CustomersFolder = "Customers"

class CustomerRepositoryImpl(
    private val customersQueries: CustomersQueries,
    private val customerCartQueries: CustomerCartQueries,
    private val customerAddressesQueries: CustomerAddressesQueries,
    private val customerCardsQueries: CustomerCardsQueries,
    private val stripeCustomerDao: StripeCustomerDataSource,
    private val stripeCardDao: StripeCardDataSource,
    private val cloudDataSource: CloudDataSource,
) : CustomerRepository {

    override suspend fun getCustomerById(customerId: ID): Result<Customer> {
        return customersQueries.getCustomerById(customerId)
            .executeAsOneOrNull()
            ?.toCustomer()
            ?.let { customer -> Result.success(customer) }
            ?: return Result.failure(Throwable(Errors.CustomerDoesntExist))
    }

    override suspend fun getCustomerByEmail(customerEmail: Email): Result<Customer> {
        return customersQueries.getCustomerByEmail(customerEmail)
            .executeAsOneOrNull()
            ?.toCustomer()
            ?.let { customer -> Result.success(customer) }
            ?: return Result.failure(Throwable(Errors.CustomerDoesntExist))
    }

    override suspend fun createCustomer(customer: Customer): Result<Customer> {

        val (id, name, email, password, imageUrl, _, _, _, creationDate) = customer

        val stripeCustomer = stripeCustomerDao.createStripeCustomer(name, email.toString())
            .getOrElse {
                return Result.failure(Throwable(Errors.SomethingWentWrong))
            }

        runCatching {
            customersQueries.createCustomer(id, stripeCustomer.id, name, email, password, imageUrl, creationDate)
        }.getOrElse {

            stripeCustomerDao.deleteStripeCustomerById(stripeCustomer.id)
                .getOrThrow()

            return Result.failure(Throwable(Errors.SomethingWentWrong))
        }

        return getCustomerById(id)
    }

    override suspend fun updateCustomer(customer: Customer): Result<Customer> {

        val (id, name, email, password, imageUrl) = customer

        val dbCustomer = customersQueries.getCustomerById(customer.id)
            .executeAsOneOrNull() ?: return Result.failure(Throwable(Errors.CustomerDoesntExist))

        stripeCustomerDao.updateStripeCustomerById(dbCustomer.stripe_id, name, email.toString())
            .getOrElse {
                return Result.failure(Throwable(Errors.SomethingWentWrong))
            }

        runCatching {

            customersQueries.updateCustomerById(
                name,
                email,
                password,
                imageUrl,
                id,
            )
        }.onFailure {

            customersQueries.getCustomerById(id)
                .executeAsOne()
                .apply {
                    stripeCustomerDao.updateStripeCustomerById(stripe_id, name, email.toString())
                        .getOrElse {
                            return Result.failure(Throwable(Errors.SomethingWentWrong))
                        }
                }

            return Result.failure(Throwable(Errors.SomethingWentWrong))
        }

        return getCustomerById(id)
    }

    override suspend fun updateCustomerImage(customerId: ID, customerImageFile: File): Result<String> {

        val imageUrl = cloudDataSource.uploadImage(
            customerImageFile,
            imageId = customerId.toString(),
            folderName = CustomersFolder
        ).getOrElse {
            return Result.failure(Throwable(Errors.ImageUploadFailed))
        }

        val customer = customersQueries.getCustomerById(customerId)
            .executeAsOne().toCustomer()

        updateCustomer(customer.copy(imageUrl = imageUrl))
            .getOrElse {
                return Result.failure(Throwable(Errors.SomethingWentWrong))
            }

        return Result.success(imageUrl)
    }

    override suspend fun deleteCustomerById(customerId: ID): Result<ID> {

        val stripeCustomerId = customersQueries.getCustomerById(customerId)
            .executeAsOneOrNull()
            ?.stripe_id ?: return Result.failure(Throwable(Errors.CustomerDoesntExist))

        stripeCustomerDao.deleteStripeCustomerById(stripeCustomerId)
            .getOrElse {
                return Result.failure(Throwable(Errors.SomethingWentWrong))
            }

        return customersQueries.transactionWithResult {

            customersQueries.deleteCustomerById(customerId)

            customerAddressesQueries.deleteAddressesByCustomerId(customerId)

            customerCardsQueries.deleteCardsByCustomerId(customerId)

            customerCartQueries.deleteCartItemsByCustomerId(customerId)

            Result.success(customerId)
        }
    }

    override suspend fun getCartByCustomerId(customerId: ID): Result<Cart> {
        return customerCartQueries.getCartItemsByCustomerId(customerId)
            .executeAsList()
            .map { it.toCartItem() }
            .toSet()
            .let { Result.success(Cart(it)) }
    }

    override suspend fun getCartItem(customerId: ID, productID: ID): Result<CartItem> {
        return customerCartQueries.getCartItem(customerId, productID)
            .executeAsOneOrNull()
            ?.toCartItem()
            ?.let { Result.success(it) } ?: return Result.failure(Throwable(Errors.CartItemDoesntExist))
    }

    override suspend fun createCartItem(customerId: ID, cartItem: CartItem): Result<CartItem> {
        val (productId, quantity) = cartItem

        return customerCartQueries.createCartItem(customerId, productId, quantity)
            .run { getCartItem(customerId, productId) }
    }

    override suspend fun updateCartItem(customerId: ID, cartItem: CartItem): Result<Order.OrderItem> {
        val (productId, quantity) = cartItem

        return customerCartQueries.updateCartItem(quantity, customerId, productId)
            .run { getCartItem(customerId, productId) }
    }

    override suspend fun deleteCartItem(customerId: ID, productId: ID): Result<ID> {
        return customerCartQueries.deleteCartItem(customerId, productId)
            .run { Result.success(productId) }
    }

    override suspend fun deleteCartItemsByCustomerId(customerId: ID): Result<ID> {
        return customerCartQueries.deleteCartItemsByCustomerId(customerId)
            .let { Result.success(customerId) }
    }

    override suspend fun getAddressesByCustomerId(customerId: ID): Result<Set<Address>> {
        return customerAddressesQueries.getAddressesByCustomerId(customerId)
            .executeAsList()
            .map { dbAddress -> dbAddress.toAddress() }
            .toSet()
            .let { Result.success(it) }
    }

    override suspend fun getAddress(customerId: ID, addressName: String): Result<Address> {
        return customerAddressesQueries.getAddress(customerId, addressName)
            .executeAsOneOrNull()
            ?.toAddress()
            ?.let { Result.success(it) } ?: return Result.failure(Throwable(Errors.AddressDoesntExist))
    }

    override suspend fun createAddress(customerId: ID, address: Address): Result<Address> {
        val (name, country, city, line, zipCode) = address

        return customerAddressesQueries.createAddress(customerId, name, country, city, line, zipCode)
            .run { getAddress(customerId, address.name) }
    }

    override suspend fun deleteAddress(customerId: ID, addressName: String): Result<String> {
        return customerAddressesQueries.deleteAddress(customerId, addressName)
            .run { Result.success(addressName) }
    }

    override suspend fun countAddressesByCustomerId(customerId: ID): Result<Long> {
        return customerAddressesQueries.countAddressesByCustomerId(customerId)
            .executeAsOne()
            .let { Result.success(it) }
    }

    override suspend fun getCardsByCustomerId(customerId: ID): Result<Set<Card>> {

        val stripeCustomerId = customersQueries.getCustomerById(customerId) { _, stripe_id, _, _, _, _, _ -> stripe_id }
            .executeAsOneOrNull() ?: return Result.failure(Throwable(Errors.CustomerDoesntExist))

        val cards = stripeCardDao.getStripeCardsByCustomerId(stripeCustomerId)
            .getOrElse { return Result.failure(Throwable(Errors.SomethingWentWrong)) }
            .map { stripeCard -> stripeCard.toCard() }
            .toSet()

        return Result.success(cards)
    }

    override suspend fun getCard(customerId: ID, cardLast4Numbers: Long): Result<Card> {

        val stripeCustomerId = customersQueries.getCustomerById(customerId) { _, stripe_id, _, _, _, _, _ -> stripe_id }
            .executeAsOneOrNull() ?: return Result.failure(Throwable(Errors.CustomerDoesntExist))

        val stripeCardId = customerCardsQueries.getCard(customerId, cardLast4Numbers)
            .executeAsOneOrNull()
            ?.stripe_card_id ?: return Result.failure(Throwable(Errors.CardDoesntExist))

        return stripeCardDao.getStripeCardById(stripeCustomerId, stripeCardId)
            .getOrElse { return Result.failure(Throwable(Errors.CardDoesntExist)) }
            .toCard()
            .let { Result.success(it) }
    }

    override suspend fun createCard(customerId: ID, card: Card): Result<Card> {

        val (_, number, expirationDate, cvc) = card

        val expMonth = expirationDate.monthValue
        val expYear = expirationDate.year

        val stripeCustomerId = customersQueries.getCustomerById(customerId)
            .executeAsOneOrNull()
            ?.stripe_id ?: return Result.failure(Throwable(Errors.CustomerDoesntExist))

        val stripeCard = stripeCardDao.createStripeCard(
            stripeCustomerId,
            number.toString(),
            expMonth,
            expYear,
            cvc
        ).getOrElse { return Result.failure(Throwable(it.message)) }

        runCatching {
            customerCardsQueries.createCard(
                customerId,
                stripeCard.id,
                stripeCard.last4.toLong()
            )
        }.getOrElse {

            stripeCardDao.deleteStripeCard(stripeCustomerId, stripeCard.id)

            return Result.failure(Throwable(Errors.SomethingWentWrong))
        }

        return Result.success(stripeCard.toCard())
    }

    override suspend fun deleteCard(customerId: ID, cardLast4Numbers: Long): Result<Long> {

        val dbCard = customerCardsQueries.getCard(customerId, cardLast4Numbers)
            .executeAsOneOrNull() ?: return Result.failure(Throwable(Errors.CardDoesntExist))

        val dbCustomer = customersQueries.getCustomerById(customerId)
            .executeAsOneOrNull() ?: return Result.failure(Throwable(Errors.CustomerDoesntExist))

        return stripeCardDao.deleteStripeCard(dbCustomer.stripe_id, dbCard.stripe_card_id)
            .fold(
                onSuccess = {
                    customerCardsQueries.deleteCard(customerId, dbCard.stripe_card_id)
                    Result.success(cardLast4Numbers)
                },
                onFailure = { return Result.failure(Throwable(Errors.SomethingWentWrong)) },
            )
    }

    override suspend fun countCardsByCustomerId(customerId: ID): Result<Long> {
        return customerCardsQueries.countCardsByCustomerId(customerId)
            .executeAsOne()
            .let { Result.success(it) }
    }

    override suspend fun chargeCard(customerId: ID, cardLast4Numbers: Long, amount: Double): Result<Unit> {
        val customer = customersQueries.getCustomerById(customerId)
            .executeAsOneOrNull() ?: return Result.failure(Throwable(Errors.CustomerDoesntExist))

        val card = customerCardsQueries.getCard(customerId, cardLast4Numbers)
            .executeAsOneOrNull() ?: return Result.failure(Throwable(Errors.CardDoesntExist))

        return stripeCardDao.chargeCardById(customer.stripe_id, card.stripe_card_id, amount)
    }
}

private fun Customers.toCustomer(): Customer {
    return Customer(id, name, email, password, image_url, creationDate = creation_date)
}

private fun CustomerAddresses.toAddress(): Address {
    return Address(name, country, city, line, zip_code)
}

private fun StripeCard.toCard(): Card {
    val expirationDate = LocalDate.of(expYear.toInt(), expMonth.toInt(), 1)
    return Card(brand, last4.toLong(), expirationDate)
}

private fun CustomerCart.toCartItem(): CartItem {
    return CartItem(product_id, quantity)
}
