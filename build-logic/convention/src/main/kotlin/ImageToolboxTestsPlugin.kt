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

import com.android.build.api.dsl.LibraryExtension
import com.t8rin.imagetoolbox.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

@Suppress("UNUSED")
class ImageToolboxTestsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<LibraryExtension> {
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                if (path == ":core:data") testFixtures.enable = true
            }

            dependencies {
                add("testImplementation", libs.junit)
                add("testImplementation", libs.kotlin.test)
                add("androidTestImplementation", libs.junit)
                add("androidTestImplementation", libs.androidx.test.ext.junit)
                add("androidTestImplementation", libs.androidx.runner)
                add("androidTestImplementation", libs.compose.ui.test.junit4)

                when (path) {
                    ":core:data" -> {
                        add("androidTestImplementation", project(":core:ui"))
                        add("testFixturesApi", project(":core:domain"))
                        add("testFixturesImplementation", project(":core:settings"))
                        add("testFixturesImplementation", libs.dagger.hilt.android)
                        add("testFixturesImplementation", libs.toolbox.exif)
                    }

                    ":feature:ai-tools", ":feature:media-picker" -> {
                        add("androidTestImplementation", testFixtures(project(":core:data")))
                    }

                    ":feature:assistant" -> add("androidTestImplementation", libs.onnx.genai)
                    ":feature:cipher" -> add("androidTestImplementation", libs.bouncycastle.pkix)
                    ":feature:edit-exif", ":feature:screenshot-framing" -> {
                        add("androidTestImplementation", libs.toolbox.exif)
                        add("androidTestImplementation", testFixtures(project(":core:data")))
                    }

                    ":feature:markup-layers" -> add("androidTestImplementation", libs.moshi)
                }
            }
        }
    }
}
