package com.shopping.domain.dto.product.request

import com.shopping.asRating
import com.shopping.domain.model.valueObject.Rating

class UpdateReviewRequest(
    val rating: Int?,
    val description: String? = null,
) {
    operator fun component1(): Rating? = rating?.asRating()

    operator fun component2(): String? = description
}
