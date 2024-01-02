package ru.tech.imageresizershrinker.coredata.image.filters

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSolarizeFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class SolarizeFilter(
    private val context: Context,
    override val value: Float = 0.5f,
) : GPUFilterTransformation(context), Filter.Solarize<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSolarizeFilter(value)
}