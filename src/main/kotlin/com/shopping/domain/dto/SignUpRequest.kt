package com.shopping.domain.dto

import com.shopping.AuthenticationError
import com.shopping.Errors
import com.shopping.domain.model.inline.Email
import com.shopping.domain.model.inline.Name
import com.shopping.domain.model.inline.Password
import com.shopping.hash

class SignUpRequest(
    val name: String,
    val email: String,
    val password: String
) {

    operator fun component1(): Name = Name.create(name).getOrElse {
        throw AuthenticationError(it.message)
    }

    operator fun component2(): Email = Email.create(email).getOrElse {
        throw AuthenticationError(it.message)
    }

    operator fun component3(): Password = Password.create(password) { hash() }.getOrElse {
        throw AuthenticationError("${it.message}; ${Errors.PASSWORD_VALIDATION}")
    }

}