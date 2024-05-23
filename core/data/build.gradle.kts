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

plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.hilt)
}

android.namespace = "ru.tech.imageresizershrinker.core.data"

dependencies {
    api(libs.coil)
    api(libs.coilCompose)
    api(libs.coilGif)
    api(libs.coilSvg)
    api(libs.datastore.preferences.android)

    api(libs.avif.coder.coil) {
        exclude(module = "com.github.awxkee:avif-coder")
    }
    api(libs.avif.coder)
    api(libs.jxl.coder.coil) {
        exclude(module = "com.github.awxkee:jxl-coder")
    }
    api(libs.jxl.coder)
    api(libs.aire)

    api(libs.androidx.exifinterface)
    api(libs.androidx.documentfile)

    api(libs.tesseract)

    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0-RC")

    implementation(libs.imageToolboxLibs)

    implementation(projects.core.domain)
    implementation(projects.core.resources)

    implementation(projects.core.filters)

    implementation(projects.core.settings)
    implementation(projects.core.di)
}