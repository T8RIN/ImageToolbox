package ru.tech.imageresizershrinker.core.data.image.filters


import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLuminanceThresholdFilter
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter


class LuminanceThresholdFilter(
    private val context: Context,
    override val value: Float = 0.5f,
) : GPUFilterTransformation(context), Filter.LuminanceThreshold<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageLuminanceThresholdFilter(value)
}