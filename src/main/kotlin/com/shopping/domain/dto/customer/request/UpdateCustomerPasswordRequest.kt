package com.shopping.domain.dto.customer.request

import com.shopping.Errors
import com.shopping.asPassword
import com.shopping.authenticationError
import com.shopping.domain.model.valueObject.Password

class UpdateCustomerPasswordRequest(
    private val oldPassword: String?,
    private val newPassword: String?,
) {
    operator fun component1(): Password = oldPassword?.asPassword() ?: authenticationError("Old Password ${Errors.PropertyMissing}")

    operator fun component2(): Password = newPassword?.asPassword() ?: authenticationError("New Password ${Errors.PropertyMissing}")
}
