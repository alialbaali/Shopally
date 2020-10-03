package com.shopping.domain.service

import com.shopping.Errors
import com.shopping.asID
import com.shopping.authenticationError
import com.shopping.domain.dto.customer.request.SignInRequest
import com.shopping.domain.dto.customer.request.SignUpRequest
import com.shopping.domain.dto.customer.response.TokenResponse
import com.shopping.domain.repository.CustomerRepository
import org.koin.core.KoinComponent
import org.koin.core.get

interface AuthService {

    companion object : KoinComponent {
        suspend fun validateCustomerById(customerId: String) {
            get<CustomerRepository>()
                .getCustomerById(customerId.asID())
                .getOrElse { authenticationError(Errors.CustomerDoesntExist) }
        }
    }

    suspend fun signUp(signUpRequest: SignUpRequest): TokenResponse

    suspend fun signIn(signInRequest: SignInRequest): TokenResponse

    suspend fun refreshToken(customerId: String): TokenResponse
}
