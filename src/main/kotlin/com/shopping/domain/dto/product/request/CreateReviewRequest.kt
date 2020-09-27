package com.shopping.domain.dto.product.request

import com.shopping.Errors
import com.shopping.asRating
import com.shopping.badRequestError
import com.shopping.domain.model.valueObject.Rating

class CreateReviewRequest(
    val rating: Int?,
    val description: String? = null,
) {
    operator fun component1(): Rating = rating?.asRating() ?: badRequestError("Rating ${Errors.PropertyMissing}")

    operator fun component2(): String? = description
}