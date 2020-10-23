package com.shopping.fake

import com.shopping.Errors
import com.shopping.domain.model.Product
import com.shopping.domain.model.valueObject.ID
import com.shopping.domain.model.valueObject.Review
import com.shopping.domain.repository.ProductRepository
import net.bytebuddy.implementation.bytecode.Throw
import java.io.File

class FakeProductRepository(
    private val products: MutableSet<Product> = mutableSetOf(),
) : ProductRepository {

    override suspend fun searchProducts(
        limit: Long,
        offset: Long,
        categories: Set<Product.Category>,
        brands: Set<String>,
        minPrice: Double?,
        maxPrice: Double?,
        searchTerm: String?
    ): Result<List<Product>> {
        val searchedProducts = products.filter { product ->
            product.category.name.equals(searchTerm, ignoreCase = true)
                    || product.brand.equals(searchTerm, ignoreCase = true)
                    || product.name.equals(searchTerm, ignoreCase = true)
                    || product.description.equals(searchTerm, ignoreCase = true)
        }
        return getProducts(limit, offset, categories, brands, minPrice, maxPrice)
            .map { it.plus(searchedProducts) }
    }

    override suspend fun getProducts(
        limit: Long,
        offset: Long,
        categories: Set<Product.Category>,
        brands: Set<String>,
        minPrice: Double?,
        maxPrice: Double?
    ): Result<List<Product>> {
        return products
            .filterIndexed { index, product ->
                product.category in categories
                        || product.brand in brands
                        || product.price >= minPrice ?: Double.MIN_VALUE
                        || product.price <= maxPrice ?: Double.MAX_VALUE
                        && index >= offset
            }
            .take(limit.toInt())
            .let { Result.success(it) }
    }

    override suspend fun getProductById(productId: ID): Result<Product> {
        return products.find { it.id == productId }
            ?.let { Result.success(it) } ?: Result.failure(Throwable(Errors.ProductDoesntExist))
    }

    override suspend fun createProduct(product: Product, productImageFiles: List<File>): Result<Product> {
        return products.add(
            product.copy(
                imagesUrls = productImageFiles
                    .map { it.absolutePath }
                    .toSet()
            )
        ).let { Result.success(product) }
    }

    override suspend fun updateProduct(product: Product): Result<Product> {
        return getProductById(product.id)
            .onSuccess {
                products.remove(it)
                products.add(product)
            }.map { product }
    }

    override suspend fun deleteProductById(productId: ID): Result<ID> {
        return getProductById(productId)
            .onSuccess { products.remove(it) }
            .map { product -> product.id }
    }

    override suspend fun getImagesByProductId(productId: ID): Result<Set<String>> {
        return getProductById(productId)
            .map { product -> product.imagesUrls }
    }

    override suspend fun updateImage(productId: ID, productImageFile: File): Result<String> {
        return getProductById(productId)
            .onSuccess { updateProduct(it.copy(imagesUrls = it.imagesUrls.plus(productImageFile.absolutePath))) }
            .map { productImageFile.absolutePath }
    }

    override suspend fun deleteImage(productId: ID, productImageUrl: String): Result<String> {
        return getProductById(productId)
            .onSuccess {
                products.remove(it)
                products.add(it.copy(imagesUrls = it.imagesUrls.minus(productImageUrl)))
            }
            .map { productImageUrl }
    }

    override suspend fun getSpecsByProductId(productId: ID): Result<Map<String, String>> {
        return getProductById(productId)
            .map { product -> product.specs }
    }

    override suspend fun updateSpec(productId: ID, spec: Pair<String, String>): Result<Pair<String, String>> {
        return getProductById(productId)
            .onSuccess {
                products.remove(it)
                products.add(it.copy(specs = it.specs.plus(spec)))
            }
            .map { spec }
    }

    override suspend fun deleteSpec(productId: ID, specKey: String): Result<String> {
        return getProductById(productId)
            .onSuccess {
                products.remove(it)
                products.add(it.copy(specs = it.specs.minus(specKey)))
            }
            .map { specKey }
    }

    override suspend fun getReviewsByProductId(productId: ID): Result<List<Review>> {
        return getProductById(productId)
            .map { it.reviews.toList() }
    }

    override suspend fun getReview(productId: ID, customerId: ID): Result<Review> {
        return getProductById(productId)
            .map { product ->
                product.reviews
                    .find { review -> review.customerId == customerId } ?: return Result.failure(Throwable(Errors.ReviewDoesntExist))
            }
    }

    override suspend fun createReview(productId: ID, review: Review): Result<Review> {
        return getProductById(productId)
            .onSuccess {
                products.remove(it)
                products.add(it.copy(reviews = it.reviews.plus(review)))
            }.map { review }
    }

    override suspend fun updateReview(productId: ID, review: Review): Result<Review> {
        return getProductById(productId)
            .onSuccess {
                products.remove(it)
                products.add(it)
            }
            .map { review }
    }

    override suspend fun deleteReview(productId: ID, customerId: ID): Result<Pair<ID, ID>> {
        return getProductById(productId)
            .onSuccess {
                products.remove(it)
                products.add(it)
            }
            .map { productId to customerId }
    }

}
