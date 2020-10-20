package com.shopping.controller

import com.shopping.*
import com.shopping.domain.dto.customer.request.*
import com.shopping.domain.service.CustomerService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.customer(customerService: CustomerService) {

    accept(APIVersion.V1) {

        authenticate {

            route("/customers/{customer_id}") {

                get {

                    val customerResponse = customerService.getCustomerById(customerId)

                    call.respond(HttpStatusCode.OK, customerResponse)
                }

                patch {

                    val updateCustomerRequest = call.receiveOrNull<UpdateCustomerRequest>()
                        ?: badRequestError(Errors.InvalidRequest)

                    val customerResponse = customerService.updateCustomerById(customerId, updateCustomerRequest)

                    call.respond(HttpStatusCode.OK, customerResponse)
                }

                patch("/image") {

                    call.receiveMultipart().forEachPart { part ->

                        part.validate()

                        when (part) {

                            is PartData.FileItem -> {

                                val inputStream = part.streamProvider()

                                val imageUrl = customerService.updateCustomerImageById(customerId, inputStream)

                                call.respond(HttpStatusCode.OK, mapOf("image_url" to imageUrl))

                                part.dispose
                            }

                            else -> badRequestError(Errors.ImageFormatValidation)
                        }
                    }
                }

                patch("/password") {

                    val updatePasswordRequest = call.receiveOrNull<UpdateCustomerPasswordRequest>()
                        ?: badRequestError(Errors.InvalidRequest)

                    customerService.updateCustomerPassword(customerId, updatePasswordRequest)

                    call.respond(HttpStatusCode.OK)
                }

                delete {

                    val id = customerService.deleteCustomer(customerId)

                    call.respond(HttpStatusCode.NoContent, mapOf("customer_id" to id))
                }

                route("/cart") {

                    get {

                        val cart = customerService.getCartByCustomerId(customerId)

                        call.respond(HttpStatusCode.OK, mapOf("cart" to cart))
                    }

                    post {

                        val cartItemRequest = call.receiveOrNull<CreateCartItemRequest>()
                            ?: badRequestError(Errors.InvalidRequest)

                        val cartItemResponse = customerService.createCartItem(customerId, cartItemRequest)

                        call.respond(HttpStatusCode.Created, cartItemResponse)
                    }

                    patch {

                        val cartItemRequest = call.receiveOrNull<CreateCartItemRequest>()
                            ?: badRequestError(Errors.InvalidRequest)

                        val cartItemResponse = customerService.updateCartItem(customerId, cartItemRequest)

                        call.respond(HttpStatusCode.OK, cartItemResponse)
                    }

                    delete {

                        val deleteCartItemRequest = call.receiveOrNull<Map<String, String>>() ?: badRequestError(Errors.InvalidRequest)

                        val cartItemId = deleteCartItemRequest.getOrElse("product_id") {
                            badRequestError("product_id ${Errors.PropertyMissing}")
                        }

                        val deletedCartItemId = customerService.deleteCartItem(customerId, cartItemId)

                        call.respond(HttpStatusCode.NoContent, mapOf("product_id" to deletedCartItemId))
                    }
                }

                route("/addresses") {

                    get {

                        val addresses = customerService.getAddressesByCustomerId(customerId)

                        call.respond(HttpStatusCode.OK, mapOf("addresses" to addresses))
                    }

                    post {

                        val createAddressRequest = call.receiveOrNull<CreateAddressRequest>()
                            ?: badRequestError(Errors.InvalidRequest)

                        val addressResponse = customerService.createAddressByCustomerId(customerId, createAddressRequest)

                        call.respond(HttpStatusCode.Created, addressResponse)
                    }

                    delete {

                        val deleteAddressRequest = call.receiveOrNull<Map<String, String>>()
                            ?: badRequestError(Errors.InvalidRequest)

                        val addressName = deleteAddressRequest.getOrElse("name") {
                            badRequestError("name ${Errors.PropertyMissing}")
                        }

                        val deletedAddressName = customerService.deleteAddressByName(customerId, addressName)

                        call.respond(HttpStatusCode.NoContent, mapOf("name" to deletedAddressName))
                    }
                }

                route("/cards") {

                    get {

                        val cardResponses = customerService.getCardsByCustomerId(customerId)

                        call.respond(HttpStatusCode.OK, mapOf("cards" to cardResponses))
                    }

                    post {

                        val createCardRequest = call.receiveOrNull<CreateCardRequest>()
                            ?: badRequestError(Errors.InvalidRequest)

                        val cardResponse = customerService.createCardByCustomerId(customerId, createCardRequest)

                        call.respond(HttpStatusCode.Created, cardResponse)
                    }

                    delete {

                        val deleteCartRequest = call.receiveOrNull<Map<String, String>>()
                            ?: badRequestError(Errors.InvalidRequest)

                        val cardLast4Numbers = deleteCartRequest
                            .getOrElse("card_last4_numbers") { badRequestError("card_last4_numbers ${Errors.PropertyMissing}") }
                            .toLongOrNull() ?: badRequestError(Errors.InvalidRequest)

                        val deletedCardNumber = customerService.deleteCartByLast4(customerId, cardLast4Numbers)

                        call.respond(HttpStatusCode.NoContent, mapOf("card_last4_numbers" to deletedCardNumber))
                    }
                }
            }
        }
    }
}
