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

/** Added if needed to regenerate module graph

import dev.iurysouza.modulegraph.Theme

plugins {
    alias(libs.plugins.dev.iurysouza.modulegraph) apply true
}

moduleGraphConfig {
readmePath.set("./ARCHITECTURE.md")
heading = "# 📐 Modules Graph"
theme.set(
Theme.BASE(
mapOf(
"primaryColor" to "#00381a",
"primaryTextColor" to "#d4fcb1",
"primaryBorderColor" to "#14b800",
"lineColor" to "#15c400",
"secondaryColor" to "#283b26",
"tertiaryColor" to "#355238",
"nodeTextColor" to "#e0ffd6",
"edgeLabelBackground" to "#1a1a1a",
"fontSize" to "28px"
)
)
)
}

 **/

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }

    dependencies {
        classpath(libs.kotlinx.serialization.gradle)
        classpath(libs.ksp.gradle)
        classpath(libs.agp.gradle)
        classpath(libs.kotlin.gradle)
        classpath(libs.hilt.gradle)
        classpath(libs.gms.gradle)
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.baselineprofile.gradle)
        classpath(libs.detekt.gradle)
        classpath(libs.aboutlibraries.gradle)
        classpath(libs.compose.compiler.gradle)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}