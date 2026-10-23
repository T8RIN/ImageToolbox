/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

import com.android.build.api.dsl.LibraryExtension
import com.t8rin.imagetoolbox.CompressReleaseResourcesTask
import com.t8rin.imagetoolbox.GenerateHelpResourcesTask

plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.compose)
}

val helpResourcesDirectory = layout.buildDirectory.dir("generated/helpResources/res")
val generateHelpResources = tasks.register<GenerateHelpResourcesTask>("generateHelpResources") {
    description = "Generates localized help resources from Weblate strings."
    sourceDirectory.set(layout.projectDirectory.dir("src/main/res"))
    outputDirectory.set(helpResourcesDirectory)
}

androidComponents.onVariants(androidComponents.selector().withBuildType("release")) { variant ->
    val compressResources = tasks.register<CompressReleaseResourcesTask>(
        "compress${variant.name.replaceFirstChar(Char::uppercase)}Resources"
    ) {
        description = "Compresses help texts and the PDF font for ${variant.name}."
        sourceDirectory.set(generateHelpResources.flatMap { it.outputDirectory })
        outputDirectory.set(layout.buildDirectory.dir("generated/compressedResources/${variant.name}"))
    }
    variant.sources.res?.addGeneratedSourceDirectory(
        compressResources,
        CompressReleaseResourcesTask::outputDirectory
    )
}

extensions.configure<LibraryExtension> {
    namespace = "com.t8rin.imagetoolbox.core.resources"

    defaultConfig.vectorDrawables.useSupportLibrary = true

    // Keep strings.xml as the Weblate source and package generated resources instead.
    sourceSets.named("main") {
        res.directories.apply {
            clear()
            add(helpResourcesDirectory.get().asFile.path)
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("release") {
            buildConfigField("String", "VERSION_NAME", "\"${libs.versions.versionName.get()}\"")
            buildConfigField("int", "VERSION_CODE", libs.versions.versionCode.get())
        }
        getByName("debug") {
            buildConfigField("String", "VERSION_NAME", "\"${libs.versions.versionName.get()}\"")
            buildConfigField("int", "VERSION_CODE", libs.versions.versionCode.get())
        }
    }
}

tasks.named("preBuild") {
    dependsOn(generateHelpResources)
}

dependencies {
    implementation(libs.xz)
    implementation(libs.material)
    implementation(libs.androidxCore)
    implementation(libs.appCompat)
    implementation(libs.splashScreen)
    implementation(libs.kotlinx.collections.immutable)
}