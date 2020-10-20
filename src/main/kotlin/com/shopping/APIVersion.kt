package com.shopping

import io.ktor.http.*

object APIVersion {

    private const val Vendor = "vnd.shopping"
    private val Json = ContentType.Application.Json
    private val Type = Json.contentType
    private val Subtype = Json.contentSubtype

    val V1 = ContentType(Type, "$Vendor-v1+$Subtype")
}
