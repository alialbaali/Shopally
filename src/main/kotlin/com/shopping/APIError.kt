package com.shopping

import io.ktor.http.*

sealed class APIError(override val message: String?) : RuntimeException(message) {
    abstract val statusCode: HttpStatusCode
}

fun authenticationError(message: String?): Nothing = throw AuthenticationError(message)
private class AuthenticationError(message: String?) : APIError(message) {
    override val statusCode: HttpStatusCode
        get() = HttpStatusCode.Unauthorized
}

fun authorizationError(message: String?): Nothing = throw AuthorizationError(message)
private class AuthorizationError(message: String?) : APIError(message) {
    override val statusCode: HttpStatusCode
        get() = HttpStatusCode.Forbidden
}

fun badRequestError(message: String?): Nothing = throw BadRequestError(message)
private class BadRequestError(message: String?) : APIError(message) {
    override val statusCode: HttpStatusCode
        get() = HttpStatusCode.BadRequest
}

fun notFoundError(message: String?): Nothing = throw NotFoundError(message)
private class NotFoundError(message: String?) : APIError(message) {
    override val statusCode: HttpStatusCode
        get() = HttpStatusCode.NotFound
}

fun internalServerError(message: String?): Nothing = throw InternalServerError(message)
private class InternalServerError(message: String?) : APIError(message) {
    override val statusCode: HttpStatusCode
        get() = HttpStatusCode.InternalServerError
}

object Errors {

    const val PropertyEmpty = "property can't be empty"
    const val PropertyMissing = "property can't be missing"
    const val YearProperty = "Year property is out of range"
    const val MonthProperty = "Month property is out of range"
    const val InvalidDate = "Invalid date"
    const val QuantityRange = "Quantity must be between 1 and 10"
    const val RatingValidation = "Rating property must be between 1 and 5"
    const val ImageFormatValidation = "Unsupported Format"
    const val InvalidName = "Invalid name. Please provide a valid name!"
    const val InvalidCredentials = "Invalid credentials. Please provide valid credentials"
    const val ProductIdParameterMissing = "Product Id parameter is missing"
    const val InvalidRequest = "Invalid request"
    const val InvalidToken = "Invalid token. Please provide a valid token; if you don't have one, you can get by signing up though /auth/new"
    const val InvalidCardNumber = "Invalid card number"
    const val InvalidEmail = "Invalid Email address. Please provide a valid email address"
    const val InvalidPassword = "Invalid password. Please provide a valid password; password must contain at least 8 characters and 1 number"
    const val PasswordsDontMatch = "Passwords doesn't match"
    const val PasswordValidation = "Password must contain at least 8 characters and one number"
    const val InvalidId = "Invalid ID"
    const val SomethingWentWrong = "Something went wrong!"
    const val CustomerDoesntExist = "Customer doesn't exist"
    const val UsedEmail = "Email not available"
    const val ProductDoesntExist = "Product doesn't exist"
    const val CartItemDoesntExist = "Cart item doesn't exist"
    const val CartItemAlreadyExist = "Cart item Already exist"
    const val AddressDoesntExist = "Address doesn't exist"
    const val AddressAlreadyExist = "Address Already Exist"
    const val CardDoesntExist = "Card doesn't exist"
    const val CardAlreadyExist = "Card already exist"
    const val OrderDoesntExist = "Order doesn't exist"
    const val ImageUrlDoesntExist = "Image Url doesn't exist"
    const val ReviewDoesntExist = "Review doesn't exist"
    const val ReviewAlreadyExist = "Review already exist"
    const val ImageUploadFailed = "Image upload failed"
    const val LimitSize = "Maximum limit is 100 and default is 50"
    const val CardDateInTheFuture=  "Card date must be in the future"
}
