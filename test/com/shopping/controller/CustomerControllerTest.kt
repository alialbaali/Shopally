package com.shopping.controller

import com.shopping.*
import io.ktor.http.*
import io.ktor.server.testing.*

class CustomerControllerTest : DefaultSpec(serviceModule, repositoryModule, helperModule, dataSourceModule, dbModule) {

    init {
        Given("an endpoint") {
            When("we call") {
                Then("this") {
                }
            }
        }
    }
}
