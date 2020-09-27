package com.shopping.service

import com.shopping.*
import com.shopping.domain.dto.product.request.CreateReviewRequest
import com.shopping.domain.dto.product.request.UpdateReviewRequest
import com.shopping.domain.dto.product.response.DeleteReviewResponse
import com.shopping.domain.dto.product.response.ProductDetailsResponse
import com.shopping.domain.dto.product.response.ProductResponse
import com.shopping.domain.dto.product.response.ReviewResponse
import com.shopping.domain.model.Customer
import com.shopping.domain.model.Product
import com.shopping.domain.model.valueObject.Review
import com.shopping.domain.repository.CustomerRepository
import com.shopping.domain.repository.ProductRepository
import com.shopping.domain.service.ProductService
import org.koin.core.KoinComponent
import org.koin.core.get
import java.io.File

private const val PaginationDefaultLimit = 50L
private const val PaginationDefaultOffset = 0L

class ProductServiceImpl(private val productRepository: ProductRepository) : ProductService, KoinComponent {

    override suspend fun getProducts(limit: Long?, offset: Long?): List<ProductResponse> {

        val validLimit = limit?.let {
            if (it < 100) it else badRequestError(Errors.LimitSize)
        }

        return productRepository.getProducts(
            validLimit ?: PaginationDefaultLimit,
            offset ?: PaginationDefaultOffset,
        ).fold(
            onSuccess = { products -> products.map { createProductResponse(it) } },
            onFailure = { internalServerError(it.message) }
        )
    }

    override suspend fun getProductById(productId: String): ProductDetailsResponse {
        return productRepository.getProductById(productId.asID())
            .fold(
                onSuccess = { createProductDetailsResponse(it) },
                onFailure = { notFoundError(it.message) }
            )
    }

    override suspend fun getReviewsByProductId(productId: String): List<ReviewResponse> {
        return productRepository.getReviewsByProductId(productId.asID())
            .fold(
                onSuccess = { reviews -> reviews.map { createReviewResponse(it, productId) } },
                onFailure = { notFoundError(it.message) }
            )
    }

    override suspend fun getReview(productId: String, customerId: String): ReviewResponse {
        return productRepository.getReview(productId.asID(), customerId.asID())
            .fold(
                onSuccess = { createReviewResponse(it, productId) },
                onFailure = { notFoundError(it.message) }
            )
    }

    override suspend fun createReview(
        productId: String,
        customerId: String,
        createReviewRequest: CreateReviewRequest
    ): ReviewResponse {

        productRepository.getReview(productId.asID(), customerId.asID())
            .exceptionOrNull() ?: badRequestError(Errors.ReviewAlreadyExist)

        val (rating, description) = createReviewRequest

        val review = Review(customerId.asID(), rating, description)

        return productRepository.createReview(productId.asID(), review)
            .fold(
                onSuccess = { createReviewResponse(it, productId) },
                onFailure = { badRequestError(it.message) }
            )
    }

    override suspend fun updateReview(
        productId: String,
        customerId: String,
        updateReviewRequest: UpdateReviewRequest
    ): ReviewResponse {

        val dbReview = productRepository.getReview(productId.asID(), customerId.asID())
            .getOrElse { notFoundError(Errors.ReviewDoesntExist) }

        var (rating, description) = updateReviewRequest

        rating = rating ?: dbReview.rating
        description = description ?: dbReview.description

        val review = Review(
            customerId.asID(),
            rating,
            description,
        )

        return productRepository.updateReview(productId.asID(), review)
            .fold(
                onSuccess = { createReviewResponse(review, productId) },
                onFailure = { badRequestError(it.message) }
            )
    }

    override suspend fun deleteReview(productId: String, customerId: String): DeleteReviewResponse {
        return productRepository.deleteReview(productId.asID(), customerId.asID())
            .fold(
                onSuccess = { DeleteReviewResponse(productId, customerId) },
                onFailure = { badRequestError(it.message) },
            )
    }

    private suspend fun createProductResponse(product: Product): ProductResponse {
        val reviews = productRepository.getReviewsByProductId(product.id)
            .getOrElse { notFoundError(it.message) }
            .toSet()

        return product.copy(reviews = reviews).toProductResponse()
    }

    private suspend fun createProductDetailsResponse(product: Product): ProductDetailsResponse {
        val specs = productRepository.getSpecsByProductId(product.id)
            .getOrElse { notFoundError(it.message) }

        val reviews = productRepository.getReviewsByProductId(product.id)
            .getOrElse { notFoundError(it.message) }
            .toSet()

        return product.copy(reviews = reviews, specs = specs).toProductDetailsResponse()
    }

    private suspend fun createReviewResponse(review: Review, productId: String): ReviewResponse {
        val customer = get<CustomerRepository>()
            .getCustomerById(review.customerId)
            .getOrElse { notFoundError(it.message) }

        return review.toReviewResponse(review, customer, productId)
    }
}

private fun Product.toProductDetailsResponse(): ProductDetailsResponse {
    return ProductDetailsResponse(
        id.toString(),
        category.toString(),
        brand,
        name,
        description,
        price,
        imagesUrls,
        specs,
        releaseDate.toString(),
        creationDate.toString()
    )
}

private fun Product.toProductResponse(): ProductResponse {
    return ProductResponse(
        id.toString(),
        category.toString(),
        brand,
        name,
        imagesUrls.firstOrNull() ?: "",
        price,
        avgRating,
    )
}

private fun Review.toReviewResponse(review: Review, customer: Customer, productId: String): ReviewResponse {
    return ReviewResponse(
        productId,
        customer.id.toString(),
        customer.name,
        customer.imageUrl,
        review.rating.ordinal.plus(1),
        review.description,
        review.creationDate.toString()
    )
}
