package com.shopping

import io.ktor.util.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@KtorExperimentalAPI
fun String.hash(): String {
    val hashKey = hex(System.getenv("SECRET") ?: "4894")
    val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(this.toByteArray(Charsets.UTF_8)))
}

fun Date.toLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault())

fun LocalDateTime.toDate(): Date = Date.from(this.atZone(ZoneId.systemDefault()).toInstant());