val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val koin_version: String by project
val kotest_version: String by project

buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.squareup.sqldelight:gradle-plugin:1.4.0")
    }
}

plugins {
    application
    kotlin("jvm") version "1.4.0"
    id("com.squareup.sqldelight") version "1.4.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.3.0"
}

sqldelight {
    database("ShoppingDatabase") {
        packageName = "com.shopping.db"
    }
}

group = "com.shopping"
version = "0.2"

ktlint {
    version.set("0.38.1")
    coloredOutput.set(true)
    disabledRules.set(mutableListOf("no-wildcard-imports"))
    filter {
        exclude("**/generated/**")
        exclude { element -> element.file.path.contains("generated/") }
        include("**/kotlin/**")
    }
}

application {
    mainClassName = "io.ktor.server.cio.EngineMain"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    google()
}

dependencies {
    implementation("io.ktor:ktor-server-cio:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("io.ktor:ktor-locations:$ktor_version")
    implementation("com.squareup.sqldelight:sqlite-driver:1.4.2")
    implementation("org.koin:koin-ktor:2.1.6")
    implementation("com.cloudinary:cloudinary-http44:1.26.0")
    implementation("com.stripe:stripe-java:19.45.0")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("com.squareup.sqldelight:sqlite-driver:1.4.0")
    testImplementation("org.koin:koin-test:2.1.6")
    testImplementation("io.mockk:mockk:1.10.0")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotest_version")
    testImplementation("io.kotest:kotest-property-jvm:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-ktor-jvm:$kotest_version")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xallow-result-return-type", "-Xinline-classes")
            jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }
    compileTestKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xallow-result-return-type", "-Xinline-classes")
            jvmTarget = JavaVersion.VERSION_1_8.toString()
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
