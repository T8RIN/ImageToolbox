package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiExposureFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.exposure,
    value = value,
    valueRange = -10f..10f
), Filter.Exposure<Bitmap>