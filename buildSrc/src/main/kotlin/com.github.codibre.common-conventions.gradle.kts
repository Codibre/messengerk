import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("maven-publish")
    id("org.jetbrains.dokka")
}

val libs = the<LibrariesForLibs>()
version = libs.versions.application.get()
group = libs.versions.group.get()

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    gradlePluginPortal()
}

dependencies {
    // kotlin
    implementation(libs.kotlinx.coroutines.core.get())

    // testing
    testImplementation(libs.junit.jupiter.get())
    testImplementation(libs.kotest.runner.junit5.get())
    testImplementation(libs.kotest.assertions.core.get())
    testImplementation(libs.kotest.property.get())
    testImplementation(libs.mockito.core.get())
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            group = group
            from(components["kotlin"])
        }
    }
}
