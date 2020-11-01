package com.shopping

import com.shopping.domain.dto.customer.request.CreateAddressRequest
import com.shopping.domain.dto.customer.request.CreateCardRequest
import com.shopping.domain.dto.customer.request.CreateCartItemRequest
import com.shopping.domain.dto.customer.request.SignUpRequest
import com.shopping.domain.dto.customer.response.TokenResponse
import com.shopping.domain.model.Customer
import com.shopping.domain.model.Order
import com.shopping.domain.model.Product
import com.shopping.domain.model.valueObject.*
import com.shopping.domain.repository.CustomerRepository
import com.shopping.domain.repository.OrderRepository
import com.shopping.domain.repository.ProductRepository
import com.shopping.domain.service.AuthService
import com.shopping.domain.service.CustomerService
import org.jetbrains.annotations.TestOnly
import java.time.LocalDate

@TestOnly
suspend fun AuthService.signUpTestCustomer(
    name: String = "John Doe",
    email: String = "johndoe@mail.com",
    password: String = "password0"
): TokenResponse = signUp(SignUpRequest(name, email, password))

suspend fun ProductRepository.createTestProducts(vararg products: Product): List<Product> {
    repeat(10) {
        createProduct(
            Product(
                category = Product.Category.values().random(),
                brand = "Apple $it",
                name = "iMac $it",
                description = "Smartphone $it",
                price = 999.0,
                releaseDate = LocalDate.now().minusWeeks(it.toLong())
            ),
            emptyList()
        )
    }
    products.forEach { product ->
        createProduct(product, emptyList())
    }

    return getProducts(100, 0, emptySet(), emptySet(), 0.0, Double.MAX_VALUE).getOrThrow()
}

suspend fun CustomerService.createTestAddress(
    customerId: String,
    name: String = "Home",
    country: String = "US",
    city: String = "LA",
    line: String = "Unknown",
    zipCode: String = "2032"
) = createAddressByCustomerId(customerId, CreateAddressRequest(name, country, city, line, zipCode))

suspend fun CustomerService.createTestCard(
    customerId: String,
    cardNumber: String = "4242_4242_4242_4242".replace("_", ""),
    expMonth: Int = 8,
    expYear: Int = 2023,
    cvc: Int = 484,
) = createCardByCustomerId(customerId, CreateCardRequest(cardNumber, expMonth, expYear, cvc))

suspend fun CustomerService.createTestCartItems(
    customerId: String,
    productId: String,
    quantity: Long = 3
) = createCartItem(customerId, CreateCartItemRequest(productId, quantity))

suspend fun CustomerRepository.createTestCustomer(
    name: String = "John Doe",
    email: String = "Johndoe@mail.com",
    password: String = "Password0"
) = createCustomer(
    Customer(
        name = name,
        email = Email(email),
        password = Password(password)
    )
)

suspend fun OrderRepository.createTestOrder(
    customerId: ID = ID.random(),
    orderItems: Set<Order.OrderItem> = emptySet(),
    address: Address = Address(
        "Home",
        "US",
        "LA",
        "Unkwnon",
        "4424"
    ),
    card: Card = Card(
        "Visa",
        "4242_4242_4242_4242".replace("_", "").toLong(),
        LocalDate.now().plusMonths(10),
        452,
    )
) = createOrder(
    Order(
        customerId = customerId,
        orderItems = orderItems,
        address = address,
        card = card
    )
)