package ru.tech.imageresizershrinker.coreui.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiSepiaFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.sepia,
    value = value,
    valueRange = 0f..1f
), Filter.Sepia<Bitmap>