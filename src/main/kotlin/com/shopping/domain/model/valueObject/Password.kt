package com.shopping.domain.model.valueObject

import com.shopping.Errors

inline class Password(private val value: String) {

    companion object {

        private val REGEX = Regex("^(?=.*?[0-9]).{8,}$")

        fun create(password: String, hash: String.() -> String): Result<Password> =
            if (password.matches(REGEX)) {
                val hashedPassword = password.hash()
                Result.success(Password(hashedPassword))
            } else {
                Result.failure(Throwable(Errors.INVALID_PASSWORD))
            }

    }

    override fun toString(): String = value

}