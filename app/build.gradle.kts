plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // using KAPT for Room compiler
}

android {
    namespace = "com.example.taskmanager"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.taskmanager"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Room schema JSONs available to androidTest
    sourceSets.getByName("androidTest") {
        assets.srcDir(file("schemas"))
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
    kotlinOptions { jvmTarget = "11" }
    buildFeatures { compose = true }
}

kapt {
    correctErrorTypes = true
    arguments {
        // export Room schemas to app/schemas
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
        // (expandProjection not needed on modern Room; remove to avoid warnings)
    }
}

dependencies {
    // --- Core / Compose ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // LiveData in Compose (for observeAsState)
    implementation("androidx.compose.runtime:runtime-livedata")
    // Flow collection with lifecycle awareness (for collectAsStateWithLifecycle)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
    // viewModel() in Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    // --- Room (KAPT) ---
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.material3)
    kapt(libs.room.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // DataStore (for step 2.1 examples)
    implementation(libs.androidx.datastore.preferences)

    // --- Test / Debug ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
