plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.autoflow.obd.ui"
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    api(projects.core)

    implementation(platform(libs.compose.bom))
    api(libs.compose.ui)
    api(libs.compose.material3)
    api(libs.compose.foundation)
    api(libs.compose.runtime)
    api(libs.compose.preview)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    debugApi(libs.compose.tooling)
}
