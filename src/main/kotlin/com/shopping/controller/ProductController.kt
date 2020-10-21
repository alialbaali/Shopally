package com.shopping.controller

import com.shopping.*
import com.shopping.domain.dto.product.request.CreateReviewRequest
import com.shopping.domain.dto.product.request.UpdateReviewRequest
import com.shopping.domain.dto.product.response.ProductResponse
import com.shopping.domain.service.ProductService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.routing.get
import io.ktor.util.*
import kotlin.reflect.KProperty1


@KtorExperimentalAPI
fun Routing.product(productService: ProductService) {

    accept(APIVersion.V1) {

        route("/products") {

            get {

                val brands = queryParameters.getAll("brands")
                val categories = queryParameters.getAll("categories")
                val minPrice = queryParameters["min_price"]?.let { it.toDoubleOrNull() ?: badRequestError(Errors.PriceMustBeDouble) }
                val maxPrice = queryParameters["max_price"]?.let { it.toDoubleOrNull() ?: badRequestError(Errors.PriceMustBeDouble) }
                val searchTerm = queryParameters["search"]

                val products = productService.getProducts(
                    limit,
                    offset,
                    sortMethod,
                    sortParam,
                    categories,
                    brands,
                    minPrice,
                    maxPrice,
                    searchTerm,
                )

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
}
