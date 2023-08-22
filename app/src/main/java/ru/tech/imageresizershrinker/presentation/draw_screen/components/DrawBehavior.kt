package ru.tech.imageresizershrinker.presentation.draw_screen.components

import android.content.pm.ActivityInfo
import androidx.compose.ui.graphics.Color

sealed class DrawBehavior(
    open val orientation: Int
) {
    data object None : DrawBehavior(ActivityInfo.SCREEN_ORIENTATION_USER)

    class Image(
        override val orientation: Int
    ) : DrawBehavior(orientation = orientation)

    class Background(
        override val orientation: Int,
        val width: Int,
        val height: Int,
        val color: Color
    ) : DrawBehavior(orientation = orientation)
}