package com.t8rin.drawbox.presentation.model

import android.graphics.Color

data class PaintOptions(
    val color: Int = Color.BLACK,
    val strokeWidth: Float = 20f,
    val alpha: Int = 255,
    val isEraserOn: Boolean = false
)
