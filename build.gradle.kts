plugins {
    kotlin("jvm") version "2.1.0"
    `maven-publish`
    java
}

group = "adam.backend.portfolio"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "chess-engine"
        }
    }
    repositories {
        mavenLocal()
    }
}

repositories {
    mavenLocal()
    // Or a remote repository if needed
}

kotlin {
    jvmToolchain(21)
}