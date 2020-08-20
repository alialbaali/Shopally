package com.shopping.domain.model

import com.shopping.domain.model.valueObject.*
import java.time.LocalDate

data class Customer(
    val id: ID = ID.random(),
    val name: String,
    val email: Email,
    val password: Password,
    val image: String = String(),
    val addresses: Set<Address> = setOf(),
    val cards: Set<Card> = setOf(),
    val creationDate: LocalDate = LocalDate.now(),
)