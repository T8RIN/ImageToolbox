package ru.tech.imageresizershrinker.presentation.root.model.transformation.filter


import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImage3x3ConvolutionFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class Convolution3x3Filter(
    private val context: @RawValue Context,
    override val value: FloatArray = floatArrayOf(
        0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f
    ),
) : FilterTransformation<FloatArray>(
    context = context,
    title = R.string.convolution3x3,
    value = value,
    valueRange = 3f..3f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImage3x3ConvolutionFilter(value)
}