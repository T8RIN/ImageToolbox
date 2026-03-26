package io.github.alexzhirkevich.qrose.options

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter

object EmptyPainter : Painter() {
    override val intrinsicSize: Size
        get() = Size.Zero

    override fun DrawScope.onDraw() {
    }

}