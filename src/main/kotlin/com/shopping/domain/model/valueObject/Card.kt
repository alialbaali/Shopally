package com.shopping.domain.model.valueObject

import java.time.LocalDate

data class Card(
    val brand: String = String(),
    val number: Long,
    val expirationDate: LocalDate,
    val cvc: Long = 0,
)
