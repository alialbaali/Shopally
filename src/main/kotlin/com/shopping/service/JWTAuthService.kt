package com.shopping.service

import com.shopping.Errors
import com.shopping.authenticationError
import com.shopping.domain.dto.customer.request.SignInRequest
import com.shopping.domain.dto.customer.request.SignUpRequest
import com.shopping.domain.dto.customer.response.TokenResponse
import com.shopping.domain.model.Customer
import com.shopping.domain.model.valueObject.asID
import com.shopping.domain.repository.CustomerRepository
import com.shopping.domain.service.AuthService
import com.shopping.helper.JWTHelper
import com.shopping.notFoundError
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.time.LocalDateTime

class JWTAuthService(private val customerRepository: CustomerRepository) : AuthService, KoinComponent {

    override suspend fun signUp(signUpRequest: SignUpRequest): TokenResponse {

        val (name, email, password) = signUpRequest

        customerRepository.getCustomerByEmail(email)
            .exceptionOrNull() ?: authenticationError(Errors.UsedEmail)

        val customer = Customer(name = name, email = email, password = password)

        return customerRepository.createCustomer(customer)
            .fold(
                onSuccess = { createTokenResponse(it) },
                onFailure = { authenticationError(it.message) }
            )
    }

    override suspend fun signIn(signInRequest: SignInRequest): TokenResponse {

        val (email, password) = signInRequest

        return customerRepository.getCustomerByEmail(email)
            .fold(
                onSuccess = {
                    if (it.password == password)
                        createTokenResponse(it)
                    else
                        authenticationError(Errors.InvalidCredentials)
                },
                onFailure = { notFoundError(it.message) }
            )
    }

    override suspend fun refreshToken(customerId: String): TokenResponse {
        return customerRepository.getCustomerById(customerId.asID())
            .fold(
                onSuccess = { createTokenResponse(it) },
                onFailure = { notFoundError(it.message) }
            )
    }

    private fun createTokenResponse(customer: Customer): TokenResponse {

        val id = customer.id

        val jwtHelper by inject<JWTHelper>()

        val accessToken = jwtHelper.generateToken(id)

        val refreshToken = jwtHelper.generateToken(id, expiresAt = LocalDateTime.now().plusDays(30))

        return TokenResponse(id.toString(), accessToken, refreshToken)
    }
}
