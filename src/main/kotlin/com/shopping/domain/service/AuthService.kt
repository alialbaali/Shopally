package com.shopping.domain.service

import com.shopping.domain.dto.SignInRequest
import com.shopping.domain.dto.SignUpRequest
import com.shopping.domain.dto.TokenResponse
import com.shopping.domain.model.inline.Id

interface AuthService {

    suspend fun signUp(signUpRequest: SignUpRequest): TokenResponse

    suspend fun signIn(signInRequest: SignInRequest): TokenResponse

    suspend fun refreshToken(customerId: String): TokenResponse

}