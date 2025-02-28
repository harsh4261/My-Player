plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // hilt plugin
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")

    // Serialization plugin
    kotlin("plugin.serialization") version "2.1.10"
}

android {
    namespace = "com.example.videoplayer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.videoplayer"
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt dependencies
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    //hilt navigation-compose
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Icon extension dependency
    implementation("androidx.compose.material:material-icons-extended-android:1.7.8")

    // NavGraph dependency
    implementation("androidx.navigation:navigation-compose:2.8.7")
    
    // Serialization dependency
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // runtime permission dependency
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    // adaptive navigation dependency
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
//    implementation("androidx.compose.material3:material3-adaptive-navigation-suite") // This is amazing


    // Exo-Player dependency
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation("androidx.media3:media3-ui:1.5.1")
    implementation("androidx.media3:media3-common:1.5.1")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.material:material:1.7.8")
    implementation("androidx.compose.ui:ui-tooling:1.7.8")
    // ExoPlayer
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // Coil
    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")


}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}