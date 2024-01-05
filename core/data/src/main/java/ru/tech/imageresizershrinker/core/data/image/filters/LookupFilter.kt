package ru.tech.imageresizershrinker.core.data.image.filters


import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLookupFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class LookupFilter(
    private val context: Context,
    override val value: Float = 0f,
) : GPUFilterTransformation(context), Filter.Lookup<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageLookupFilter(value)
}