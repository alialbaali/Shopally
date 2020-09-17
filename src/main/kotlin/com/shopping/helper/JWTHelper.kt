package com.shopping.helper

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier
import com.shopping.domain.model.valueObject.ID
import com.shopping.toDate
import java.time.LocalDateTime
import java.util.*
import com.auth0.jwt.algorithms.Algorithm as JWTAlgorithm

class JWTHelper {

    companion object {

        private val Secret = System.getenv("JWTSecret") ?: "Secret"

        val Issuer: String = System.getenv("JWTIssuer") ?: "Issuer"

        val Audience: String = System.getenv("JWTAudience") ?: "Audience"

        val Algorithm: JWTAlgorithm = JWTAlgorithm.HMAC256(Secret)

        val Verifier: JWTVerifier = JWT.require(Algorithm)
            .withIssuer(Issuer)
            .withAudience(Audience)
            .build()
    }

    fun generateToken(
        id: ID,
        issuedAt: LocalDateTime? = LocalDateTime.now(),
        expiresAt: LocalDateTime? = LocalDateTime.now().plusDays(7),
        notBefore: LocalDateTime? = null,
        claims: Map<String, Any> = mapOf()
    ): String = JWT.create()
        .withSubject(id.toString())
        .withIssuedAt(issuedAt?.toDate())
        .withExpiresAt(expiresAt?.toDate())
        .withNotBefore(notBefore?.toDate())
        .withClaims(claims)
        .withIssuer(Issuer)
        .withAudience(Audience)
        .sign(Algorithm)
}

private fun JWTCreator.Builder.withClaims(claims: Map<String, Any>): JWTCreator.Builder {
    claims.forEach { (key, value) ->
        when (value) {
            is Int -> withClaim(key, value)
            is Date -> withClaim(key, value)
            is Long -> withClaim(key, value)
            is Double -> withClaim(key, value)
            is String -> withClaim(key, value)
            is Boolean -> withClaim(key, value)
            is LocalDateTime -> withClaim(key, value.toDate())
            else -> throw IllegalArgumentException("Unsupported value type: $value")
        }
    }
    return this
}
