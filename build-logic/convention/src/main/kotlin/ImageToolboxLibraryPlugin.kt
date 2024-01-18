import com.t8rin.imagetoolbox.configureDetekt
import com.t8rin.imagetoolbox.configureKotlinAndroid
import com.t8rin.imagetoolbox.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType


class ImageToolboxLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("kotlin-parcelize")
            }

            pluginManager.apply(
                libs.findLibrary("detekt-gradle").get().get().group.toString()
            )
            configureDetekt(extensions.getByType<DetektExtension>())

            extensions.configure<com.android.build.api.dsl.LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.minSdk =
                    libs.findVersion("androidMinSdk").get().toString().toIntOrNull()
            }

            dependencies {
                "implementation"(libs.findLibrary("androidxCore").get())
            }
        }
    }
}