plugins {
    id("java")
    id("idea")
    id("fr.ladder.releasr") version "0.1.0"
    id("com.gradleup.shadow") version "9.3.2"
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

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

releasr {
    url = "https://repo.lylaw.fr/repository/maven-releases/"
    username = findProperty("REPO_USER") as String? ?: System.getenv("repoUser")
    password = findProperty("REPO_PASSWORD") as String? ?: System.getenv("repoPassword")
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()
}
