package com.shopping.service

import com.shopping.*
import com.shopping.domain.dto.customer.request.SignInRequest
import com.shopping.domain.dto.customer.request.SignUpRequest
import com.shopping.domain.service.AuthService
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldNotBeEmpty
import org.koin.test.inject

class JWTAuthServiceTest : DefaultSpec(testServiceModule, helperModule, fakeRepositoryModule) {

    private val authService by inject<AuthService>()

    init {

        Given("a valid sign up request") {
            When("calling auth service sign up method") {
                Then("it should return a token response with a valid access and refresh token") {

                    val tokenResponse = authService.signUpTestCustomer()

                    tokenResponse.shouldNotBeNull()
                    tokenResponse.accessToken.shouldNotBeEmpty()
                    tokenResponse.refreshToken.shouldNotBeEmpty()
                }
            }
        }

        Given("an invalid sign up request") {
            When("calling auth service sign up method") {
                Then("it should throw an API Error exception") {

                    shouldThrow<APIError> {
                        authService.signUpTestCustomer(name = "John", email = "johndoe@com", password = "pass")
                    }

                }
            }
        }

        Given("a valid sign in request") {
            When("calling auth service sign in method") {
                Then("it should return a token response with a valid access and refresh token") {

                    shouldNotThrowAny { authService.signUpTestCustomer(email = "johndoe3@mail.com") }

                    val signInRequest = SignInRequest(
                        "johndoe3@mail.com",
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

                    authService.signUpTestCustomer(email = "Johndoe4@mail.com")

                    shouldThrowAny {
                        authService.signUpTestCustomer(email = "Johndoe4@mail.com")
                    }
                }
            }
        }

        Given("a new customer") {
            When("calling auth service sign in method") {
                Then("it should throw an Authentication Error exception") {

                    val signInRequest = SignInRequest(
                        "johndoe@mail0.com",
                        "password0"
                    )

                    shouldThrowAny {
                        authService.signIn(signInRequest)
                    }
                }
            }
        }

        Given("a valid Id") {
            When("calling auth service refresh token method") {
                Then("it should return a Token Response with access and refresh tokens") {

                    val response = authService.signUpTestCustomer(email = "Johndoe5@mail.com")

                    val tokenResponse = authService.refreshToken(response.customerId)

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

                    shouldThrowAny {
                        authService.refreshToken(id)
                    }
                }
            }
        }
    }
}
