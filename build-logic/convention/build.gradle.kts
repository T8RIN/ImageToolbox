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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.t8rin.imagetoolbox.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.agp.gradle)
    compileOnly(libs.kotlin.gradle)
    compileOnly(libs.detekt.gradle)
}

gradlePlugin {
    // register the convention plugin
    plugins {
        register("imageToolboxLibrary") {
            id = "image.toolbox.library"
            implementationClass = "ImageToolboxLibraryPlugin"
        }
        register("imageToolboxLibraryHilt") {
            id = "image.toolbox.hilt"
            implementationClass = "ImageToolboxLibraryHiltPlugin"
        }
        register("imageToolboxLibraryFeature") {
            id = "image.toolbox.feature"
            implementationClass = "ImageToolboxLibraryFeaturePlugin"
        }
        register("imageToolboxLibraryComposePlugin") {
            id = "image.toolbox.compose"
            implementationClass = "ImageToolboxLibraryComposePlugin"
        }
    }
}