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

@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.plugin.compose")
}

val testedProjects = listOf(
    "core/data",
    "core/games",
    "core/ui",
    "core/utils",
    "feature/ai-tools",
    "feature/assistant",
    "feature/audio-cover-extractor",
    "feature/batch-rename",
    "feature/apng-tools",
    "feature/cipher",
    "feature/checksum-tools",
    "feature/code-preview",
    "feature/compare",
    "feature/compression-lab",
    "feature/draw",
    "feature/duplicate-finder",
    "feature/edit-exif",
    "feature/emoji-kitchen",
    "feature/filters",
    "feature/fractal-generation",
    "feature/gradient-maker",
    "feature/gif-tools",
    "feature/load-net-image",
    "feature/image-cutting",
    "feature/image-splitting",
    "feature/image-stitch",
    "feature/image-stacking",
    "feature/jxl-tools",
    "feature/markup-layers",
    "feature/media-picker",
    "feature/multi-frame-fusion",
    "feature/pdf-tools",
    "feature/screenshot-framing",
    "feature/svg-maker",
    "feature/texture-generation",
    "feature/webp-tools",
    "feature/weight-resize",
    "lib/archive",
    "lib/collages",
    "lib/colors",
    "lib/curves",
    "lib/documentscanner",
    "lib/opencv-tools"
)

val testSourceSets = mapOf(
    "main" to "androidTest",
    "foss" to "androidTestFoss",
    "market" to "androidTestMarket"
)
val testSources = testSourceSets.mapValues { (_, sourceSet) ->
    testedProjects.flatMap { projectPath ->
        listOf("java", "kotlin").map { directory ->
            rootProject.file("$projectPath/src/$sourceSet/$directory")
        }
    }
}

android {
    namespace = "com.t8rin.imagetoolbox.instrumentation"
    compileSdk = libs.versions.androidCompileSdk.get().toIntOrNull()
    compileSdkMinor = libs.versions.androidCompileSdkMinor.get().toIntOrNull()
    compileSdkExtension = libs.versions.androidCompileSdkExtension.get().toIntOrNull()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toIntOrNull()
        targetSdk = libs.versions.androidTargetSdk.get().toIntOrNull()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        isCoreLibraryDesugaringEnabled = true
    }

    kotlin.compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get()))
        freeCompilerArgs.add("-Xdont-warn-on-error-suppression")
    }

    flavorDimensions += "app"
    productFlavors {
        create("foss") { dimension = "app" }
        create("market") { dimension = "app" }
    }

    testSourceSets.forEach { (name, sourceSet) ->
        sourceSets.named(name) {
            assets.directories.addAll(
                testedProjects.map { projectPath ->
                    rootProject.file("$projectPath/src/$sourceSet/assets").absolutePath
                }
            )
        }
    }

    targetProjectPath = ":app"
}

androidComponents.onVariants { variant ->
    val compileTaskName = "compile${variant.name.replaceFirstChar(Char::uppercaseChar)}Kotlin"
    val sources =
        testSources.getValue("main") + testSources.getValue(variant.productFlavors.single().second)
    // Keep the source roots owned by their original androidTest modules in Android Studio.
    tasks.withType<KotlinCompile>().matching { it.name == compileTaskName }.configureEach {
        source(sources)
    }
}

evaluationDependsOn(":app")
testedProjects.forEach { projectPath ->
    evaluationDependsOn(":${projectPath.replace('/', ':')}")
}
val testedProjectDependencies = testedProjects.flatMap { projectPath ->
    project(":${projectPath.replace('/', ':')}")
        .configurations
        .getByName("fossDebugRuntimeClasspath")
        .allDependencies
        .filterIsInstance<ProjectDependency>()
        .map(ProjectDependency::getPath)
}.distinct()
configurations.configureEach {
    if (name.endsWith("RuntimeClasspath")) {
        exclude(group = "androidx.lifecycle", module = "lifecycle-common")
    }

    val variantName = listOf("fossDebug", "marketDebug")
        .firstOrNull { name == "${it}CompileOnly" }
    if (variantName != null) {
        testedProjects.forEach { projectPath ->
            project(":${projectPath.replace('/', ':')}")
                .configurations
                .getByName("${variantName}RuntimeClasspath")
                .allDependencies
                .forEach { dependency ->
                    if (dependency !is ProjectDependency) {
                        dependencies.add(dependency.copy())
                    }
                }
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugaring)

    testedProjects.forEach { projectPath ->
        compileOnly(project(":${projectPath.replace('/', ':')}"))
    }
    testedProjectDependencies.forEach { projectPath ->
        compileOnly(project(projectPath))
    }

    implementation(libs.junit)
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.runner)
    implementation(libs.compose.ui.test.junit4)
    implementation(libs.moshi)
    implementation(libs.bouncycastle.provider)
    compileOnly(libs.onnx.genai)
    compileOnly(libs.kotlinx.serialization.json)

    implementation(testFixtures(projects.core.data))
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add(
        libraries.elements.map { classpath ->
            "-Xfriend-paths=" + classpath
                .map { it.asFile }
                .joinToString(",")
        }
    )
}
