package ru.tech.imageresizershrinker.core.filters.presentation.model


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter


class UiColorBalanceFilter(
    override val value: FloatArray = floatArrayOf(
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f
    ),
) : UiFilter<FloatArray>(
    title = R.string.color_balance,
    value = value,
    valueRange = 3f..3f
), Filter.ColorBalance<Bitmap>