package com.shopping.domain.repository

import com.shopping.domain.model.Product
import com.shopping.domain.model.valueObject.ID
import com.shopping.domain.model.valueObject.Review
import java.io.File

interface ProductRepository {

    suspend fun getProductsByCategory(productCategory: Product.Category): Result<List<Product>>

    suspend fun getProductById(productId: ID): Result<Product>

    suspend fun createProduct(product: Product, productImageFiles: List<File>): Result<Product>

    suspend fun updateProduct(product: Product, productImageFiles: List<File>): Result<Product>

    suspend fun deleteProductById(productId: ID): Result<ID>


    suspend fun getSpecsByProductId(productId: ID): Result<Map<String, String>>


    suspend fun getReviewsByProductId(productId: ID): Result<List<Review>>

    suspend fun createReview(productId: ID, review: Review): Result<Review>

    suspend fun updateReview(productId: ID, review: Review): Result<Review>

    suspend fun deleteReview(productId: ID, customerId: ID): Result<ID>
}
