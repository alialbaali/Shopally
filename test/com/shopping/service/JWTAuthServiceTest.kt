package com.shopping.service

import com.shopping.APIError
import com.shopping.AuthenticationError
import com.shopping.KoinTestListener
import com.shopping.di.helperModule
import com.shopping.di.serviceModule
import com.shopping.domain.dto.SignInRequest
import com.shopping.domain.dto.SignUpRequest
import com.shopping.domain.model.valueObject.ID
import com.shopping.domain.service.AuthService
import com.shopping.FakeRepositoryModule
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldNotBeEmpty
import org.koin.test.KoinTest
import org.koin.test.inject

class JWTAuthServiceTest : BehaviorSpec(), KoinTest {

    private val authService by inject<AuthService>()

    override fun listeners(): List<TestListener> = listOf(KoinTestListener(serviceModule, helperModule, FakeRepositoryModule))

    init {

        Given("a valid sign up request") {
            When("calling auth service sign up method") {
                Then("it should return a token response with a valid access and refresh token") {

                    val signUpRequest = SignUpRequest(
                        "John",
                        "johndoe@mail.com",
                        "password0"
                    )

                    val tokenResponse = authService.signUp(signUpRequest)

                    tokenResponse.shouldNotBeNull()
                    tokenResponse.accessToken.shouldNotBeEmpty()
                    tokenResponse.refreshToken.shouldNotBeEmpty()

                }
            }
        }

        Given("an invalid sign up request") {
            When("calling auth service sign up method") {
                Then("it should throw an API Error exception") {

                    val signUpRequest = SignUpRequest(
                        "John",
                        "johndoe@com",
                        "pass"
                    )

                    shouldThrow<APIError> {
                        authService.signUp(signUpRequest)
                    }

                }
            }
        }

        Given("a valid sign in request") {
            When("calling auth service sign in method") {
                Then("it should return a token response with a valid access and refresh token") {

                    val signUpRequest = SignUpRequest(
                        "John",
                        "johndoe@mail.com",
                        "password0"
                    )

                    authService.signUp(signUpRequest)

                    val signInRequest = SignInRequest(
                        "johndoe@mail.com",
                        "password0"
                    )

                    val tokenResponse = authService.signIn(signInRequest)

                    tokenResponse.shouldNotBeNull()
                    tokenResponse.accessToken.shouldNotBeEmpty()
                    tokenResponse.refreshToken.shouldNotBeEmpty()

                }
            }
        }

        Given("an invalid sign in request") {
            When("calling auth service sign in method") {
                Then("it should throw an API Error exception") {

                    val signInRequest = SignInRequest(
                        "johndoe@com",
                        "pass"
                    )

                    shouldThrow<APIError> {
                        authService.signIn(signInRequest)
                    }

                }
            }
        }

        Given("a two customers with the same email") {
            When("calling auth service sign up method on each of them") {
                Then("it should throw an Authentication Error exception") {

                    val signUpRequest = SignUpRequest(
                        "John",
                        "johndoe@mail.com",
                        "password0"
                    )

                    authService.signUp(signUpRequest)

                    shouldThrowExactly<AuthenticationError> {
                        authService.signUp(signUpRequest)
                    }

                }
            }
        }

        Given("a new customer") {
            When("calling auth service sign in method") {
                Then("it should throw an Authentication Error exception") {

                    val signInRequest = SignInRequest(
                        "johndoe@mail.com",
                        "password0"
                    )

                    shouldThrowExactly<AuthenticationError> {
                        authService.signIn(signInRequest)
                    }

                }
            }
        }

        Given("a valid Id") {
            When("calling auth service refresh token method") {
                Then("it should return a Token Response with access and refresh tokens") {

                    val id = ID.random().toString()

                    val tokenResponse = authService.refreshToken(id)

                    tokenResponse.shouldNotBeNull()
                    tokenResponse.accessToken.shouldNotBeEmpty()
                    tokenResponse.refreshToken.shouldNotBeEmpty()

                }
            }
        }

        Given("an invalid Id") {
            When("calling auth service refresh token method") {
                Then("it should throw an Authentication Error exception") {

                    val id = "3232-43209423-43242-3223"

                    shouldThrowExactly<AuthenticationError> {
                        authService.refreshToken(id)
                    }

                }
            }
        }

    }

}