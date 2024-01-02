package ru.tech.imageresizershrinker.coreui.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiVibranceFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.vibrance,
    value = value,
    valueRange = -1f..1f
), Filter.Vibrance<Bitmap>