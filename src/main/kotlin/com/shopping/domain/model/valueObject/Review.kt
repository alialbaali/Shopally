package com.shopping.domain.model.valueObject

import java.time.LocalDate

data class Review(
    val customerId: ID,
    val rating: Rating,
    val description: String? = null,
    val creationDate: LocalDate = LocalDate.now(),
)

enum class Rating {
    One, Two, Three, Four, Five
}