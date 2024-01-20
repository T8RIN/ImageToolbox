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
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "ru.tech.imageresizershrinker.core.ui"

dependencies {
    api(libs.reimagined)
    api(libs.reimagined.hilt)

    api(libs.androidx.documentfile)

    //AndroidX
    api(libs.activityCompose)
    api(libs.splashScreen)
    api(libs.androidx.exifinterface)
    api(libs.appCompat)
    api(libs.androidx.lifecycle.viewmodel.compose)

    //Konfetti
    api(libs.konfetti.compose)

    //Coil
    api(libs.coil)
    api(libs.coilCompose)
    api(libs.coilGif)
    api(libs.coilSvg)

    //Modules
    api(projects.libs.cropper)
    api(projects.libs.dynamicTheme)
    api(projects.libs.colordetector)
    api(projects.libs.gesture)
    api(projects.libs.beforeafter)
    api(projects.libs.modalsheet)
    api(projects.libs.gpuimage)
    api(projects.libs.screenshot)
    api(projects.libs.systemuicontroller)
    api(projects.libs.placeholder)
    api(projects.libs.logger)
    api(projects.libs.zoomable)
    api(projects.libs.colorpicker) {
        exclude("com.github.SmartToolFactory", "Compose-Color-Detector")
    }
    api(projects.core.domain)

    api(libs.reorderable)

    api(libs.compose)
    api(libs.shadowsPlus)

    api(libs.kotlinx.collections.immutable)

    api(libs.fadingEdges)
    api(libs.scrollbar)

    implementation(libs.datastore.preferences.android)

    "marketImplementation"(libs.mlkit.segmentation.selfie)
    "marketImplementation"(libs.firebase.crashlytics.ktx)
    "marketImplementation"(libs.firebase.analytics.ktx)
    "marketImplementation"(libs.review.ktx)
    "marketImplementation"(libs.app.update)
    "marketImplementation"(libs.app.update.ktx)

    api(projects.core.resources)
}