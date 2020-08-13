package com.shopping.helper

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.shopping.domain.model.inline.Id
import com.shopping.toDate
import java.time.LocalDateTime
import java.util.*

class JWTHelper {

    companion object {

        private val SECRET = System.getenv("JWT_SECRET")

        val ISSUER: String = System.getenv("JWT_ISSUER")

        val AUDIENCE: String = System.getenv("JWT_AUDIENCE")

        val ALGORITHM: Algorithm = Algorithm.HMAC256(SECRET)

        val VERIFIER: JWTVerifier = JWT.require(ALGORITHM)
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .build()

    }

    fun generateToken(
        id: Id,
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
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .sign(ALGORITHM)

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