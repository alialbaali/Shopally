package com.shopping.controller

import com.shopping.*
import io.ktor.server.testing.*
import io.ktor.util.*

@OptIn(KtorExperimentalAPI::class)
class AuthControllerTest : DefaultSpec(serviceModule, repositoryModule, helperModule, dataSourceModule, dbModule) {

    init {

        withTestApplication({ module(true) }) {

        }
    }
}
