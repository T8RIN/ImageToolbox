plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        disable += "UsingMaterialAndMaterial3Libraries"
        disable += "ModifierParameter"
    }
    namespace = "com.smarttoolfactory.colorpicker"
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.core:core-ktx:1.12.0")

    implementation(project(":gesture"))
    implementation("com.github.SmartToolFactory:Compose-Extended-Colors:1.0.0-alpha07")
    implementation(project(":screenshot"))
    implementation("com.github.SmartToolFactory:Compose-Color-Detector:1.0.0")
    implementation("com.github.SmartToolFactory:Compose-Colorful-Sliders:1.2.0")
    // Jetpack Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3")
}