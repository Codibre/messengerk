plugins {
    id("com.github.codibre.common-conventions")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    // serialization
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlinx.serialization.json)
}
