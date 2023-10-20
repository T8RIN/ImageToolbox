package ru.tech.imageresizershrinker.domain.image.draw

import ru.tech.imageresizershrinker.domain.model.IntegerSize
import kotlin.math.min

@JvmInline
value class Pt(val value: Float) {
    fun toPx(
        size: IntegerSize
    ): Float = min(size.width * (value / 500), size.height * (value / 500))
}

inline val Float.pt: Pt get() = Pt(this)
inline val Int.pt: Pt get() = Pt(this.toFloat())