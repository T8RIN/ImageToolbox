package ru.tech.imageresizershrinker.main_screen.components

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class Screen : Parcelable {
    object Main : Screen()
    object SingleResize : Screen()
    object BatchResize : Screen()

    @Parcelize
    class PickColorFromImage(
        val uri: Uri? = null
    ) : Screen()

    @Parcelize
    class GeneratePalette(
        val uri: Uri? = null
    ) : Screen()

    object Crop : Screen()
}