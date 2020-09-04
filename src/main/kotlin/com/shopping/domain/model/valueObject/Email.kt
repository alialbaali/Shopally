package com.shopping.domain.model.valueObject

import com.shopping.Errors

inline class Email(private val value: String) {

    companion object {

        private val REGEX = Regex(
            "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+" +
                "(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\" +
                "x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\" +
                ".)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\." +
                "){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\" +
                "x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
        )

        fun create(email: String): Result<Email> {

            val lowerCasedEmail = email.toLowerCase()

            return if (lowerCasedEmail.matches(REGEX))
                Result.success(Email(lowerCasedEmail))
            else
                Result.failure(Throwable(Errors.INVALID_EMAIL))
        }
    }

    override fun toString(): String = value
}
