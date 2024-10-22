// build.gradle.kts (App-level)
plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.samid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.samid"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    implementation ("org.osmdroid:osmdroid-android:6.1.12") //test
    implementation ("com.google.android.gms:play-services-location:21.3.0") //test



    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.material.v121)
    implementation(libs.okhttp)
    implementation(libs.json)
    implementation(libs.mpandroidchart)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

}
