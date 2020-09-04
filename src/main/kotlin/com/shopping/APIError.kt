package com.shopping

import io.ktor.http.*

sealed class APIError(override val message: String?) : RuntimeException(message) {
    abstract val statusCode: HttpStatusCode
}

class AuthenticationError(message: String?) : APIError(message) {
    override val statusCode: HttpStatusCode
        get() = HttpStatusCode.Unauthorized
}

class AuthorizationError(message: String?) : APIError(message) {
    override val statusCode: HttpStatusCode
        get() = HttpStatusCode.Forbidden
}

class NotFoundError(message: String?) : APIError(message) {
    override val statusCode: HttpStatusCode
        get() = HttpStatusCode.NotFound
}

class BadRequestError(message: String?) : APIError(message) {
    override val statusCode: HttpStatusCode
        get() = HttpStatusCode.BadRequest
}

object Errors {

    const val INVALID_NAME = "Invalid name"
    const val INVALID_CREDENTIALS = "Invalid credentials"
    const val INVALID_REQUEST = "Invalid request"
    const val INVALID_TOKEN = "Invalid token"
    const val INVALID_EMAIL = "Invalid Email-Address"
    const val INVALID_PASSWORD = "Invalid password"
    const val PASSWORD_VALIDATION = "Password must contain at least 8 characters and one number"
    const val INVALID_IMAGE = "Invalid image url"
    const val INVALID_ISBN = "Invalid ISBN"
    const val INVALID_ID = "Invalid ID"
}
