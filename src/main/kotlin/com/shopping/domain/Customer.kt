package com.shopping.domain

import java.time.Instant

data class Customer(
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String,
    val creationDate: String = Instant.now().toString()
)