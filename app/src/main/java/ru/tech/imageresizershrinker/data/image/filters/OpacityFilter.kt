package ru.tech.imageresizershrinker.data.image.filters


import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageOpacityFilter
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class OpacityFilter(
    private val context: Context,
    override val value: Float = 1f,
) : GPUFilterTransformation(context), Filter.Opacity<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageOpacityFilter(value)
}