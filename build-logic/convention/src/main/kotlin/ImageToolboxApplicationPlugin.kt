/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

import com.android.build.api.dsl.ApplicationExtension
import com.t8rin.imagetoolbox.configureCompose
import com.t8rin.imagetoolbox.configureDetekt
import com.t8rin.imagetoolbox.configureKotlinAndroid
import com.t8rin.imagetoolbox.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

@Suppress("UNUSED")
class ImageToolboxApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")
            apply(plugin = "kotlin-parcelize")
            apply(plugin = "com.google.gms.google-services")
            apply(plugin = "com.google.firebase.crashlytics")
            apply(plugin = "com.mikepenz.aboutlibraries.plugin")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")
            apply(plugin = "io.gitlab.arturbosch.detekt")

            configureDetekt(extensions.getByType<DetektExtension>())

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(
                    commonExtension = this,
                    createFlavors = false
                )
                defaultConfig.targetSdk =
                    libs.findVersion("androidTargetSdk").get().toString().toIntOrNull()
            }

            dependencies {
                "implementation"(libs.findLibrary("androidxCore").get())
                "implementation"(project(":core:data"))
                "implementation"(project(":core:ui"))
                "implementation"(project(":core:domain"))
                "implementation"(project(":core:resources"))
                "implementation"(project(":core:settings"))
                "implementation"(project(":core:di"))
                "implementation"(project(":core:crash"))
            }

            configureCompose(extensions.getByType<ApplicationExtension>())
        }
    }
}