rootProject.name = "wirer"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven {
            name = "MavenPublic"
            url = uri("https://repo.lylaw.fr/repository/maven-public/")
        }
    }
}