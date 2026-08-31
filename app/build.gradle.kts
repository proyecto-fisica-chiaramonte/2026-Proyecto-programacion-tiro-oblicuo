plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.openjfx.javafxplugin") version "0.1.0"
    application
}

repositories {
    mavenCentral()
}

javafx {
    version = "21.0.1"
    modules("javafx.controls", "javafx.fxml")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass.set("com.example.juegofisica.AppKt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}