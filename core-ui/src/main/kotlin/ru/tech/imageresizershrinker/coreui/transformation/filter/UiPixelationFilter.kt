package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiPixelationFilter(
    override val value: Float = 25f,
) : UiFilter<Float>(
    title = R.string.pixelation,
    value = value,
    valueRange = 5f..50f
), Filter.Pixelation<Bitmap>