package com.shopping.domain.service

import com.shopping.AuthorizationError
import com.shopping.Errors
import com.shopping.domain.dto.customer.request.SignInRequest
import com.shopping.domain.dto.customer.request.SignUpRequest
import com.shopping.domain.dto.customer.response.TokenResponse
import com.shopping.domain.model.valueObject.asID
import com.shopping.domain.repository.CustomerRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

interface AuthService {

    companion object : KoinComponent {

        private val customerRepository by inject<CustomerRepository>()

        suspend fun validateCustomerById(customerId: String) =
            customerRepository.getCustomerById(customerId.asID()).getOrElse {
                throw AuthorizationError(Errors.InvalidRequest)
            }
    }

    suspend fun signUp(signUpRequest: SignUpRequest): TokenResponse

    suspend fun signIn(signInRequest: SignInRequest): TokenResponse

    suspend fun refreshToken(customerId: String): TokenResponse
}
