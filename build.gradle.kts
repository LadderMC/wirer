plugins {
    id("java")
    id("idea")
    id("fr.ladder.releasr") version "1.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "fr.ladder"

repositories {
    mavenCentral()
    maven {
        name = "maven-public"
        url = uri("https://repo.lylaw.fr/repository/maven-public/")
    }
}

dependencies {
    implementation("org.jetbrains", "annotations", "24.0.1")
    implementation("org.slf4j", "slf4j-simple", "1.6.1")

    implementation("fr.ladder", "reflex", "1.0.1-83cbd75")
    compileOnly("fr.snowtyy", "papermc", "1.8.8")
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}