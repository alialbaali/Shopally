package com.shopping

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.shopping.controller.auth
import com.shopping.controller.customer
import com.shopping.controller.order
import com.shopping.controller.product
import com.shopping.domain.service.AuthService
import com.shopping.domain.service.CustomerService
import com.shopping.domain.service.OrderService
import com.shopping.domain.service.ProductService
import com.shopping.helper.JWTHelper
import com.stripe.Stripe
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.util.*
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.get
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = EngineMain.main(args)

@KtorExperimentalAPI
@kotlin.jvm.JvmOverloads
fun Application.module(isTesting: Boolean = false) {

    Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc"

    install(Locations)

    install(CallLogging) {
        level = Level.TRACE
        filter { call -> call.request.path().startsWith("/") }
    }

    install(Authentication) {
        jwt {

            verifier(JWTHelper.Verifier)

            challenge { _, _ -> authorizationError(Errors.InvalidToken) }

            validate { credentials ->

                val customerId = credentials.payload.subject ?: authorizationError(Errors.InvalidToken)

                AuthService.validateCustomerById(customerId)

                JWTPrincipal(credentials.payload)
            }
        }
    }

    install(ContentNegotiation) {
        jackson {
            propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            enable(SerializationFeature.INDENT_OUTPUT)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    install(Koin) {
        modules(
            dbModule,
            repositoryModule,
            dataSourceModule,
            helperModule,
            serviceModule
        )
    }

    install(StatusPages) {

        exception<APIError> { apiError ->
            call.respond(apiError.statusCode, mapOf("error" to apiError.message))
        }

        exception<Throwable> { error ->
            error.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to error.message))
        }
    }

    routing {
        get("/") { call.respond(HttpStatusCode.OK, mapOf("status" to "Healthy")) }
        customer(get<CustomerService>())
        product(get<ProductService>())
        order(get<OrderService>())
        auth(get<AuthService>())
    }
}
