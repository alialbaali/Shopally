package com.shopping.domain.dto.customer.request

import com.shopping.Errors
import com.shopping.asEmail
import com.shopping.asPassword
import com.shopping.authenticationError
import com.shopping.domain.model.valueObject.Email
import com.shopping.domain.model.valueObject.Password

class SignInRequest(
    val email: String?,
    val password: String?,
) {
    operator fun component1(): Email = email?.asEmail() ?: authenticationError("Email ${Errors.PropertyMissing}")

    operator fun component2(): Password = password?.asPassword() ?: authenticationError("Password ${Errors.PropertyMissing}")
}
