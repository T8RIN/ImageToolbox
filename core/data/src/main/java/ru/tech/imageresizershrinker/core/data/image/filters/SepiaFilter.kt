package ru.tech.imageresizershrinker.core.data.image.filters


import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter


class SepiaFilter(
    private val context: Context,
    override val value: Float = 0f,
) : GPUFilterTransformation(context), Filter.Sepia<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSepiaToneFilter(value)
}