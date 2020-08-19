package com.shopping.domain.model.valueObject

import java.time.LocalDate

data class Card(
    val name: String,
    val brand: String,
    val number: Long,
    val balance: Balance = Balance.MIN,
    val ccv: Long,
    val expirationDate: LocalDate,
)