package com.shopping.domain.repository

import com.shopping.domain.model.Product
import com.shopping.domain.model.valueObject.ID
import com.shopping.domain.model.valueObject.Review
import java.io.File

interface ProductRepository {

    suspend fun getProducts(limit: Long, offset: Long): Result<List<Product>>

    suspend fun getProductById(productId: ID): Result<Product>

    suspend fun createProduct(product: Product, productImageFiles: List<File>): Result<Product>

    suspend fun updateProduct(product: Product): Result<Product>

    suspend fun deleteProductById(productId: ID): Result<ID>

    suspend fun getImagesByProductId(productId: ID): Result<Set<String>>

    suspend fun updateImage(productId: ID, productImageFile: File): Result<String>

    suspend fun deleteImage(productId: ID, productImageUrl: String): Result<String>

    suspend fun getSpecsByProductId(productId: ID): Result<Map<String, String>>

    suspend fun updateSpec(productId: ID, spec: Pair<String, String>): Result<Pair<String, String>>

    suspend fun deleteSpec(productId: ID, specKey: String): Result<String>

    suspend fun getReviewsByProductId(productId: ID): Result<List<Review>>

    suspend fun getReview(productId: ID, customerId: ID): Result<Review>

    suspend fun createReview(productId: ID, review: Review): Result<Review>

    suspend fun updateReview(productId: ID, review: Review): Result<Review>

    suspend fun deleteReview(productId: ID, customerId: ID): Result<Pair<ID, ID>>
}
