package com.smarttoolfactory.cropper.settings

import androidx.compose.ui.graphics.Path

// TODO Find VectorDrawables as paths(or library to convert to) to add more
object Paths {
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

    val Clover = Path().apply {
        moveTo(12f, 100f)
        cubicTo(12f, 76f, 0f, 77.6142f, 0f, 50f)
        cubicTo(0f, 22.3858f, 22.3858f, 0f, 50f, 0f)
        cubicTo(77.6142f, 0f, 76f, 12f, 100f, 12f)
        cubicTo(124f, 12f, 122.3858f, 0f, 150f, 0f)
        cubicTo(177.6142f, 0f, 200f, 22.3858f, 200f, 50f)
        cubicTo(200f, 77.6142f, 188f, 76f, 188f, 100f)
        cubicTo(188f, 124f, 200f, 122.3858f, 200f, 150f)
        cubicTo(200f, 177.6142f, 177.6142f, 200f, 150f, 200f)
        cubicTo(122.3858f, 200f, 124f, 188f, 100f, 188f)
        cubicTo(76f, 188f, 77.6142f, 200f, 50f, 200f)
        cubicTo(22.3858f, 200f, 0f, 177.6142f, 0f, 150f)
        cubicTo(0f, 122.3858f, 12f, 124f, 12f, 100f)
        close()
    }
}

