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
    alias(libs.plugins.image.toolbox.feature)
    alias(libs.plugins.image.toolbox.hilt)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "com.t8rin.imagetoolbox.feature.filters"

dependencies {
    api(projects.core.filters)
    ksp(projects.core.ksp)
    implementation(projects.core.ksp)
    implementation(projects.feature.draw)
    implementation(projects.feature.pickColor)
    implementation(projects.feature.compare)
    implementation(libs.kotlin.reflect)
    implementation(libs.aire)
    implementation(libs.trickle)
    implementation(libs.toolbox.gpuimage)
    implementation(libs.toolbox.opencvTools)
    implementation(libs.toolbox.neuralTools)
    implementation(libs.toolbox.curves)
    implementation(libs.toolbox.jhlabs)
    implementation(libs.toolbox.ascii)
}