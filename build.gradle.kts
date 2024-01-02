buildscript {
    rootProject.extra.apply {
        set("javaCompile", JavaVersion.VERSION_17)
    }

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.kotlinSerializationGradlePlugin)
        classpath(libs.kspGradlePlugin)
        classpath(libs.androidBuildTools)
        classpath(libs.kotlinGradlePlugin)
        classpath(libs.hiltGradlePlugin)
        classpath(libs.gmsGradlePlugin)
        classpath(libs.firebase.crashlytics.gradle)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
