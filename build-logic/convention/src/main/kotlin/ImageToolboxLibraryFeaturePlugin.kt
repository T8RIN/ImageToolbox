import com.t8rin.imagetoolbox.configureDetekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType


class ImageToolboxLibraryFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configureDetekt(extensions.getByType<DetektExtension>())
            dependencies {
                "implementation"(project(":core:data"))
                "implementation"(project(":core:ui"))
                "implementation"(project(":core:domain"))
                "implementation"(project(":core:resources"))
            }
        }
    }
}