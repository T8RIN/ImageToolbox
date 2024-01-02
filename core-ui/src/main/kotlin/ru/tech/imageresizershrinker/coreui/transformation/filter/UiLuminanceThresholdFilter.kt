package ru.tech.imageresizershrinker.coreui.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiLuminanceThresholdFilter(
    override val value: Float = 0.5f,
) : UiFilter<Float>(
    title = R.string.luminance_threshold,
    value = value,
    valueRange = 0f..1f
), Filter.LuminanceThreshold<Bitmap>