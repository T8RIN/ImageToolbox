plugins {
    alias(libs.plugins.image.toolbox.library)
}

android {
    namespace = "jp.co.cyberagent.android.gpuimage"
    defaultConfig {
        ndk.abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
    }
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            ndkVersion = "26.1.10909125"
        }
    }
}