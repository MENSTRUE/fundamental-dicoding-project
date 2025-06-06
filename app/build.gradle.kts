plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}

android {

    buildFeatures {
        viewBinding = true
    }

    namespace = "com.android.dicodingeventapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.android.dicodingeventapp"
        minSdk = 24
        targetSdk = 35
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

    kapt {
        correctErrorTypes = true
    }

}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // HTTP & Networking
    implementation(libs.android.async.http)
    implementation(libs.squareup.retrofit)
    implementation(libs.converter.gson)

    // Dependency Injection (Hilt)
    implementation(libs.hilt.android)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.datastore.preferences.core.android)
    implementation(libs.androidx.espresso.core)
    kapt(libs.hilt.android.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //glide
//    implementation (libs.github.glide)
//    kapt (libs.compiler)
    implementation (libs.github.glide)
    annotationProcessor (libs.compiler)

    implementation (libs.circleimageview)

    implementation (libs.shimmer)

    // teori unit test
    testImplementation (libs.junit)

    implementation (libs.material.v1110)

    // WorkManager - KTX (Kotlin Extensions)
    implementation (libs.androidx.work.runtime.ktx)

    implementation (libs.androidx.datastore.preferences)

    // Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx) // opsional untuk coroutine dan LiveData support



}

