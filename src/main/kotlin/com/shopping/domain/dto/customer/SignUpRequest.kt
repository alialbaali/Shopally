package com.shopping.domain.dto

import com.shopping.AuthenticationError
import com.shopping.Errors
import com.shopping.domain.model.valueObject.Email
import com.shopping.domain.model.valueObject.Password
import com.shopping.hash

class SignUpRequest(
    val name: String,
    val email: String,
    val password: String
) {

    operator fun component1(): String = if (name.isBlank()) throw AuthenticationError(Errors.INVALID_NAME) else name

    operator fun component2(): Email = Email.create(email).getOrElse {
        throw AuthenticationError(it.message)
    }

    operator fun component3(): Password = Password.create(password) { hash() }.getOrElse {
        throw AuthenticationError("${it.message}; ${Errors.PASSWORD_VALIDATION}")
    }

}