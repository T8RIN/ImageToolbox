package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiHueFilter(
    override val value: Float = 90f,
) : UiFilter<Float>(
    title = R.string.hue,
    value = value,
    valueRange = 0f..255f
), Filter.Hue<Bitmap>