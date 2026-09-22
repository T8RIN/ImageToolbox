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

import org.gradle.api.artifacts.ProjectDependency
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.plugin.compose")
}

val testedProjects = listOf(
    "core/data",
    "feature/apng-tools",
    "feature/cipher",
    "feature/code-preview",
    "feature/draw",
    "feature/filters",
    "feature/fractal-generation",
    "feature/gradient-maker",
    "feature/image-stitch",
    "feature/jxl-tools",
    "feature/markup-layers",
    "feature/multi-frame-fusion",
    "feature/pdf-tools",
    "feature/texture-generation",
    "feature/webp-tools",
    "lib/archive",
    "lib/collages",
    "lib/curves"
)

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
    }

    flavorDimensions += "app"
    productFlavors {
        create("foss") { dimension = "app" }
        create("market") { dimension = "app" }
    }

    sourceSets.named("main") {
        kotlin.directories.addAll(
            testedProjects.flatMap { projectPath ->
                listOf(
                    rootProject.file("$projectPath/src/androidTest/java").absolutePath,
                    rootProject.file("$projectPath/src/androidTest/kotlin").absolutePath
                )
            }
        )
        assets.directories.addAll(
            testedProjects.map { projectPath ->
                rootProject.file("$projectPath/src/androidTest/assets").absolutePath
            }
        )
    }

    targetProjectPath = ":app"
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
    implementation(libs.moshi)
    implementation(libs.bouncycastle.provider)
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
