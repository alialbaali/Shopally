package com.shopping.domain.model

import com.shopping.domain.model.valueObject.ID
import java.time.LocalDate

data class Product(
    val id: ID = ID.random(),
    val category: Category,
    val brand: String,
    val name: String,
    val description: String,
    val price: Double,
    val images: Set<String>,
    val specs: Map<String, String>,
    val releaseDate: LocalDate,
    val creationDate: LocalDate = LocalDate.now(),
) {
    enum class Category {
        VideoGames, Movies,
    }
}