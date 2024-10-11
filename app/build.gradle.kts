/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    var isFoss = false

    namespace = "ru.tech.imageresizershrinker"
    compileSdk = libs.versions.androidCompileSdk.get().toIntOrNull()

    defaultConfig {
        vectorDrawables.useSupportLibrary = true
        
        applicationId = "ru.tech.imageresizershrinker"
        minSdk = libs.versions.androidMinSdk.get().toIntOrNull()
        targetSdk = libs.versions.androidTargetSdk.get().toIntOrNull()
        versionCode = libs.versions.versionCode.get().toIntOrNull()
        versionName = System.getenv("VERSION_NAME") ?: libs.versions.versionName.get()

        archivesName.set("image-toolbox-$versionName${if (isFoss) "-foss" else ""}")
    }

    androidResources {
        generateLocaleConfig = true
    }

    flavorDimensions += "app"

    productFlavors {
        create("foss") {
            dimension = "app"
            isFoss = true
            extra.set("gmsEnabled", false)
        }
        create("market") {
            dimension = "app"
            extra.set("gmsEnabled", true)
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    val javaVersion = JavaVersion.toVersion(libs.versions.jvmTarget.get())

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    buildFeatures {
        compose = true
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86_64")
            isUniversalApk = true
        }
    }
    packaging {
        jniLibs {
            pickFirsts.add("lib/*/libcoder.so")
        }
        resources {
            excludes += "META-INF/"
            excludes += "kotlin/"
            excludes += "org/"
            excludes += ".properties"
            excludes += ".bin"
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugaring)

    implementation(libs.hilt)
    kapt(libs.dagger.hilt.compiler)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.window.sizeclass)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material)

    implementation(projects.core.domain)
    implementation(projects.core.ui)
    implementation(projects.core.data)
    implementation(projects.core.resources)
    implementation(projects.core.settings)
    implementation(projects.core.filters)
    implementation(projects.core.crash)

    implementation(projects.feature.root)
    implementation(projects.feature.mediaPicker)
    implementation(projects.feature.quickTiles)
}


afterEvaluate {
    android.productFlavors.forEach { flavor ->
        tasks.matching { task ->
            listOf("GoogleServices", "Crashlytics").any {
                task.name.contains(it)
            }.and(
                task.name.contains(
                    flavor.name.replaceFirstChar(Char::uppercase)
                )
            )
        }.forEach { task ->
            task.enabled = flavor.extra.get("gmsEnabled") == true
        }
    }
}
