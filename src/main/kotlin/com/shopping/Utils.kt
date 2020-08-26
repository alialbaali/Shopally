package com.shopping

import com.auth0.jwt.interfaces.Payload
import com.cloudinary.Cloudinary
import com.cloudinary.Configuration
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
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

@KtorExperimentalAPI
fun String.hash(): String {
    val secret = System.getenv("SECRET") ?: "4894"
    val hashKey = hex(secret)
    val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(this.toByteArray(Charsets.UTF_8)))
}

fun Date.toLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault())

fun LocalDateTime.toDate(): Date = Date.from(this.atZone(ZoneId.systemDefault()).toInstant());

inline val PipelineContext<*, ApplicationCall>.jwtPayload: Payload
    get() = call.principal<JWTPrincipal>()?.payload ?: throw BadRequestException(Errors.INVALID_REQUEST)

inline val PipelineContext<*, ApplicationCall>.customerId: String
    get() = jwtPayload.subject ?: throw AuthorizationError("Missing ID")

inline val PartData.FileItem.size get() = contentDisposition?.parameter(ContentDisposition.Parameters.Size)

fun cloudinary(configuration: Configuration.() -> Unit): Cloudinary = Cloudinary().apply {
    config.apply(configuration)
}

fun InputStream.toFile(filePath: String): File = File(filePath).apply {
    outputStream().use { outputStream ->
        copyTo(outputStream)
    }
}