package ru.tech.imageresizershrinker.main_screen.components

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class Screen : Parcelable {
    object Main : Screen()
    object SingleResize : Screen()
    object BatchResize : Screen()
    object PickColorFromImage : Screen()
    object GeneratePalette : Screen()
    object Crop : Screen()
    object ResizeByBytes : Screen()
}