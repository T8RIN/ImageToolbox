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

import dagger.hilt.android.plugin.util.capitalize
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.util.Locale

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
        applicationId = "ru.tech.imageresizershrinker"
        minSdk = libs.versions.androidMinSdk.get().toIntOrNull()
        targetSdk = libs.versions.androidTargetSdk.get().toIntOrNull()
        versionCode = libs.versions.versionCode.get().toIntOrNull()
        versionName = libs.versions.versionName.get()

        resourceConfigurations += setOf(
            "en",
            "af",
            "ar",
            "be",
            "bn",
            "cs",
            "da",
            "de",
            "es",
            "eu",
            "fa",
            "fil",
            "fr",
            "hi",
            "hu",
            "ia",
            "in",
            "it",
            "iw",
            "ja",
            "kk",
            "kn",
            "ko",
            "nl",
            "pl",
            "pt",
            "pt-rBR",
            "ro",
            "ru",
            "sk",
            "sr",
            "te",
            "th",
            "tr",
            "uk",
            "vi",
            "zh-rCN",
            "zh-rTW"
        )
        archivesName.set("image-toolbox-$versionName${if (isFoss) "-foss" else ""}")
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
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        isCoreLibraryDesugaringEnabled = true
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

    packaging {
        resources {
            excludes += setOf(
                "META-INF/*.kotlin_module",
                "kotlin/*.kotlin_builtins",
                "kotlin/**/*.kotlin_builtins",
                "CERT.SF",
                "publicsuffixes.gz"
            )
        }
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
    }

    lint {
        disable += "UsingMaterialAndMaterial3Libraries"
        disable += "ModifierParameter"
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugaring)

    //Di
    implementation(libs.hilt)
    kapt(libs.dagger.hilt.compiler)

    //Compose
    api(libs.androidx.material3)
    api(libs.androidx.material3.window.sizeclass)
    api(libs.androidx.material.icons.extended)
    api(libs.androidx.material)

    implementation(projects.core.domain)
    implementation(projects.core.ui)
    implementation(projects.core.data)
    implementation(projects.core.resources)
    implementation(projects.core.settings)

    implementation(projects.feature.main)

    "marketImplementation"(libs.firebase.crashlytics.ktx)
    "marketImplementation"(libs.firebase.analytics.ktx)
}


afterEvaluate {
    android.productFlavors.forEach { flavor ->
        tasks.matching {
            (it.name.contains("GoogleServices") || it.name.contains("Crashlytics")) && it.name.contains(
                flavor.name.capitalize(Locale.getDefault())
            )
        }.forEach {
            it.enabled = flavor.extra.get("gmsEnabled") == true
        }
    }
}