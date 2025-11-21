plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.autoflow.obd.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    api(libs.androidx.core.ktx)
    api(libs.coroutines.core)
    api(libs.coroutines.android)
    api(libs.serialization.json)
    api(libs.kotlinx.datetime)

    implementation(libs.timber)
    implementation(libs.play.services.location)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.work.runtime)
    implementation(libs.hilt.android)

    kapt(libs.hilt.compiler)

    testImplementation(libs.junit)
}
