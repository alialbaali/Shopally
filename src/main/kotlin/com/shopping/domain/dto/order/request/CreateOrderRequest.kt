package com.shopping.domain.dto.order.request

import com.shopping.Errors
import com.shopping.badRequestError

class CreateOrderRequest(
    val addressName: String?,
    val cardLast4Numbers: String?,
) {
    operator fun component1(): String = addressName ?: badRequestError("Address Name ${Errors.PropertyMissing}")
    operator fun component2(): String = cardLast4Numbers ?: badRequestError("Card Last 4 ${Errors.PropertyMissing}")
}
