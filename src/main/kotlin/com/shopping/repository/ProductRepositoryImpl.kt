package com.shopping.repository

import com.shopping.Errors
import com.shopping.db.*
import com.shopping.domain.CloudDataSource
import com.shopping.domain.model.Product
import com.shopping.domain.model.valueObject.ID
import com.shopping.domain.model.valueObject.Review
import com.shopping.domain.repository.ProductRepository
import com.squareup.sqldelight.db.SqlDriver
import java.io.File
import java.time.LocalDate

private const val ProductsFolder = "Products"

class ProductRepositoryImpl(
    private val productsQueries: ProductsQueries,
    private val productImagesQueries: ProductImagesQueries,
    private val productSpecsQueries: ProductSpecsQueries,
    private val productReviewsQueries: ProductReviewsQueries,
    private val cloudDataSource: CloudDataSource,
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

        val products = productsQueries.searchProducts(
            searchTerm, searchTerm,
            searchTerm, searchTerm,
            categories, brands,
            minPrice, maxPrice,
            limit, offset
        ).executeAsList()
            .map { dbProduct ->
                val imageUrl = productImagesQueries.getImagesUrlByProductId(dbProduct.id) { _, url -> url }
                    .executeAsList()
                    .firstOrNull() ?: ""

                dbProduct.toProduct(setOf(imageUrl))
            }

        return Result.success(products)
    }

    override suspend fun getProducts(
        limit: Long,
        offset: Long,
        categories: Set<Product.Category>,
        brands: Set<String>,
        minPrice: Double?,
        maxPrice: Double?,
    ): Result<List<Product>> {

        val products = productsQueries.getProducts(categories, brands, minPrice, maxPrice, limit, offset)
            .executeAsList()
            .map { dbProduct ->
                val imageUrl = productImagesQueries.getImagesUrlByProductId(dbProduct.id) { _, url -> url }
                    .executeAsList()
                    .firstOrNull() ?: ""

                dbProduct.toProduct(setOf(imageUrl))
            }

        return Result.success(products)
    }

    override suspend fun getProductById(productId: ID): Result<Product> {

        val product = productsQueries.getProductById(productId)
            .executeAsOneOrNull()
            ?.toProduct() ?: return Result.failure(Throwable(Errors.ProductDoesntExist))

        return Result.success(product)
    }

    override suspend fun createProduct(product: Product, productImageFiles: List<File>): Result<Product> {

        return productsQueries.transactionWithResult {

            productsQueries.createProduct(
                product.id,
                product.category,
                product.brand,
                product.name,
                product.description,
                product.price,
                product.releaseDate,
                product.creationDate,
            )

            productImageFiles.forEach { productImageFile ->
                val imageUrl = uploadProductImageFile(product.id, productImageFile)
                    .getOrElse {
                        return@transactionWithResult Result.failure(Throwable(Errors.ImageUploadFailed))
                    }

                productImagesQueries.createImageUrl(product.id, imageUrl)
            }

            product.specs.forEach { (key, value) ->
                productSpecsQueries.createSpec(product.id, key, value)
            }

            Result.success(product)
        }
    }

    override suspend fun updateProduct(product: Product): Result<Product> {

        productsQueries.updateProductByID(
            product.category,
            product.brand,
            product.name,
            product.description,
            product.price,
            product.releaseDate,
            product.id
        )

        return getProductById(product.id)
    }

    override suspend fun deleteProductById(productId: ID): Result<ID> = runCatching {

        return productsQueries.transactionWithResult {

            productsQueries.deleteProductById(productId)

            productImagesQueries.deleteImagesByProductId(productId)

            productSpecsQueries.deleteSpecsByProductId(productId)

            Result.success(productId)
        }
    }

    override suspend fun getImagesByProductId(productId: ID): Result<Set<String>> {

        val imagesUrls = productImagesQueries.getImagesUrlByProductId(productId) { _, url -> url }
            .executeAsList()
            .toSet()

        return Result.success(imagesUrls)
    }

    override suspend fun updateImage(productId: ID, productImageFile: File): Result<String> {

        return productImagesQueries.transactionWithResult {

            val imageUrl = uploadProductImageFile(productId, productImageFile)
                .getOrElse {
                    return@transactionWithResult Result.failure(Throwable(Errors.ImageUploadFailed))
                }

            productImagesQueries.createImageUrl(productId, imageUrl)

            Result.success(imageUrl)
        }
    }

    override suspend fun deleteImage(productId: ID, productImageUrl: String): Result<String> {

        return productImagesQueries.transactionWithResult {

            cloudDataSource.deleteImage(productImageUrl)
                .getOrElse {
                    return@transactionWithResult Result.failure(Throwable(Errors.ImageUrlDoesntExist))
                }

            productImagesQueries.deleteImageUrl(productImageUrl)

            Result.success(productImageUrl)
        }
    }

    override suspend fun getSpecsByProductId(productId: ID): Result<Map<String, String>> {

        val specs = productSpecsQueries.getSpecsByProductId(productId)
            .executeAsList()
            .map { dbSpec -> dbSpec.toSpec() }
            .toMap()

        return Result.success(specs)
    }

    override suspend fun updateSpec(productId: ID, spec: Pair<String, String>): Result<Pair<String, String>> {

        val (key, value) = spec

        val dbSpecs = productSpecsQueries.getSpecsByProductId(productId)
            .executeAsList()
            .map { dbSpec -> dbSpec.toSpec() }
            .toMap()

        if (dbSpecs.containsKey(key))
            productSpecsQueries.updateSpecByKey(value, productId, key)
        else
            productSpecsQueries.createSpec(productId, key, value)

        return Result.success(spec)
    }

    override suspend fun deleteSpec(productId: ID, specKey: String): Result<String> {

        productSpecsQueries.deleteSpecByKey(productId, specKey)

        return Result.success(specKey)
    }

    override suspend fun getReviewsByProductId(productId: ID): Result<List<Review>> {

        val reviews = productReviewsQueries.getReviewsByProductId(productId)
            .executeAsList()
            .map { dbReview -> dbReview.toReview() }

        return Result.success(reviews)
    }

    override suspend fun getReview(productId: ID, customerId: ID): Result<Review> {

        val review = productReviewsQueries.getReview(productId, customerId)
            .executeAsOneOrNull()
            ?.toReview() ?: return Result.failure(Throwable(Errors.ReviewDoesntExist))

        return Result.success(review)
    }

    override suspend fun createReview(productId: ID, review: Review): Result<Review> {

        val (customerId, rating, description, creationDate) = review

        productReviewsQueries.createReview(productId, customerId, rating, description, creationDate)

        return getReview(productId, customerId)
    }

    override suspend fun updateReview(productId: ID, review: Review): Result<Review> {

        val (customerId, rating, description, _) = review

        productReviewsQueries.updateReview(rating, description, productId, customerId)

        return getReview(productId, customerId)
    }

    override suspend fun deleteReview(productId: ID, customerId: ID): Result<Pair<ID, ID>> {

        productReviewsQueries.deleteReview(productId, customerId)

        return Result.success(productId to customerId)
    }

    private fun uploadProductImageFile(productId: ID, productImageFile: File): Result<String> {

        return cloudDataSource.uploadImage(
            productImageFile,
            imageId = productId.toString().plus(productImageFile.nameWithoutExtension),
            folderName = ProductsFolder,
        )
    }
}

private fun Products.toProduct(
    imagesUrls: Set<String> = emptySet(),
    specs: Map<String, String> = emptyMap(),
    reviews: Set<Review> = emptySet()
): Product {
    return Product(
        id,
        category,
        brand,
        name,
        description,
        price,
        imagesUrls,
        specs,
        reviews,
        release_date,
        creation_date,
    )
}

private fun ProductSpecs.toSpec(): Pair<String, String> {
    return key to value
}

private fun ProductReviews.toReview(): Review {
    return Review(customer_id, rating, description, creation_date)
}
