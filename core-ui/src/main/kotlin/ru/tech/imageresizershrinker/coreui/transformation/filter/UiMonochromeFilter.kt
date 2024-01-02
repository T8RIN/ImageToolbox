package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiMonochromeFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.monochrome,
    value = value,
    valueRange = 0f..1f
), Filter.Monochrome<Bitmap>