package com.smarttoolfactory.cropper.settings

import androidx.compose.ui.graphics.Path

// TODO Find VectorDrawables as paths(or library to convert to) to add more
object  Paths {
    val Favorite
        get() = Path().apply {
            moveTo(12.0f, 21.35f)
            relativeLineTo(-1.45f, -1.32f)
            cubicTo(5.4f, 15.36f, 2.0f, 12.28f, 2.0f, 8.5f)
            cubicTo(2.0f, 5.42f, 4.42f, 3.0f, 7.5f, 3.0f)
            relativeCubicTo(1.74f, 0.0f, 3.41f, 0.81f, 4.5f, 2.09f)
            cubicTo(13.09f, 3.81f, 14.76f, 3.0f, 16.5f, 3.0f)
            cubicTo(19.58f, 3.0f, 22.0f, 5.42f, 22.0f, 8.5f)
            relativeCubicTo(0.0f, 3.78f, -3.4f, 6.86f, -8.55f, 11.54f)
            lineTo(12.0f, 21.35f)
            close()
        }

    val Star = Path().apply {
        moveTo(12.0f, 17.27f)
        lineTo(18.18f, 21.0f)
        relativeLineTo(-1.64f, -7.03f)
        lineTo(22.0f, 9.24f)
        relativeLineTo(-7.19f, -0.61f)
        lineTo(12.0f, 2.0f)
        lineTo(9.19f, 8.63f)
        lineTo(2.0f, 9.24f)
        relativeLineTo(5.46f, 4.73f)
        lineTo(5.82f, 21.0f)
        close()
    }
}

