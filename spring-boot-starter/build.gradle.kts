plugins {
    id("com.github.codibre.common-conventions")
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    // messengerk
    api(project(":core"))
    implementation(project(":kafka-transport"))

    // spring
    implementation(libs.spring.context)
    implementation(libs.snake.yaml)
    kapt(libs.spring.configuration.processor)
    implementation(libs.spring.starter.validation)

    // javax
    implementation(libs.javax.annotation.api)

    // testing
    testImplementation(libs.spring.starter.test)
    testImplementation(libs.json.smart)
}
