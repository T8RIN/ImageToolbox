buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.kotlinx.serialization.gradle)
        classpath(libs.ksp.gradle)
        classpath(libs.agp.gradle)
        classpath(libs.kotlin.gradle)
        classpath(libs.hilt.gradle)
        classpath(libs.gms.gradle)
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.baselineprofile.gradle)
        classpath(libs.detekt.gradle)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
