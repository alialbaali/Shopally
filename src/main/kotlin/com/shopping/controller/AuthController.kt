package com.shopping.controller

import com.shopping.AuthenticationError
import com.shopping.AuthorizationError
import com.shopping.Errors
import com.shopping.domain.dto.SignInRequest
import com.shopping.domain.dto.SignUpRequest
import com.shopping.domain.model.inline.validateId
import com.shopping.domain.service.AuthService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.auth(authService: AuthService) {

    route("/auth") {

        post("/new") {

            val signUpRequest = call.receiveOrNull<SignUpRequest>() ?: throw AuthenticationError(Errors.INVALID_REQUEST)

            val tokenResponse = authService.signUp(signUpRequest)

            call.respond(HttpStatusCode.Created, tokenResponse)

        }

        post {

            val signInRequest = call.receiveOrNull<SignInRequest>() ?: throw AuthenticationError(Errors.INVALID_REQUEST)

            val tokenResponse = authService.signIn(signInRequest)

            call.respond(HttpStatusCode.Created, tokenResponse)

        }

        authenticate {

            get {

                val customerId = call.authentication.principal<JWTPrincipal>()?.payload?.subject
                    ?: throw AuthorizationError(Errors.INVALID_TOKEN)

                val tokenResponse = authService.refreshToken(customerId)

                call.respond(HttpStatusCode.Accepted, tokenResponse)

            }

        }

    }

}