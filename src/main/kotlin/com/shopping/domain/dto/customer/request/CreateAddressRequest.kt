package com.shopping.domain.dto.customer.request

import com.shopping.Errors
import com.shopping.badRequestError

class CreateAddressRequest(
    val name: String?,
    val country: String?,
    val city: String?,
    val line: String?,
    val zipCode: String?,
) {
    operator fun component1(): String = name ?: badRequestError("Name ${Errors.PropertyMissing}")

    operator fun component2(): String = country ?: badRequestError("Country ${Errors.PropertyMissing}")

    operator fun component3(): String = city ?: badRequestError("City ${Errors.PropertyMissing}")

    operator fun component4(): String = line ?: badRequestError("Line ${Errors.PropertyMissing}")

    operator fun component5(): String = zipCode ?: badRequestError("Zipcode ${Errors.PropertyMissing}")
}
