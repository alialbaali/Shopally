package com.shopping.domain.dto

import com.shopping.AuthenticationError
import com.shopping.domain.model.inline.Email
import com.shopping.domain.model.inline.Password
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