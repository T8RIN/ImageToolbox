package ru.tech.imageresizershrinker.coreui.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiContrastFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.contrast,
    value = value,
    valueRange = 0f..4f
), Filter.Contrast<Bitmap>