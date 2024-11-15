// build.gradle.kts (Project-level)
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false // Ensure you have Kotlin plugin
    kotlin("jvm") version "1.8.0" apply false
    kotlin("kapt") version "1.8.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}