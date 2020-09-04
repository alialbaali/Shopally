package com.shopping.domain.dto.customer

import com.shopping.AuthenticationError
import com.shopping.domain.model.valueObject.Email
import com.shopping.domain.model.valueObject.Password
import com.shopping.hash

class SignInRequest(
    val email: String,
    val password: String
) {

    operator fun component1(): Email = Email.create(email).getOrElse {
        throw AuthenticationError(it.message)
    }

    operator fun component2(): Password = Password.create(password) { hash() }.getOrElse {
        throw AuthenticationError(it.message)
    }
}
