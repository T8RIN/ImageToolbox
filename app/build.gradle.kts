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

var isFoss = false

plugins {
    alias(libs.plugins.image.toolbox.application)
    alias(libs.plugins.image.toolbox.hilt)
}

android {
    val supportedAbi = arrayOf("armeabi-v7a", "arm64-v8a", "x86_64")

    namespace = "com.t8rin.imagetoolbox"

    defaultConfig {
        vectorDrawables.useSupportLibrary = true

        //Maintained for compatibility with old version
        applicationId = "ru.tech.imageresizershrinker"

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
            resValue("string", "file_provider", "com.t8rin.imagetoolbox.fileprovider.debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_launcher_name", "Image Toolbox")
            resValue("string", "file_provider", "com.t8rin.imagetoolbox.fileprovider")

            dependencySubstitution {
                substitute(
                    dependency = "org.opencv:opencv:4.11.0",
                    using = "org.opencv:opencv:4.12.0"
                )
            }
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
            // Detect app bundle and conditionally disable split abis
            // This is needed due to a "Sequence contains more than one matching element" error
            // present since AGP 8.9.0, for more info see:
            // https://issuetracker.google.com/issues/402800800

            // AppBundle tasks usually contain "bundle" in their name
            //noinspection WrongGradleMethod
            val isBuildingBundle = gradle.startParameter.taskNames.any { it.lowercase().contains("bundle") }

            // Disable split abis when building appBundle
            isEnable = !isBuildingBundle
            reset()
            //noinspection ChromeOsAbiSupport
            include(*supportedAbi)
            isUniversalApk = true
        }
    }
    packaging {
        jniLibs {
            pickFirsts.add("lib/*/libcoder.so")
            pickFirsts.add("**/libc++_shared.so")
            useLegacyPackaging = true
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
        export.excludeFields.addAll("generated")
    }

    buildFeatures {
        resValues = true
    }
}

dependencies {
    implementation(projects.feature.root)
    implementation(projects.feature.mediaPicker)
    implementation(projects.feature.quickTiles)

    implementation(libs.toolbox.opencvTools)

    implementation(libs.bouncycastle.pkix)
    implementation(libs.bouncycastle.provider)

    "marketImplementation"(libs.quickie.bundled)
    "fossImplementation"(libs.quickie.foss)
}

dependencySubstitution {
    substitute(
        dependency = "com.caverock:androidsvg-aar:1.4",
        using = "com.github.deckerst:androidsvg:cc9d59a88f"
    )
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

fun Project.dependencySubstitution(action: DependencySubstitutions.() -> Unit) {
    allprojects {
        configurations.all {
            resolutionStrategy.dependencySubstitution(action)
        }
    }
}

fun DependencySubstitutions.substitute(
    dependency: String,
    using: String
) = substitute(module(dependency)).using(module(using))