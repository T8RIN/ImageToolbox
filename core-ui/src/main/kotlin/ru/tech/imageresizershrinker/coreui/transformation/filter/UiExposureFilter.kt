package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiExposureFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.exposure,
    value = value,
    valueRange = -10f..10f
), Filter.Exposure<Bitmap>