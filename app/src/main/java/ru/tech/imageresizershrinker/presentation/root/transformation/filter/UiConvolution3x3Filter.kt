package ru.tech.imageresizershrinker.presentation.root.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiConvolution3x3Filter(
    override val value: FloatArray = floatArrayOf(
        0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f
    ),
) : UiFilter<FloatArray>(
    title = R.string.convolution3x3,
    value = value,
    valueRange = 3f..3f
), Filter.Convolution3x3<Bitmap>