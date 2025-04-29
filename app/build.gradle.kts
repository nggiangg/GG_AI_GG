plugins {

    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.gg_ai_gg"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gg_ai_gg"
        minSdk = 33
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
}

dependencies {
    // PDF processing libraries
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")
    // For file operations
    implementation("androidx.documentfile:documentfile:1.0.1")
    // Gemini API
    implementation("com.google.ai.client.generativeai:generativeai:0.2.2")

    // Guava dependencies for Futures API
    implementation("com.google.guava:guava:32.1.2-android")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}