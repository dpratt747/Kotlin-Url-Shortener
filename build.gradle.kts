plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

private val arrowVersion = "2.1.0"
private val http4kVersion = "6.9.1.0"

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-api-openapi:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson:$http4kVersion")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("Application")
}