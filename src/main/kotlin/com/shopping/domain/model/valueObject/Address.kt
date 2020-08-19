package com.shopping.domain.model.valueObject

data class Address(
    val name: String,
    val country: String,
    val city: String,
    val line: String,
    val zipCode: String,
)