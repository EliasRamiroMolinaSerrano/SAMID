pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()

        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "SAMID"
include(":app")