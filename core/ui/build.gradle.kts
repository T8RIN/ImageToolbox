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

plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.hilt)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "com.t8rin.imagetoolbox.core.ui"

dependencies {
    api(projects.core.resources)
    api(projects.core.domain)
    api(projects.core.utils)
    implementation(projects.core.di)
    implementation(projects.core.settings)

    // Navigation
    api(libs.decompose)
    api(libs.decomposeExtensions)

    //AndroidX
    api(libs.activityCompose)
    api(libs.splashScreen)
    api(libs.appCompat)
    api(libs.androidx.documentfile)

    //Konfetti
    api(libs.konfetti.compose)

    //Coil
    api(libs.coil)
    api(libs.coilCompose)
    api(libs.coilGif)
    api(libs.coilSvg)
    api(libs.coilNetwork)
    api(libs.ktor)

    //Modules
    api(libs.toolbox.uCrop)
    api(projects.lib.cropper)
    api(projects.lib.dynamicTheme)
    api(projects.lib.colors)
    api(projects.lib.gesture)
    api(projects.lib.image)
    api(projects.lib.modalsheet)
    api(libs.logger)
    api(projects.lib.zoomable)
    api(projects.lib.snowfall)
    api(libs.toolbox.histogram)

    api(libs.reorderable)

    api(libs.shadowGadgets)
    api(libs.shadowsPlus)

    api(libs.kotlinx.collections.immutable)

    api(libs.fadingEdges)
    api(libs.scrollbar)

    implementation(libs.datastore.preferences.android)
    implementation(libs.datastore.core.android)
    api(libs.material)

    "marketImplementation"(libs.firebase.crashlytics.ktx)
    "marketImplementation"(libs.firebase.analytics.ktx)
    "marketImplementation"(libs.review.ktx)
    "marketImplementation"(libs.app.update)
    "marketImplementation"(libs.app.update.ktx)

    "marketImplementation"(libs.mlkit.document.scanner)
    "fossImplementation"(projects.lib.documentscanner)

    "marketImplementation"(libs.quickie.bundled)
    "fossImplementation"(libs.quickie.foss)
    implementation(libs.zxing.core)

    implementation(projects.lib.qrose)

    implementation(libs.jsoup)

    api(libs.androidliquidglass)
    api(libs.capsule)
    api(libs.squircle.shape)

    api(libs.evaluator)

    api(libs.flinger)
}