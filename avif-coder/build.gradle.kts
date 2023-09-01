@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}


android {
    namespace = "com.github.awxkee.avifcoder"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        externalNativeBuild {
            cmake {
                cppFlags(
                    "-Wl,--build-id=none",
                    "-Wl,--gc-sections",
                    "-ffunction-sections",
                    "-fdata-sections",
                    "-fvisibility=hidden"
                )
                cFlags(
                    "-ffunction-sections",
                    "-fdata-sections",
                    "-fvisibility=hidden",
                    "-Wl,--build-id=none",
                    "-Wl,--gc-sections",
                )
                abiFilters += setOf("armeabi-v7a", "arm64-v8a", "x86_64")
            }
        }

    }

    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    externalNativeBuild {
        cmake { path("src/main/cpp/CMakeLists.txt") }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}
dependencies {
    implementation("androidx.annotation:annotation-jvm:1.6.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
}
