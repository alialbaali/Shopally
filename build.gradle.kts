val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val koin_version: String by project

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.squareup.sqldelight:gradle-plugin:1.4.0")
    }
}


plugins {
    application
    kotlin("jvm") version "1.3.72"
    id("com.squareup.sqldelight") version "1.4.0"
}

sqldelight {
    database("ShoppingDatabase") {
        packageName = "com.shopping.db"
    }
}

group = "com.shopping"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.cio.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    google()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-cio:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("com.squareup.sqldelight:sqlite-driver:1.4.0")
    implementation("org.koin:koin-ktor:2.1.6")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("com.squareup.sqldelight:sqlite-driver:1.4.0")
    testImplementation("org.koin:koin-test:2.1.6")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.1.3")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.1.3")
    testImplementation("io.kotest:kotest-property-jvm:4.1.3")
    testImplementation("io.kotest:kotest-runner-console-jvm:4.1.3")
    testImplementation("io.kotest:kotest-assertions-ktor-jvm:4.1.3")
    testImplementation("io.kotest:kotest-extensions-koin-jvm:4.1.3")
}


tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xallow-result-return-type")
            jvmTarget = "1.8"
        }
    }

    test {
        useJUnitPlatform()
    }
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
