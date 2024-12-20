// build.gradle.kts (App-level)
plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
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
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    // Conector para SqlServer
    implementation(libs.sourceforge.jtds)
    // libreria Pcs (utilidades)
    implementation ("com.google.firebase:firebase-auth:21.5.0")
    implementation ("com.google.firebase:firebase-database:20.3.3") // o Firestore
    implementation ("com.google.firebase:firebase-database:20.0.5")
    implementation ("com.google.firebase:firebase-auth:21.0.5")
    implementation ("com.google.firebase:firebase-auth:21.0.0")
    implementation ("com.google.firebase:firebase-analytics:21.0.0")
    implementation ("com.google.firebase:firebase-firestore:24.0.0")
    implementation(libs.libreria.pcs)
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.play.services.maps)
    implementation(libs.androidx.cardview)
    implementation("com.google.firebase:firebase-firestore-ktx:21.6.0")
    implementation(libs.firebase.auth.ktx) // Corrección aquí
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    implementation("org.osmdroid:osmdroid-android:6.1.12") // test
    implementation("com.google.android.gms:play-services-location:21.3.0") // test
    implementation("com.android.volley:volley:1.2.1")
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.material.v121)
    implementation(libs.okhttp)
    implementation(libs.json)
    implementation(libs.mpandroidchart)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}
