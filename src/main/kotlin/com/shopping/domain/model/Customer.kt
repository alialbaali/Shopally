package com.shopping.domain.model

import com.shopping.domain.model.valueObject.*
import java.time.LocalDate

private const val CUSTOMER_DEFAULT_IMAGE_URL = "https://res.cloudinary.com/shopping-cloud/image/upload/v1598449571/placeHolder_elsjfb.png"

data class Customer(
    val id: ID = ID.random(),
    val name: String,
    val email: Email,
    val password: Password,
    val imageUrl: String = CUSTOMER_DEFAULT_IMAGE_URL,
    val cart: Cart = Cart.EMPTY,
    val addresses: Set<Address> = setOf(),
    val cards: Set<Card> = setOf(),
    val creationDate: LocalDate = LocalDate.now(),
)