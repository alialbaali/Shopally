package com.shopping.domain.dto.customer.request

import com.shopping.Errors
import com.shopping.asEmail
import com.shopping.asPassword
import com.shopping.authenticationError
import com.shopping.domain.model.valueObject.Email
import com.shopping.domain.model.valueObject.Password

class SignUpRequest(
    val name: String?,
    val email: String?,
    val password: String?
) {
    operator fun component1(): String = name?.let {
        name.takeIf { name.isNotBlank() } ?: authenticationError("Name ${Errors.PropertyEmpty}")
    } ?: authenticationError("Name ${Errors.PropertyMissing}")

    operator fun component2(): Email = email?.asEmail() ?: authenticationError("Email ${Errors.PropertyMissing}")

    operator fun component3(): Password = password?.asPassword() ?: authenticationError("Password ${Errors.PropertyMissing}")
}
