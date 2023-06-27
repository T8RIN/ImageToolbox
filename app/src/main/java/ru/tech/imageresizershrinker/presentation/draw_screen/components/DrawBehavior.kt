package ru.tech.imageresizershrinker.presentation.draw_screen.components

import android.content.pm.ActivityInfo

sealed class DrawBehavior(
    open val orientation: Int
) {
    object None : DrawBehavior(ActivityInfo.SCREEN_ORIENTATION_USER)

    class Image(
        override val orientation: Int
    ) : DrawBehavior(orientation = orientation)

    class Background(
        override val orientation: Int,
        val width: Int,
        val height: Int,
    ) : DrawBehavior(orientation = orientation)
}