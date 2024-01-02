package ru.tech.imageresizershrinker.data.image.filters

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class HueFilter(
    private val context: Context,
    override val value: Float = 90f,
) : GPUFilterTransformation(context), Filter.Hue<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageHueFilter(value)
}