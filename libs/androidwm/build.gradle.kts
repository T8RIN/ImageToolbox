plugins {
    alias(libs.plugins.image.toolbox.library)
}
android {
    namespace = "com.watermark.androidwm"
    defaultConfig {
        ndk.abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
    }
    externalNativeBuild {
        cmake { path("src/main/cpp/CMakeLists.txt") }
    }
}

dependencies {
    implementation(libs.appCompat)
}