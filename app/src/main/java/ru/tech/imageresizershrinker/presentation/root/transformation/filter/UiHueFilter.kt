package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiHueFilter(
    override val value: Float = 90f,
) : UiFilter<Float>(
    title = R.string.hue,
    value = value,
    valueRange = 0f..255f
), Filter.Hue<Bitmap>