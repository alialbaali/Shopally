package com.shopping.controller

import com.shopping.*
import com.shopping.domain.dto.product.request.CreateReviewRequest
import com.shopping.domain.dto.product.request.UpdateReviewRequest
import com.shopping.domain.service.ProductService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*

@KtorExperimentalAPI
fun Routing.product(productService: ProductService) {

    route("/products") {

        get {

            val limit = queryParameters["limit"]?.toLongOrNull()

            val offset = queryParameters["offset"]?.toLongOrNull()

            val products = productService.getProducts(limit, offset)

            call.respond(HttpStatusCode.OK, mapOf("products" to products))
        }

        route("/{product-id}") {

            get {

                val product = productService.getProductById(productId)

                call.respond(HttpStatusCode.OK, product)
            }

            route("/reviews") {

                get {

                    val reviews = productService.getReviewsByProductId(productId)

                    call.respond(HttpStatusCode.OK, mapOf("reviews" to reviews))
                }

                authenticate {

                    post {

                        val createReviewRequest = call.receiveOrNull<CreateReviewRequest>()
                            ?: badRequestError(Errors.InvalidRequest)

                        val reviewResponse = productService.createReview(productId, customerId, createReviewRequest)

                        call.respond(HttpStatusCode.Created, reviewResponse)
                    }

                    patch {

                        val updateReviewRequest = call.receiveOrNull<UpdateReviewRequest>()
                            ?: badRequestError(Errors.InvalidRequest)

                        val reviewResponse = productService.updateReview(productId, customerId, updateReviewRequest)

                        call.respond(HttpStatusCode.OK, reviewResponse)
                    }

                    delete {

                        val deleteReviewResponse = productService.deleteReview(productId, customerId)

                        call.respond(HttpStatusCode.NoContent, deleteReviewResponse)
                    }

                }
            }
        }
    }
}