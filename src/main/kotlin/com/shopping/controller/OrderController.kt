package com.shopping.controller

import com.shopping.APIVersion
import com.shopping.Errors
import com.shopping.badRequestError
import com.shopping.customerId
import com.shopping.domain.dto.order.request.CreateOrderRequest
import com.shopping.domain.service.OrderService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.order(orderService: OrderService) {

    accept(APIVersion.V1) {

        authenticate {

            route("/orders") {

                get {

                    val ordersResponse = orderService.getOrdersByCustomerId(customerId)

                    call.respond(HttpStatusCode.OK, ordersResponse)
                }

                post {

                    val createOrderRequest = call.receiveOrNull<CreateOrderRequest>()
                        ?: badRequestError(Errors.InvalidRequest)

                    val orderDetailsResponse = orderService.createOrder(customerId, createOrderRequest)

                    call.respond(HttpStatusCode.Created, orderDetailsResponse)
                }

                route("/{id}") {

                    get {

                        val orderId = call.parameters["id"] ?: badRequestError(Errors.InvalidRequest)

                        val orderDetailsResponse = orderService.getOrderById(customerId, orderId)

                        call.respond(HttpStatusCode.OK, orderDetailsResponse)
                    }
                }
            }
        }
    }
}
