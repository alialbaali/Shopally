
object App {

    const val Group = "com.shopping"

    const val Version = "1.0"

    const val MainClassName = "io.ktor.server.cio.EngineMain"

}

object Versions {

    const val Kotlin = "1.4.10"

    const val SqlDelight = "1.4.4"

    const val KtLint = "0.39.0"

    const val KtLintGradle = "9.4.0"

    const val Cloudinary = "1.26.0"

    const val Stripe = "19.45.0"

    const val Ktor = "1.4.1"

    const val LogBack = "1.2.3"

    const val Koin = "2.1.6"

    const val Mockk = "1.10.2"

    const val Kotest = "4.3.0"

    const val Shadow = "5.0.0"

}

object Database {

    const val Name = "ShoppingDatabase"

    const val Package = "com.shopping.db"

}

object Plugins {

    const val SqlDelight = "com.squareup.sqldelight"

    const val KtLint = "org.jlleitschuh.gradle.ktlint"

    const val Kotlin = "jvm"

    const val Shadow = "com.github.johnrengelman.shadow"

}

object Libs {

    const val Cloudinary = "com.cloudinary:cloudinary-http44:${Versions.Cloudinary}"

    const val Stripe = "com.stripe:stripe-java:${Versions.Stripe}"

    const val SqlDelight = "com.squareup.sqldelight:gradle-plugin:${Versions.SqlDelight}"

    const val SqlDelightDriver = "com.squareup.sqldelight:sqlite-driver:${Versions.SqlDelight}"

    const val Koin = "org.koin:koin-ktor:${Versions.Koin}"

    object Ktor {

        const val Engine = "io.ktor:ktor-server-cio:${Versions.Ktor}"

        const val Core = "io.ktor:ktor-server-core:${Versions.Ktor}"

        const val Host = "io.ktor:ktor-server-host-common:${Versions.Ktor}"

        const val Auth = "io.ktor:ktor-auth:${Versions.Ktor}"

        const val AuthJwt = "io.ktor:ktor-auth-jwt:${Versions.Ktor}"

        const val Jackson = "io.ktor:ktor-jackson:${Versions.Ktor}"

        const val Locations = "io.ktor:ktor-locations:${Versions.Ktor}"

        const val LogBack = "ch.qos.logback:logback-classic:${Versions.LogBack}"

    }

    object Test {

        const val SqlDelightDriver = Libs.SqlDelightDriver

        const val Ktor = "io.ktor:ktor-server-tests:${Versions.Ktor}"

        const val Koin = "org.koin:koin-test:${Versions.Koin}"

        const val Mockk = "io.mockk:mockk:${Versions.Mockk}"

        object Kotest {

            const val Runner = "io.kotest:kotest-runner-junit5-jvm:${Versions.Kotest}"

            const val Property = "io.kotest:kotest-property-jvm:${Versions.Kotest}"

            const val Assertions = "io.kotest:kotest-assertions-core-jvm:${Versions.Kotest}"

            const val KtorAssertions = "io.kotest:kotest-assertions-ktor-jvm:${Versions.Kotest}"

        }

    }

}