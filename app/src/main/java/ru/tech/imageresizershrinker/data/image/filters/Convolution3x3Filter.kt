package ru.tech.imageresizershrinker.data.image.filters


import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImage3x3ConvolutionFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class Convolution3x3Filter(
    private val context: Context,
    override val value: FloatArray = floatArrayOf(
        0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f
    ),
) : GPUFilterTransformation(context), Filter.Convolution3x3<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImage3x3ConvolutionFilter(value)
}