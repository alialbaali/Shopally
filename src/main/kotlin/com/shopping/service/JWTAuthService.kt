package com.shopping.service

import com.shopping.AuthenticationError
import com.shopping.Errors
import com.shopping.domain.dto.SignInRequest
import com.shopping.domain.dto.SignUpRequest
import com.shopping.domain.dto.TokenResponse
import com.shopping.domain.model.Customer
import com.shopping.domain.model.valueObject.ID
import com.shopping.domain.model.valueObject.asID
import com.shopping.domain.repository.CustomerRepository
import com.shopping.domain.service.AuthService
import com.shopping.helper.JWTHelper
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.time.LocalDateTime

class JWTAuthService(private val customerRepository: CustomerRepository) : AuthService, KoinComponent {

    override suspend fun signUp(signUpRequest: SignUpRequest): TokenResponse {

        val (name, email, password) = signUpRequest

        val customer = Customer(name = name, email = email, password = password)

        customerRepository.createCustomer(customer).getOrElse {
            throw AuthenticationError(it.message)
        }

        return createTokenResponse(customer.id)

    }

    override suspend fun signIn(signInRequest: SignInRequest): TokenResponse {

        val (email, password) = signInRequest

        val customer = customerRepository.getCustomerByEmail(email).getOrElse {
            throw AuthenticationError(it.message)
        }

        return if (customer.password == password)
            createTokenResponse(customer.id)
        else
            throw AuthenticationError(Errors.INVALID_CREDENTIALS)

    }

    override suspend fun refreshToken(customerId: String): TokenResponse = createTokenResponse(customerId.asID())

    private fun createTokenResponse(id: ID): TokenResponse {

        val jwtHelper by inject<JWTHelper>()

        val accessToken = jwtHelper.generateToken(id)

        val refreshToken = jwtHelper.generateToken(id, expiresAt = LocalDateTime.now().plusDays(30))

        return TokenResponse(accessToken, refreshToken)

    }

}