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
import com.t8rin.imagetoolbox.core
import com.t8rin.imagetoolbox.crash
import com.t8rin.imagetoolbox.data
import com.t8rin.imagetoolbox.di
import com.t8rin.imagetoolbox.domain
import com.t8rin.imagetoolbox.implementation
import com.t8rin.imagetoolbox.libs
import com.t8rin.imagetoolbox.projects
import com.t8rin.imagetoolbox.resources
import com.t8rin.imagetoolbox.settings
import com.t8rin.imagetoolbox.ui
import com.t8rin.imagetoolbox.utils
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
            apply(plugin = "com.mikepenz.aboutlibraries.plugin.android")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")
            apply(plugin = "io.gitlab.arturbosch.detekt")

            configureDetekt(extensions.getByType<DetektExtension>())

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(
                    commonExtension = this,
                    createFlavors = false
                )
                defaultConfig.targetSdk = libs.versions.androidTargetSdk.get().toIntOrNull()
            }

            dependencies {
                implementation(libs.androidxCore)
                implementation(projects.core.data)
                implementation(projects.core.ui)
                implementation(projects.core.domain)
                implementation(projects.core.resources)
                implementation(projects.core.settings)
                implementation(projects.core.di)
                implementation(projects.core.crash)
                implementation(projects.core.utils)
            }

            configureCompose(extensions.getByType<ApplicationExtension>())
        }
    }
}