package com.shopping.helper

import com.shopping.DefaultSpec
import com.shopping.domain.model.valueObject.ID
import com.shopping.helperModule
import com.shopping.toLocalDateTime
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.koin.test.inject
import java.time.LocalDateTime

class JWTHelperTest : DefaultSpec(helperModule) {

    private val jwtHelper by inject<JWTHelper>()

    init {

        Given("an Id") {
            When("calling jwt helper generate token method") {
                Then("it should return a valid jwt token with a subject matching the id") {

                    val id = ID.random()

                    val token = jwtHelper.generateToken(id)

                    val decodedJwt = JWTHelper.VERIFIER.verify(token)

                    val subject = decodedJwt.subject

                    subject.shouldNotBeNull()
                    subject shouldBe id.toString()
                }
            }
        }

        Given("an expiresAt date, issuedAt date and notBefore date ") {
            When("calling jwt helper generate token method") {
                Then("it should return a valid jwt token with dates matching the provided ones") {

                    val id = ID.random()
                    val issuedAt = LocalDateTime.now().minusHours(1)
                    val expiresAt = LocalDateTime.now().plusDays(1)
                    val notBefore = LocalDateTime.now()

                    val token = jwtHelper.generateToken(id, issuedAt, expiresAt, notBefore)

                    val decodedJwt = JWTHelper.VERIFIER.verify(token)

                    val decodedJwtIssuedAt = decodedJwt.issuedAt
                    val decodedJwtExpiresAt = decodedJwt.expiresAt
                    val decodedJwtNotBefore = decodedJwt.notBefore

                    decodedJwtIssuedAt.shouldNotBeNull()
                    decodedJwtIssuedAt.toLocalDateTime().toLocalDate() shouldBe issuedAt.toLocalDate()

                    decodedJwtExpiresAt.shouldNotBeNull()
                    decodedJwtExpiresAt.toLocalDateTime().toLocalDate() shouldBe expiresAt.toLocalDate()

                    decodedJwtNotBefore.shouldNotBeNull()
                    decodedJwtNotBefore.toLocalDateTime().toLocalDate() shouldBe notBefore.toLocalDate()
                }
            }
        }

        Given("a valid map of claims") {
            And("an Id") {
                When("calling jwt helper generate token method") {
                    Then("it should return a valid jwt token with the claims provided") {

                        val id = ID.random()
                        val claims = mapOf(
                            "claim1" to 1,
                            "claim2" to "two",
                            "claim3" to true,
                            "claim4" to LocalDateTime.now()
                        )

                        val token = jwtHelper.generateToken(id, claims = claims)

                        val decodedJwt = JWTHelper.VERIFIER.verify(token)

                        val decodedJwtClaims = decodedJwt.claims

                        decodedJwtClaims.shouldNotBeNull()
                        decodedJwtClaims.keys shouldContainAll claims.keys
                    }
                }
            }
        }
    }
}
