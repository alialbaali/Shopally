package com.shopping.domain.dto.customer.request

import com.shopping.Errors
import com.shopping.asEmail
import com.shopping.badRequestError
import com.shopping.domain.model.valueObject.Email

class UpdateCustomerRequest(
    val name: String?,
    val email: String?,
) {
    operator fun component1(): String? = name?.let {
        name.takeIf { name.isNotBlank() } ?: badRequestError(Errors.InvalidName)
    }

    operator fun component2(): Email? = email?.asEmail()
}
