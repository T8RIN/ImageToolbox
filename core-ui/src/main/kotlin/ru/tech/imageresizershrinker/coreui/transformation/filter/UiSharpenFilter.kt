package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiSharpenFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.sharpen,
    value = value,
    valueRange = -4f..4f
), Filter.Sharpen<Bitmap>