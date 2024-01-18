import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.t8rin.imagetoolbox.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.agp.gradle)
    compileOnly(libs.kotlin.gradle)
    compileOnly(libs.detekt.gradle)
}

gradlePlugin {
    // register the convention plugin
    plugins {
        register("imageToolboxLibrary") {
            id = "image.toolbox.library"
            implementationClass = "ImageToolboxLibraryPlugin"
        }
        register("imageToolboxLibraryHilt") {
            id = "image.toolbox.hilt"
            implementationClass = "ImageToolboxLibraryHiltPlugin"
        }
        register("imageToolboxLibraryFeature") {
            id = "image.toolbox.feature"
            implementationClass = "ImageToolboxLibraryFeaturePlugin"
        }
        register("imageToolboxLibraryComposePlugin") {
            id = "image.toolbox.compose"
            implementationClass = "ImageToolboxLibraryComposePlugin"
        }
    }
}