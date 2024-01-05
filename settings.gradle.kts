@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}
rootProject.name = "Image Toolbox"
include(":app")
include(":cropper")
include(":dynamic_theme")
include(":colordetector")
include(":gesture")
include(":beforeafter")
include(":image")
include(":screenshot")
include(":modalsheet")
include(":gpuimage")
include(":colorpicker")
include(":systemuicontroller")
include(":placeholder")
include(":logger")
include(":feature:pick-color")
include(":feature:image-stitch")
include(":core:filters")
include(":feature:filters")
include(":feature:draw")
include(":feature:erase-background")
include(":feature:single-edit")
include(":feature:pdf-tools")
include(":feature:resize-convert")
include(":feature:generate-palette")
include(":feature:delete-exif")
include(":feature:compare")
include(":feature:bytes-resize")
include(":feature:image-preview")
include(":feature:cipher")
include(":feature:limits-resize")
include(":feature:crop")
include(":feature:load-net-image")
include(":core-data")
include(":core-domain")
include(":core-ui")
include(":feature:main")
include(":core:resources")