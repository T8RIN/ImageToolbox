package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiSolarizeFilter(
    override val value: Float = 0.5f,
) : UiFilter<Float>(
    title = R.string.solarize,
    value = value,
    valueRange = 0f..1f
), Filter.Solarize<Bitmap>