package com.shopping.domain.dto.product.response

class ProductDetailsResponse(
    val id: String,
    val category: String,
    val brand: String,
    val name: String,
    val description: String,
    val price: Double,
    val imagesUrls: Set<String>,
    val specs: Map<String, String>,
    val releaseDate: String,
    val creationDate: String
)
