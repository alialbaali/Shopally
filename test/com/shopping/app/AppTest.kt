package com.shopping.app

import io.kotest.assertions.ktor.shouldHaveContent
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.koin.test.KoinTest

class AppTest : BehaviorSpec(), KoinTest {

    init {

        given("a root endpoint") {
            `when`("requesting the endpoint") {
                then("it should return OK and HELLO WORLD") {

                    withTestApplication({ module(testing = true) }) {
                        handleRequest(HttpMethod.Get, "/").apply {

                            response shouldHaveStatus HttpStatusCode.OK
                            response shouldHaveContent "HELLO WORLD!"

                        }
                    }

                }
            }
        }

    }
}
