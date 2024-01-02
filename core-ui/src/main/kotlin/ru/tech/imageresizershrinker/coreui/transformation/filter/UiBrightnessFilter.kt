package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiBrightnessFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.brightness,
    value = value,
    valueRange = (-1f)..1f
), Filter.Brightness<Bitmap>