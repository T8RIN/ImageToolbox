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

import com.t8rin.imagetoolbox.implementation

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

android.namespace = "com.t8rin.imagetoolbox.core.data"

dependencies {
    api(libs.coil)
    api(libs.coilNetwork)
    api(libs.ktor)
    api(libs.ktor.logging)
    implementation(libs.coilGif)
    implementation(libs.coilSvg)
    implementation(libs.trickle)

    implementation(libs.androidx.compose.ui.graphics)

    api(libs.datastore.preferences.android)

    implementation(libs.avif.coder.coil) {
        exclude(module = "com.github.awxkee:avif-coder")
    }
    implementation(libs.avif.coder)
    implementation(libs.jxl.coder.coil) {
        exclude(module = "com.github.awxkee:jxl-coder")
    }
    implementation(libs.jxl.coder)

    implementation(libs.aire)
    implementation(libs.jpegli.coder)

    implementation(libs.moshi)
    implementation(libs.moshi.adapters)

    api(libs.androidx.documentfile)

    api(libs.toolbox.logger)

    implementation(libs.toolbox.gifConverter)
    implementation(libs.toolbox.exif)
    implementation(libs.toolbox.tiffDecoder)
    implementation(libs.toolbox.qoiCoder)
    implementation(libs.toolbox.jp2decoder)
    implementation(libs.toolbox.awebp)
    implementation(libs.toolbox.psd)
    implementation(libs.toolbox.apng)
    implementation(libs.toolbox.djvuCoder)
    implementation(libs.trickle)

    implementation(projects.core.domain)
    implementation(projects.core.resources)

    implementation(projects.core.filters)

    implementation(projects.core.settings)
    implementation(projects.core.di)
    api(projects.core.utils)
}
