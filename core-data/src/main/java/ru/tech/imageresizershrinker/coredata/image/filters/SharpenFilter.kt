package ru.tech.imageresizershrinker.coredata.image.filters

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class SharpenFilter(
    private val context: Context,
    override val value: Float = 0f,
) : GPUFilterTransformation(context), Filter.Sharpen<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSharpenFilter(value)
}