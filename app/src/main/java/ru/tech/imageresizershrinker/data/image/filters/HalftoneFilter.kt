package ru.tech.imageresizershrinker.data.image.filters

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHalftoneFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class HalftoneFilter(
    private val context: Context,
    override val value: Float = 0.005f,
) : GPUFilterTransformation(context), Filter.Halftone<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageHalftoneFilter(value)
}