plugins {
    id("java")
    id("idea")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "fr.ladder"
version = "1.0.0"

tasks.compileJava {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    maven {
        name = "maven-releases"
        url = uri("https://repo.lylaw.fr/repository/maven-releases/")
        credentials {
            username = findProperty("NEXUS_USER") as String?
            password = findProperty("NEXUS_PASSWORD") as String?
        }
    }
}

dependencies {
    implementation("org.jetbrains", "annotations", "24.0.1")
    implementation("io.github.classgraph", "classgraph", "4.8.181")
    implementation("org.slf4j", "slf4j-simple", "1.6.1")

    compileOnly("fr.snowtyy", "papermc", "1.8.8")
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}