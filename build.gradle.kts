plugins {
    id("java")
    id("idea")
    id("maven-publish")
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
            username = findProperty("NEXUS_USER") as String? ?: System.getenv("nexusUser")
            password = findProperty("NEXUS_PASSWORD") as String? ?: System.getenv("nexusPassword")
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

publishing {
    repositories {
        maven {
            name = "maven-releases"
            url = uri("https://repo.lylaw.fr/repository/maven-releases/")
            credentials {
                username = findProperty("NEXUS_USER") as String? ?: System.getenv("nexusUser")
                password = findProperty("NEXUS_PASSWORD") as String? ?: System.getenv("nexusPassword")
            }
        }
    }

    publications {
        val refType = System.getenv("refType") ?: ""
        when (refType) {
            "branch" -> {
                // create commit package
                val refName = (System.getenv("refName") ?: "").replace("/", "-")
                val commitHash = (System.getenv("commitHash") ?: "").take(7);

                val classifier = if(refName.isNotEmpty() && commitHash.isNotEmpty()) "-$refName-$commitHash" else ""
                if(classifier.isNotEmpty()) {
                    // create a publication with the classifier
                    create<MavenPublication>("maven") {
                        groupId = project.group.toString()
                        artifactId = "dependency-injection"
                        version = project.version.toString() + classifier

                        from(components["java"])
                    }
                }
            }
            "tag" -> {
                // create a publication with the classifier
                create<MavenPublication>("maven") {
                    groupId = project.group.toString()
                    artifactId = "dependency-injection"
                    version = project.version.toString()

                    from(components["java"])
                }
            }
            else -> println("No publication created because refType is not branch or tag")
        }
    }
}