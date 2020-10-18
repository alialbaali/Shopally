package com.shopping

import com.auth0.jwt.interfaces.Payload
import com.cloudinary.Cloudinary
import com.cloudinary.Configuration
import com.shopping.domain.model.valueObject.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import java.io.File
import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun String.hash(): String {
    val secret = System.getenv("SECRET") ?: "4894"
    val hashKey = hex(secret)
    val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(this.toByteArray(Charsets.UTF_8)))
}

val Card.last4Numbers
    get() = this.number.toString().takeLast(4).toLong()

fun PartData.validate() {
    val isImageContentType = contentType?.match(ContentType.Image.Any) ?: badRequestError(Errors.ImageFormatValidation)
    if (!isImageContentType) badRequestError(Errors.InvalidRequest)
}

fun String.asID(): ID = ID.from(this)
    .getOrElse { badRequestError(it.message) }

fun String.asEmail(): Email = Email.create(this)
    .getOrElse { badRequestError(it.message) }

fun String.asPassword(): Password = Password.create(this, String::hash)
    .getOrElse { badRequestError("${it.message} ${Errors.PasswordValidation}") }

fun Int.asRating(): Rating = Rating.values()
    .find { it.ordinal.plus(1) == this } ?: badRequestError(Errors.RatingValidation)

fun Date.toLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault())

fun LocalDateTime.toDate(): Date = Date.from(this.atZone(ZoneId.systemDefault()).toInstant())

inline val PipelineContext<*, ApplicationCall>.jwtPayload: Payload
    get() = call.principal<JWTPrincipal>()?.payload ?: badRequestError(Errors.InvalidRequest)

inline val PipelineContext<*, ApplicationCall>.customerId: String
    get() = jwtPayload.subject ?: authorizationError(Errors.InvalidToken)

inline val PipelineContext<*, ApplicationCall>.productId: String
    get() = parameters["product-id"] ?: badRequestError(Errors.ProductIdParameterMissing)

inline val PartData.FileItem.size: String?
    get() = contentDisposition?.parameter(ContentDisposition.Parameters.Size)

inline val PipelineContext<*, ApplicationCall>.queryParameters: Parameters
    get() = call.request.queryParameters

inline val PipelineContext<*, ApplicationCall>.limit: Long?
    get() = queryParameters["limit"]?.toLongOrNull()

inline val PipelineContext<*, ApplicationCall>.offset: Long?
    get() = queryParameters["offset"]?.toLongOrNull()

inline val PipelineContext<*, ApplicationCall>.sort: String
    get() = queryParameters["sort"].toString()

inline val PipelineContext<*, ApplicationCall>.sortParam: String?
    get() = sort.substringBefore(':')

inline val PipelineContext<*, ApplicationCall>.sortMethod: String?
    get() = sort.substringAfter(':')

inline val PipelineContext<*, ApplicationCall>.parameters: Parameters
    get() = call.parameters

fun cloudinary(configuration: Configuration.() -> Unit): Cloudinary = Cloudinary().apply {
    config.apply(configuration)
}

fun InputStream.toFile(filePath: String): File = File(filePath).apply {
    outputStream().use { outputStream ->
        copyTo(outputStream)
    }
}

enum class SortingMethod {
    Asc, Desc
}

inline fun <T> Iterable<T>.sortByMethod(method: SortingMethod?, crossinline selector: (T) -> Comparable<*>?): Iterable<T> {
    return when (method) {
        SortingMethod.Asc -> sortedWith(compareBy(selector))
        SortingMethod.Desc -> sortedWith(compareByDescending(selector))
        else -> this
    }
}

fun String?.toSortingMethod(): SortingMethod? {
    return SortingMethod.values()
        .find { it.name.equals(this, ignoreCase = true) }
}