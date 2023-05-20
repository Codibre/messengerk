plugins {
    id("com.github.codibre.common-conventions")
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    // messengerk
    implementation(project(":kafka-transport"))

    // spring
    implementation(libs.spring.context)
    implementation(libs.spring.autoconfigure)
}
