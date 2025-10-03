plugins {
    id("java")
    id("idea")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

/**
 * Get the latest tag from the environment variable set in the CI/CD pipeline.
 * The tag is expected to be in the format "vX.Y.Z".
 * The commit hash is also retrieved from the environment variable and truncated to 7 characters.
 */
val latestTag: String? = System.getenv("latestTag")?.replace("v", "")

/**
 * Get the commit hash from the environment variable set in the CI/CD pipeline.
 * The commit hash is truncated to 7 characters.
 */
val commitHash: String? = System.getenv("commitHash")?.take(7);

/**
 * Constructs a version string in the format "vX.Y.Z" based on the latest tag and an incremented patch number.
 */
fun getVersion(): String? {
    val numbers: List<String>? = latestTag?.split('.')
    val builder = StringBuilder("v");
    if(numbers == null) {
        return null;
    }

    for (i in 0 until numbers.size) {
        if(i < numbers.size - 1) {
            builder
                .append(numbers[i])
                .append(".")
        } else {
            try {
                // increment the last number (patch version)
                builder
                    .append(numbers[i].toInt() + 1)
                    .append(commitHash ?.let { "-$it" } ?: "")
            } catch (ex: NumberFormatException) {
                println("Error parsing version number: ${ex.message}")
                throw ex;
            }
        }
    }
    return builder.toString()
}

group = "fr.ladder"
version = getVersion() ?: "local"

tasks.compileJava {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    maven {
        name = "maven-releases"
        url = uri("https://repo.lylaw.fr/repository/maven-releases/")
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

        val githubUser = System.getenv("githubUser")
        val githubPassword = System.getenv("githubPassword")
        val githubRepository = System.getenv("githubRepository")

        if(githubUser != null) {
            println("- register 'GitHubPackages' repository.")
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/$githubRepository")
                credentials {
                    username = githubUser
                    password = githubPassword
                }
            }
        } else {
            println("- 'githubUser' is null.")
        }

        val nexusUser = findProperty("NEXUS_USER") as String? ?: System.getenv("nexusUser")
        val nexusPassword = findProperty("NEXUS_PASSWORD") as String? ?: System.getenv("nexusPassword")

        if(nexusUser != null) {
            println("- register 'MavenReleases' repository.")
            maven {
                name = "MavenReleases"
                url = uri("https://repo.lylaw.fr/repository/maven-releases/")
                credentials {
                    username = nexusUser
                    password = nexusPassword
                }
            }
        } else {
            println("- 'nexusUser' is null.")
        }
    }

    publications {
        val refType = System.getenv("refType") ?: ""
        when (refType) {
            "branch" -> {
                // create commit package on push to branch main
                if(version.toString().startsWith("v")) {
                    create<MavenPublication>("maven") {
                        groupId = project.group.toString()
                        artifactId = "wirer"
                        version = version.toString()

                        from(components["java"])
                    }
                }
            }
            "tag" -> {
                val refName = (System.getenv("refName") ?: "")
                    .replace("v", "")
                    .replace("/", "-")
                // create a publication with the classifier
                create<MavenPublication>("maven") {
                    groupId = project.group.toString()
                    artifactId = "wirer"
                    version = refName

                    from(components["java"])
                }
            }
            else -> println("No publication created because refType is not branch or tag")
        }
    }
}