// File: settings.gradle.kts
// Project settings and repository configuration

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()      // Google Maven repository
        mavenCentral() // Central Maven repository
    }
}

rootProject.name = "Medical File Processor"
include(":app")