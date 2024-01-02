package ru.tech.imageresizershrinker.coreui.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiColorMatrixFilter(
    override val value: FloatArray = floatArrayOf(
        1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f
    ),
) : UiFilter<FloatArray>(
    title = R.string.color_matrix,
    value = value,
    valueRange = 4f..4f
), Filter.ColorMatrix<Bitmap>