package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiSolarizeFilter(
    override val value: Float = 0.5f,
) : UiFilter<Float>(
    title = R.string.solarize,
    value = value,
    valueRange = 0f..1f
), Filter.Solarize<Bitmap>