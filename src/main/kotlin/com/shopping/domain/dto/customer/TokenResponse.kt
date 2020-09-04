package com.shopping.domain.dto.customer

data class TokenResponse(
    val customerId: String,
    val accessToken: String,
    val refreshToken: String,
)
