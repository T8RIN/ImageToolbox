import com.t8rin.imagetoolbox.configureKotlinAndroid
import com.t8rin.imagetoolbox.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies


class ImageToolboxLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("kotlin-parcelize")
            }

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