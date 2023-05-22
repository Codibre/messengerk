plugins {
    id("com.github.codibre.common-conventions")
    kotlin("plugin.serialization") version "1.8.21"
}

dependencies {
    // messengerk
    implementation(project(":core"))

    // kafka-clients
    implementation(libs.kafka.clients)
}
