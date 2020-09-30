package com.shopping.domain.dto.customer.response

class CustomerResponse(
    val id: String,
    val name: String,
    val email: String,
    val imageUrl: String,
    val addressesCount: Long,
    val cardsCount: Long,
    val creationDate: String,
)
