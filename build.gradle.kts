import org.jetbrains.kotlin.config.LanguageFeature

buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath(Libs.SqlDelight)
    }
}

plugins {
    application
    kotlin(Plugins.Kotlin) version Versions.Kotlin
    id(Plugins.SqlDelight) version Versions.SqlDelight
    id(Plugins.KtLint) version Versions.KtLintGradle
}

sqldelight {
    database(Database.Name) {
        packageName = Database.Package
    }
}

group = App.Group
version = App.Version

ktlint {
    version.set(Versions.KtLint)
    coloredOutput.set(true)
    disabledRules.set(mutableListOf("no-wildcard-imports"))
    filter {
        exclude("**/generated/**")
        exclude { element -> element.file.path.contains("generated/") }
        include("**/kotlin/**")
    }
}

application {
    mainClassName = App.MainClassName
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    google()
}

dependencies {
    implementation(Libs.Ktor.Engine)
    implementation(Libs.Ktor.LogBack)
    implementation(Libs.Ktor.Core)
    implementation(Libs.Ktor.Host)
    implementation(Libs.Ktor.Auth)
    implementation(Libs.Ktor.AuthJwt)
    implementation(Libs.Ktor.Jackson)
    implementation(Libs.Ktor.Locations)

    implementation(Libs.SqlDelightDriver)
    implementation(Libs.Koin)
    implementation(Libs.Cloudinary)
    implementation(Libs.Stripe)

    testImplementation(Libs.Test.Ktor)
    testImplementation(Libs.Test.SqlDelightDriver)
    testImplementation(Libs.Test.Koin)
    testImplementation(Libs.Test.Mockk)

    testImplementation(Libs.Test.Kotest.Runner)
    testImplementation(Libs.Test.Kotest.Property)
    testImplementation(Libs.Test.Kotest.Assertions)
    testImplementation(Libs.Test.Kotest.KtorAssertions)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    target {
        compilations.configureEach {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_1_8.toString()
            }
        }
    }
    sourceSets {
        all {
            languageSettings.enableLanguageFeature(LanguageFeature.AllowResultInReturnType.toString())
            languageSettings.enableLanguageFeature(LanguageFeature.InlineClasses.toString())
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
