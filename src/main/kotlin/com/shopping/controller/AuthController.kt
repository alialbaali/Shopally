package com.shopping.controller

import com.shopping.Errors
import com.shopping.badRequestError
import com.shopping.customerId
import com.shopping.domain.dto.customer.request.SignInRequest
import com.shopping.domain.dto.customer.request.SignUpRequest
import com.shopping.domain.service.AuthService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.auth(authService: AuthService) {

    route("/auth") {

        post("/new") {

            val signUpRequest = call.receiveOrNull<SignUpRequest>() ?: badRequestError(Errors.InvalidRequest)

            val tokenResponse = authService.signUp(signUpRequest)

            call.respond(HttpStatusCode.Created, tokenResponse)
        }

        post {

            val signInRequest = call.receiveOrNull<SignInRequest>() ?: badRequestError(Errors.InvalidRequest)

            val tokenResponse = authService.signIn(signInRequest)

            call.respond(HttpStatusCode.OK, tokenResponse)
        }

        authenticate {

            get {

                val tokenResponse = authService.refreshToken(customerId)

                call.respond(HttpStatusCode.OK, tokenResponse)
            }
        }
    }
}
