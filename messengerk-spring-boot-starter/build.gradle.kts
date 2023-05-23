plugins {
    id("com.github.codibre.common-conventions")
    alias(libs.plugins.spring.dependency.management)
    kotlin("kapt")
}

dependencies {
    // messengerk
    api(project(":core"))

    // spring
    implementation(libs.spring.context)
    implementation(libs.snake.yaml)
    implementation(libs.spring.starter.validation)
    kapt(libs.spring.configuration.processor)

    // javax
    implementation(libs.javax.annotation.api)

    // testing
    testImplementation(libs.spring.starter.test)
    testImplementation(libs.json.smart)
}
