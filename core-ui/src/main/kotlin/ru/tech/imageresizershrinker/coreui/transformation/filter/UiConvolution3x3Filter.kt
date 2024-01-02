package ru.tech.imageresizershrinker.coreui.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


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