package com.shopping.domain.service

import com.shopping.domain.dto.product.request.CreateReviewRequest
import com.shopping.domain.dto.product.request.UpdateReviewRequest
import com.shopping.domain.dto.product.response.DeleteReviewResponse
import com.shopping.domain.dto.product.response.ProductDetailsResponse
import com.shopping.domain.dto.product.response.ProductResponse
import com.shopping.domain.dto.product.response.ReviewResponse

interface ProductService {

    suspend fun getProducts(
        limit: Long?,
        offset: Long?,
        method: String?,
        param: String?,
        categories: List<String>?,
        brands: List<String>?,
        minPrice: Double?,
        maxPrice:Double?,
        searchTerm: String?,
    ): List<ProductResponse>

    suspend fun getProductById(productId: String): ProductDetailsResponse

    suspend fun getReviewsByProductId(productId: String): List<ReviewResponse>

    suspend fun getReview(productId: String, customerId: String): ReviewResponse

    suspend fun createReview(productId: String, customerId: String, createReviewRequest: CreateReviewRequest): ReviewResponse

    suspend fun updateReview(productId: String, customerId: String, updateReviewRequest: UpdateReviewRequest): ReviewResponse

    suspend fun deleteReview(productId: String, customerId: String): DeleteReviewResponse
}
