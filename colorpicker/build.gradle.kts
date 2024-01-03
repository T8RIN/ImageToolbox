plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = libs.versions.androidCompileSdk.get().toIntOrNull()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toIntOrNull()
    }

    compileOptions {
        sourceCompatibility = rootProject.extra.get("javaCompile") as JavaVersion
        targetCompatibility = rootProject.extra.get("javaCompile") as JavaVersion
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    buildFeatures {
        compose = true
    }
    namespace = "com.smarttoolfactory.colorpicker"
}

dependencies {
    implementation(libs.androidxCore)

    implementation(project(":gesture"))
    implementation(project(":screenshot"))

    implementation(libs.compose.extended.colors)
    implementation(libs.compose.color.detector)
    implementation(libs.compose.colorful.sliders)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.runtime)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)
    implementation(libs.compose.material.iconsExtended)
}