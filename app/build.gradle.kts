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

plugins {
    alias(libs.plugins.image.toolbox.application)
    alias(libs.plugins.image.toolbox.hilt)
}

android {
    var isFoss = false

    val supportedAbi = arrayOf("armeabi-v7a", "arm64-v8a", "x86_64")

    namespace = "ru.tech.imageresizershrinker"

    defaultConfig {
        vectorDrawables.useSupportLibrary = true

        applicationId = "ru.tech.imageresizershrinker.predictive_back_repro"
        versionCode = libs.versions.versionCode.get().toIntOrNull()
        versionName = System.getenv("VERSION_NAME") ?: libs.versions.versionName.get()

        ndk {
            abiFilters.clear()
            //noinspection ChromeOsAbiSupport
            abiFilters += supportedAbi.toSet()
        }

        setProperty("archivesBaseName", "image-toolbox-$versionName${if (isFoss) "-foss" else ""}")
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
        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_launcher_name", "Image Toolbox DEBUG")
            resValue("string", "file_provider", "ru.tech.imageresizershrinker.fileprovider.debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_launcher_name", "Image Toolbox")
            resValue("string", "file_provider", "ru.tech.imageresizershrinker.fileprovider")
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            //noinspection ChromeOsAbiSupport
            include(*supportedAbi)
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
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }

    aboutLibraries {
        excludeFields = arrayOf("generated")
    }

    buildFeatures {
        resValues = true
    }
}

dependencies {
    // Navigation
    api(libs.decompose)
    api(libs.decomposeExtensions)

    //AndroidX
    api(libs.activityCompose)
    api(libs.splashScreen)
    api(libs.androidx.exifinterface)
    api(libs.appCompat)
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.documentfile)

    //Konfetti
    api(libs.konfetti.compose)

    //Coil
    api(libs.coil)
    api(libs.coilCompose)
    api(libs.coilGif)
    api(libs.coilSvg)
    api(libs.coilNetwork)
    api(libs.ktor)

    //Modules
    api(libs.toolbox.uCrop)
    api(libs.toolbox.cropper)
    api(libs.toolbox.dynamicTheme)
    api(libs.toolbox.colordetector)
    api(libs.toolbox.gesture)
    api(libs.toolbox.beforeafter)
    api(libs.toolbox.image)
    api(libs.toolbox.screenshot)
    api(libs.toolbox.modalsheet)
    api(libs.toolbox.colorpicker)
    api(libs.toolbox.systemuicontroller)
    api(libs.toolbox.placeholder)
    api(libs.toolbox.logger)
    api(libs.toolbox.zoomable)
    api(libs.toolbox.snowfall)
    api(libs.toolbox.extendedcolors)
    api(libs.toolbox.histogram)

    api(libs.reorderable)

    api(libs.shadowGadgets)
    api(libs.shadowsPlus)

    api(libs.kotlinx.collections.immutable)

    api(libs.fadingEdges)
    api(libs.scrollbar)

    implementation(libs.datastore.preferences.android)
    api(libs.material)

    "marketImplementation"(libs.firebase.crashlytics.ktx)
    "marketImplementation"(libs.firebase.analytics.ktx)
    "marketImplementation"(libs.review.ktx)
    "marketImplementation"(libs.app.update)
    "marketImplementation"(libs.app.update.ktx)

    "marketImplementation"(libs.mlkit.document.scanner)
    "fossImplementation"(libs.documentscanner)

    "marketImplementation"(libs.quickie.bundled)
    "fossImplementation"(libs.quickie.foss)
    implementation(libs.zxing.android.embedded)
    implementation(libs.jsoup)

    api(libs.capturable)
    api(libs.evaluator)
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