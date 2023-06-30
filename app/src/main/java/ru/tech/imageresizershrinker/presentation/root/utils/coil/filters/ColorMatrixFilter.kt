package ru.tech.imageresizershrinker.presentation.root.utils.coil.filters


import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorMatrixFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class ColorMatrixFilter(
    private val context: @RawValue Context,
    override val value: FloatArray = floatArrayOf(
        1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f
    ),
) : FilterTransformation<FloatArray>(
    context = context,
    title = R.string.color_matrix,
    value = value,
    valueRange = 4f..4f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageColorMatrixFilter(1f, value)
}