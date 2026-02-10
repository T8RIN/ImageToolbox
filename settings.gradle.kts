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

pluginManagement {
    repositories {
        includeBuild("build-logic")
        gradlePluginPortal()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven("https://jitpack.io") { name = "JitPack" }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven("https://androidx.dev/storage/compose-compiler/repository") {
            name = "Compose Compiler Snapshots"
            content { includeGroup("androidx.compose.compiler") }
        }
        mavenCentral()
        maven("https://jitpack.io") { name = "JitPack" }
        maven("https://oss.sonatype.org/content/repositories/snapshots/") {
            name = "Sonatype Snapshots"
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "ImageToolbox"

include(":app")

include(":feature:main")
include(":feature:pick-color")
include(":feature:image-stitch")
include(":core:filters")
include(":feature:filters")
include(":feature:draw")
include(":feature:erase-background")
include(":feature:single-edit")
include(":feature:pdf-tools")
include(":feature:resize-convert")
include(":feature:palette-tools")
include(":feature:delete-exif")
include(":feature:compare")
include(":feature:weight-resize")
include(":feature:image-preview")
include(":feature:cipher")
include(":feature:limits-resize")
include(":feature:crop")
include(":feature:load-net-image")
include(":feature:recognize-text")
include(":feature:watermarking")
include(":feature:gradient-maker")
include(":feature:gif-tools")
include(":feature:apng-tools")
include(":feature:zip")
include(":feature:jxl-tools")
include(":feature:media-picker")
include(":feature:quick-tiles")
include(":feature:settings")
include(":feature:easter-egg")
include(":feature:svg-maker")
include(":feature:format-conversion")
include(":feature:document-scanner")
include(":feature:scan-qr-code")
include(":feature:image-stacking")
include(":feature:image-splitting")
include(":feature:color-tools")
include(":feature:webp-tools")
include(":feature:noise-generation")
include(":feature:collage-maker")
include(":feature:libraries-info")
include(":feature:markup-layers")
include(":feature:base64-tools")
include(":feature:checksum-tools")
include(":feature:mesh-gradients")
include(":feature:edit-exif")
include(":feature:image-cutting")
include(":feature:audio-cover-extractor")
include(":feature:library-details")
include(":feature:wallpapers-export")
include(":feature:ascii-art")
include(":feature:ai-tools")
include(":feature:color-library")

include(":feature:root")

include(":core:settings")
include(":core:resources")
include(":core:data")
include(":core:domain")
include(":core:ui")
include(":core:di")
include(":core:crash")
include(":core:ksp")
include(":core:utils")

include(":benchmark")