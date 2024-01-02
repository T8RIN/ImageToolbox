package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiGammaFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.gamma,
    value = value,
    valueRange = 0f..3f
), Filter.Gamma<Bitmap>