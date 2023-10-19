package ru.tech.imageresizershrinker.presentation.erase_background_screen.components

import android.content.res.Resources
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import ru.tech.imageresizershrinker.domain.model.IntegerSize
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawMode
import kotlin.math.min

data class PathPaint(
    val path: Path,
    val strokeWidth: Pt,
    val brushSoftness: Pt,
    val drawColor: Color = Color.Transparent,
    val isErasing: Boolean,
    val drawMode: DrawMode = DrawMode.Pen,
    val canvasSize: IntegerSize
)

private val dm get() = Resources.getSystem().displayMetrics

@JvmInline
value class Pt(val value: Float) {
    fun toPx(
        size: IntegerSize
    ): Float = min(size.width * (value / 500), size.height * (value / 500))
}

val PtSaver: Saver<Pt, Float> = Saver(
    save = {
        it.value
    },
    restore = {
        it.pt
    }
)

inline val Float.pt: Pt get() = Pt(this)

inline val Int.pt: Pt get() = Pt(this.toFloat())