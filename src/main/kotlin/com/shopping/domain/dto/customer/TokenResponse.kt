package com.shopping.domain.dto

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)