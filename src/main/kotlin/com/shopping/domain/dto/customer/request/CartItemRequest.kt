package com.shopping.domain.dto.customer.request

import com.shopping.Errors
import com.shopping.asID
import com.shopping.badRequestError
import com.shopping.domain.model.valueObject.ID

class CreateCartItemRequest(
    val productId: String?,
    val quantity: Long?,
) {
    operator fun component1(): ID = productId?.asID() ?: badRequestError("Product Id ${Errors.PropertyMissing}")

    operator fun component2(): Long = quantity?.let {
        quantity.takeIf { quantity in 1..10L } ?: badRequestError(Errors.QuantityRange)
    } ?: badRequestError("Quantity ${Errors.PropertyMissing}")
}

typealias UpdateCartItemRequest = CreateCartItemRequest
