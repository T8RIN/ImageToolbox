package ru.tech.imageresizershrinker.coreui.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiOpacityFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.opacity,
    value = value,
    valueRange = 0f..1f
), Filter.Opacity<Bitmap>