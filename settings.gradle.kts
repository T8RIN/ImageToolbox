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

include(":feature:gif-tools")


pluginManagement {
    repositories {
        includeBuild("build-logic")
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Image_Toolbox"

include(":app")

include(":libs:cropper")
include(":libs:dynamic-theme")
include(":libs:colordetector")
include(":libs:gesture")
include(":libs:beforeafter")
include(":libs:image")
include(":libs:screenshot")
include(":libs:modalsheet")
include(":libs:gpuimage")
include(":libs:colorpicker")
include(":libs:systemuicontroller")
include(":libs:placeholder")
include(":libs:logger")
include(":libs:zoomable")
include(":libs:extendedcolors")
include(":libs:androidwm")

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
include(":feature:generate-palette")
include(":feature:delete-exif")
include(":feature:compare")
include(":feature:bytes-resize")
include(":feature:image-preview")
include(":feature:cipher")
include(":feature:limits-resize")
include(":feature:crop")
include(":feature:load-net-image")
include(":feature:recognize-text")
include(":feature:watermarking")
include(":feature:gradient-maker")

include(":core:settings")
include(":core:resources")
include(":core:data")
include(":core:domain")
include(":core:ui")

include(":benchmark")