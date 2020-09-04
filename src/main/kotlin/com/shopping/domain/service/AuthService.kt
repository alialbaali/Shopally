package com.shopping.domain.service

import com.shopping.domain.dto.customer.SignInRequest
import com.shopping.domain.dto.customer.SignUpRequest
import com.shopping.domain.dto.customer.TokenResponse

interface AuthService {

    suspend fun signUp(signUpRequest: SignUpRequest): TokenResponse

    suspend fun signIn(signInRequest: SignInRequest): TokenResponse

    suspend fun refreshToken(customerId: String): TokenResponse
}
