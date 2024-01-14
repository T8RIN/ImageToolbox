import com.android.build.api.dsl.LibraryExtension
import com.t8rin.imagetoolbox.configureCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

@Suppress("UNUSED")
class ImageToolboxLibraryComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<LibraryExtension> {
                configureCompose(this)
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
                "implementation"(libs.findLibrary("androidx.material3").get())
                "implementation"(libs.findLibrary("androidx.material3.window.sizeclass").get())
                "implementation"(libs.findLibrary("androidx.material").get())
                "implementation"(libs.findLibrary("androidx.material.icons.extended").get())
            }
        }
    }
}
